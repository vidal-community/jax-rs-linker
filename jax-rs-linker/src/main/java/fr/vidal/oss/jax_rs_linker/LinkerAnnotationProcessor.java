package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.collect.*;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.functions.ClassToName;
import fr.vidal.oss.jax_rs_linker.functions.OptionalFunctions;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.parser.ElementParser;
import fr.vidal.oss.jax_rs_linker.predicates.OptionalPredicates;
import fr.vidal.oss.jax_rs_linker.writer.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static fr.vidal.oss.jax_rs_linker.functions.JavaxElementToMappings.intoOptionalMapping;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToClassName.INTO_CLASS_NAME;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToQueryParameters.TO_QUERY_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.functions.TypeElementToElement.intoElement;
import static fr.vidal.oss.jax_rs_linker.predicates.ElementHasKind.byKind;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;


@AutoService(Processor.class)
public class LinkerAnnotationProcessor extends AbstractProcessor {

    public static final String GENERATED_CLASSNAME_SUFFIX = "Linker";
    public static final String GENERATED_PATHPARAMETERS_ENUMNAME_SUFFIX = "PathParameters";
    public static final String GENERATED_QUERYPARAMETERS_ENUMNAME_SUFFIX = "QueryParameters";
    public static final String GRAPH_OPTION = "graph";

    private static final Set<String> SUPPORTED_ANNOTATIONS =
        FluentIterable
            .from(Lists.<Class<?>>newArrayList(Self.class, SubResource.class))
            .transform(ClassToName.INSTANCE)
            .toSet();

    private final Multimap<ClassName, Mapping> elements = LinkedHashMultimap.create();
    private ElementParser elementParser;
    private ResourceFileWriters resourceFiles;
    private static final ClassName CONTEXT_PATH_HOLDER = ClassName.valueOf("fr.vidal.oss.jax_rs_linker.ContextPathHolder");

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
        resourceFiles = new ResourceFileWriters(processingEnv.getFiler());
        elementParser = new ElementParser(
            processingEnv.getMessager(),
            processingEnv.getTypeUtils()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

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
            generateSources(roundElements);
        } catch (IOException ioe) {
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

    private void generateSources(Multimap<ClassName, Mapping> roundElements) throws IOException {
        tryGenerateContextPathHolder();
        elements.putAll(roundElements);
        generateLinkerSources(roundElements);
    }

    private void tryGenerateContextPathHolder() throws IOException {
        try {
            new LinkersWriter(processingEnv.getFiler()).write(CONTEXT_PATH_HOLDER);
        } catch (FilerException ignored) {
        }
    }

    private void generateLinkerSources(Multimap<ClassName, Mapping> elements) throws IOException {
        for (ClassName className : elements.keySet()) {
            generateLinkerClasses(className, elements.get(className), CONTEXT_PATH_HOLDER);
            generatePathParamEnums(className, elements.get(className));
            generateQueryParamEnums(className, elements.get(className));
        }
    }

    private void generateLinkerClasses(ClassName className, Collection<Mapping> mappings, ClassName linkersClassname) throws IOException {
        ClassName generatedClass = className.append(GENERATED_CLASSNAME_SUFFIX);
        new LinkerWriter(processingEnv.getFiler()).write(generatedClass, mappings, linkersClassname);
    }

    private void generatePathParamEnums(ClassName className, Collection<Mapping> mappings) throws IOException {
        if (FluentIterable.from(mappings).transformAndConcat(TO_PATH_PARAMETERS).isEmpty()) {
            return;
        }
        ClassName generatedEnum = className.append(GENERATED_PATHPARAMETERS_ENUMNAME_SUFFIX);
        new PathParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }

    private void generateQueryParamEnums(ClassName className, Collection<Mapping> mappings) throws IOException {
        if (FluentIterable.from(mappings).transformAndConcat(TO_QUERY_PARAMETERS).isEmpty()) {
            return;
        }
        ClassName generatedEnum = className.append(GENERATED_QUERYPARAMETERS_ENUMNAME_SUFFIX);
        new QueryParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }
}
