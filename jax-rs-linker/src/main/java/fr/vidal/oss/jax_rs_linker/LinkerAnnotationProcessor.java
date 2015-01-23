package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
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
import fr.vidal.oss.jax_rs_linker.writer.DotFileWriter;
import fr.vidal.oss.jax_rs_linker.writer.LinkerWriter;
import fr.vidal.oss.jax_rs_linker.writer.LinkersWriter;
import fr.vidal.oss.jax_rs_linker.writer.PathParamsEnumWriter;
import fr.vidal.oss.jax_rs_linker.functions.MappingToClassName;
import fr.vidal.oss.jax_rs_linker.functions.TypeElementToElement;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static fr.vidal.oss.jax_rs_linker.functions.JavaxElementToMappings.intoOptionalMapping;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.predicates.ElementHasKind.byKind;
import static java.lang.String.format;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;


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

    public static ImmutableMap<String, String> processorQualifiedName() {
        return ImmutableMap.of("value", format("%s", StringLiteral.forValue(LinkerAnnotationProcessor.class.getName()).literal()));
    }

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
        Messager messager = processingEnv.getMessager();
        elementParser = new ElementParser(
            messager,
            processingEnv.getTypeUtils()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        Optional<? extends TypeElement> maybeExposedApplication = parseExposedApplication(annotations);
        if (maybeExposedApplication.isPresent()) {
            Optional<String> name = parseApplicationName(roundEnv);
            if (!name.isPresent()) {
                processingEnv.getMessager().printMessage(ERROR, CompilationError.ONE_APPLICATION_ONLY.text());
                return false;
            }
            this.applicationName = name.get();
        }

        Multimap<ClassName, Mapping> roundElements =
            FluentIterable.from(annotations)
                .transformAndConcat(TypeElementToElement.intoElement(roundEnv))
                .filter(byKind(METHOD))
                .transform(intoOptionalMapping(elementParser))
                .filter(OptionalPredicates.<Mapping>byPresence())
                .transform(OptionalFunctions.<Mapping>intoUnwrapped())
                .filter(notNull())
                .index(MappingToClassName.INTO_CLASS_NAME);

        try {
            generateSources(annotations, roundEnv, roundElements);
        }
        catch (IOException ioe) {
            throw propagate(ioe);
        }

        if (roundEnv.processingOver() && processingEnv.getOptions().get(GRAPH_OPTION) != null) {
            try (DotFileWriter writer = new DotFileWriter(resourceWriter("resources.dot"))) {
                writer.write(elements);
            }
        }
        return false;
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
            return absent();
        }
        Element application = applications.iterator().next();
        return Optional.of(application.getAnnotation(ExposedApplication.class).servletName());
    }

    private Optional<? extends TypeElement> parseExposedApplication(Set<? extends TypeElement> annotations) {
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
            processingEnv.getMessager().printMessage(ERROR, CompilationError.NO_APPLICATION_FOUND.text());
            return;
        }

        ClassName linkers = ClassName.valueOf("fr.vidal.oss.jax_rs_linker.Linkers");
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(linkers.fullyQualifiedName());
        try (LinkersWriter writer = new LinkersWriter(javaWriter(sourceFile))) {
            writer.write(linkers, elements.keySet(), applicationName);
        }
    }

    private void generateLinkerSources(Multimap<ClassName, Mapping> elements) throws IOException {
        for (ClassName className : elements.keySet()) {
            generateLinkerClasses(className, elements.get(className));
            generatePathParamEnums(className, elements.get(className));
        }
    }

    private void generateLinkerClasses(ClassName className, Collection<Mapping> mappings) throws IOException {
        ClassName generatedClass = className.append(GENERATED_CLASSNAME_SUFFIX);
        String generatedClassName = generatedClass.fullyQualifiedName();
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(generatedClassName);
        try (LinkerWriter writer = new LinkerWriter(javaWriter(sourceFile))) {
            writer.write(generatedClass, mappings);
        }
    }

    private void generatePathParamEnums(ClassName className, Collection<Mapping> mappings) throws IOException {
        if (FluentIterable.from(mappings).transformAndConcat(TO_PATH_PARAMETERS).isEmpty()) {
            return;
        }
        ClassName generatedEnum = className.append(GENERATED_ENUMNAME_SUFFIX);
        String generatedEnumName = generatedEnum.fullyQualifiedName();
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(generatedEnumName);
        try (PathParamsEnumWriter writer = new PathParamsEnumWriter(javaWriter(sourceFile))) {
            writer.write(generatedEnum, mappings);
        }
    }

    private JavaWriter javaWriter(JavaFileObject sourceFile) throws IOException {
        return new JavaWriter(new BufferedWriter(sourceFile.openWriter()));
    }

    private Writer resourceWriter(String fileName) {
        try {
            return processingEnv.getFiler().createResource(CLASS_OUTPUT, "", fileName).openWriter();
        } catch (IOException e) {
            throw propagate(e);
        }
    }

}
