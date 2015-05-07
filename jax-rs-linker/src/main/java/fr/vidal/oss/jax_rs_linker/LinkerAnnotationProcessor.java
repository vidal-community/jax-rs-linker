package fr.vidal.oss.jax_rs_linker;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
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
import fr.vidal.oss.jax_rs_linker.writer.QueryParamsEnumWriter;
import fr.vidal.oss.jax_rs_linker.writer.ResourceFileWriters;

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

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import com.google.auto.service.AutoService;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;


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
    private boolean entryPointGenerated;
    private Messager messager;
    private ResourceFileWriters resourceFiles;
    private static final ClassName LINKERS_CLASSNAME = ClassName.valueOf("fr.vidal.oss.jax_rs_linker.ContextPathHolder");

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

    private void generateSources(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, Multimap<ClassName, Mapping> roundElements) throws IOException {
        if (mustGenerateLinkersSource(roundEnv, annotations, elements)) {
            generateLinkersSource();
            entryPointGenerated = true;
        } else {
            elements.putAll(roundElements);
            generateLinkerSources(roundElements);
        }
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
        new LinkersWriter(processingEnv.getFiler()).write(LINKERS_CLASSNAME);
    }

    private void generateLinkerSources(Multimap<ClassName, Mapping> elements) throws IOException {
        for (ClassName className : elements.keySet()) {
            generateLinkerClasses(className, elements.get(className), LINKERS_CLASSNAME);
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
