package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.errors.CompilationError;
import fr.vidal.oss.jax_rs_linker.functions.ClassToName;
import fr.vidal.oss.jax_rs_linker.functions.OptionalFunctions;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.parser.ElementParser;
import fr.vidal.oss.jax_rs_linker.predicates.OptionalPredicates;
import fr.vidal.oss.jax_rs_linker.visitor.ApplicationNameVisitor;
import fr.vidal.oss.jax_rs_linker.writer.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static fr.vidal.oss.jax_rs_linker.functions.JavaxElementToMappings.intoOptionalMapping;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToClassName.INTO_CLASS_NAME;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.functions.TypeElementToElement.intoElement;
import static fr.vidal.oss.jax_rs_linker.predicates.ElementHasKind.byKind;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.tools.Diagnostic.Kind.ERROR;


@AutoService(Processor.class)
public class LinkerAnnotationProcessor extends AbstractProcessor {

    public static final String GENERATED_CLASSNAME_SUFFIX = "Linker";
    public static final String GENERATED_ENUMNAME_SUFFIX = "PathParameters";
    public static final String GRAPH_OPTION = "graph";

    private static final Set<String> SUPPORTED_ANNOTATIONS =
        FluentIterable
            .from(Lists.<Class<?>>newArrayList(Self.class, SubResource.class, ExposedApplication.class))
            .transform(ClassToName.INSTANCE)
            .toSet();

    private final Multimap<ClassName, Mapping> elements = LinkedHashMultimap.create();
    private ElementParser elementParser;
    private String applicationName = "";
    private boolean entryPointGenerated;
    private Messager messager;
    private ResourceFileWriters resourceFiles;

    @Override
    public Set<String> getSupportedOptions() {
        return ImmutableSet.of(GRAPH_OPTION);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATIONS;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latest();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        resourceFiles = new ResourceFileWriters(processingEnv.getFiler());
        elementParser = new ElementParser(
            messager,
            processingEnv.getTypeUtils()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        Optional<? extends TypeElement> maybeExposedApplication = extractExposedApplication(annotations);
        if (maybeExposedApplication.isPresent()) {
            Optional<String> name = parseApplicationName(roundEnv);
            if (!name.isPresent()) {
                return false;
            }
            this.applicationName = name.get();
        }

        Multimap<ClassName, Mapping> roundElements =
            FluentIterable.from(annotations)
                .transformAndConcat(intoElement(roundEnv))
                .filter(byKind(METHOD))
                .transform(intoOptionalMapping(elementParser))
                .filter(OptionalPredicates.<Mapping>byPresence())
                .transform(OptionalFunctions.<Mapping>intoUnwrapped())
                .filter(notNull())
                .index(INTO_CLASS_NAME);

        tryGenerateSources(annotations, roundEnv, roundElements);
        tryExportGraph(roundEnv);

        return false;
    }

    private void tryGenerateSources(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, Multimap<ClassName, Mapping> roundElements) {
        try {
            generateSources(annotations, roundEnv, roundElements);
        }
        catch (IOException ioe) {
            throw propagate(ioe);
        }
    }

    private void tryExportGraph(RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() && processingEnv.getOptions().get(GRAPH_OPTION) != null) {
            try (DotFileWriter writer = new DotFileWriter(resourceFiles.writer("resources.dot"))) {
                writer.write(elements);
            }
        }
    }

    private void generateSources(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, Multimap<ClassName, Mapping> roundElements) throws IOException {
        if (mustGenerateLinkersSource(roundEnv, annotations, elements)) {
            generateLinkersSource();
            entryPointGenerated = true;
        }
        else {
            elements.putAll(roundElements);
            generateLinkerSources(roundElements);
        }
    }

    private Optional<String> parseApplicationName(RoundEnvironment roundEnv) {
        Set<? extends Element> applications = roundEnv.getElementsAnnotatedWith(ExposedApplication.class);
        if (applications.size() != 1) {
            messager.printMessage(ERROR, CompilationError.ONE_APPLICATION_ONLY.text());
            return absent();
        }
        return new ApplicationNameVisitor(messager).visit(applications.iterator().next());
    }

    private Optional<? extends TypeElement> extractExposedApplication(Set<? extends TypeElement> annotations) {
        return FluentIterable.from(annotations)
                .firstMatch(new Predicate<TypeElement>() {
                    @Override
                    public boolean apply(TypeElement typeElement) {
                        return typeElement.getQualifiedName().contentEquals(ExposedApplication.class.getCanonicalName());
                    }
                });
    }

    private boolean mustGenerateLinkersSource(RoundEnvironment roundEnv,
                                              Set<? extends TypeElement> roundElements,
                                              Multimap<ClassName, Mapping> processedElements) {

        return !entryPointGenerated
            && !roundEnv.processingOver()
            && !processedElements.isEmpty()
            && roundElements.isEmpty();
    }

    private void generateLinkersSource() throws IOException {
        if (applicationName.isEmpty()) {
            messager.printMessage(ERROR, CompilationError.NO_APPLICATION_FOUND.text());
            return;
        }

        ClassName linkers = ClassName.valueOf("fr.vidal.oss.jax_rs_linker.Linkers");
        new LinkersWriter(processingEnv.getFiler()).write(linkers, elements.keySet(), applicationName);
    }

    private void generateLinkerSources(Multimap<ClassName, Mapping> elements) throws IOException {
        for (ClassName className : elements.keySet()) {
            generateLinkerClasses(className, elements.get(className));
            generatePathParamEnums(className, elements.get(className));
        }
    }

    private void generateLinkerClasses(ClassName className, Collection<Mapping> mappings) throws IOException {
        ClassName generatedClass = className.append(GENERATED_CLASSNAME_SUFFIX);
        new LinkerWriter(processingEnv.getFiler()).write(generatedClass, mappings);
    }

    private void generatePathParamEnums(ClassName className, Collection<Mapping> mappings) throws IOException {
        if (FluentIterable.from(mappings).transformAndConcat(TO_PATH_PARAMETERS).isEmpty()) {
            return;
        }
        ClassName generatedEnum = className.append(GENERATED_ENUMNAME_SUFFIX);
        new PathParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }

}
