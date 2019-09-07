package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.functions.OptionalFunctions;
import fr.vidal.oss.jax_rs_linker.model.ClassNameGeneration;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.parser.ElementParser;
import fr.vidal.oss.jax_rs_linker.parser.ResourceGraphValidator;
import fr.vidal.oss.jax_rs_linker.writer.DotFileWriter;
import fr.vidal.oss.jax_rs_linker.writer.LinkerWriter;
import fr.vidal.oss.jax_rs_linker.writer.PathParamsEnumWriter;
import fr.vidal.oss.jax_rs_linker.writer.QueryParamsEnumWriter;
import fr.vidal.oss.jax_rs_linker.writer.ResourceFileWriters;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToQueryParameters.TO_QUERY_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.predicates.ElementHasKind.byKind;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;


@AutoService(Processor.class)
public class LinkerAnnotationProcessor extends AbstractProcessor {

    private static final String GENERATED_CLASSNAME_SUFFIX = "Linker";
    private static final String GRAPH_OPTION = "graph";

    private final Multimap<ClassNameGeneration, Mapping> elements = LinkedHashMultimap.create();
    private ResourceFileWriters resourceFiles;
    private ElementParser elementParser;
    private ResourceGraphValidator validator;

    @Override
    public Set<String> getSupportedOptions() {
        return ImmutableSet.of(GRAPH_OPTION);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return newHashSet(
            Self.class.getName(),
            SubResource.class.getName()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latest();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Messager messager = processingEnv.getMessager();
        resourceFiles = new ResourceFileWriters(processingEnv.getFiler());
        validator = new ResourceGraphValidator(messager);
        elementParser = new ElementParser(
            messager,
            processingEnv.getTypeUtils()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        ImmutableListMultimap.Builder<ClassNameGeneration, Mapping> buildingRoundElements = ImmutableListMultimap.builder();
        annotations.stream()
            .flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
            .filter(byKind(METHOD))
            .map(e -> elementParser.parse(e))
            .filter(Optional::isPresent)
            .map(OptionalFunctions.intoUnwrapped())
            .filter(Objects::nonNull)
            .forEach(e -> buildingRoundElements.put(e.getJavaLocation().getClassNameGeneration(), e));

        Multimap<ClassNameGeneration, Mapping> roundElements = buildingRoundElements.build();
        if (validator.validateMappings(roundElements)) {
            tryGenerateSources(roundElements);
            tryExportGraph(roundEnv);
        }

        return false;
    }

    private void tryGenerateSources(Multimap<ClassNameGeneration, Mapping> roundElements) {
        try {
            generateSources(roundElements);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void tryExportGraph(RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() && processingEnv.getOptions().get(GRAPH_OPTION) != null) {
            try (DotFileWriter writer = new DotFileWriter(resourceFiles.writer("resources.dot"))) {
                writer.write(elements);
            }
        }
    }

    private void generateSources(Multimap<ClassNameGeneration, Mapping> roundElements) throws IOException {
        elements.putAll(roundElements);
        generateLinkerSources(roundElements);
    }

    private void generateLinkerSources(Multimap<ClassNameGeneration, Mapping> elements) throws IOException {
        for (ClassNameGeneration className : elements.keySet()) {
            generateLinkerClasses(className, elements.get(className));
            generatePathParamEnums(className, elements.get(className));
            generateQueryParamEnums(className, elements.get(className));
        }
    }

    private void generateLinkerClasses(ClassNameGeneration className, Collection<Mapping> mappings) throws IOException {
        ClassNameGeneration generatedClass = className.append(GENERATED_CLASSNAME_SUFFIX);
        new LinkerWriter(processingEnv.getFiler()).write(generatedClass, mappings);
    }

    private void generatePathParamEnums(ClassNameGeneration className, Collection<Mapping> mappings) throws IOException {
        if (!mappings.stream().flatMap(TO_PATH_PARAMETERS).findAny().isPresent()) {
            return;
        }
        ClassNameGeneration generatedEnum = className.append("PathParameters");
        new PathParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }

    private void generateQueryParamEnums(ClassNameGeneration className, Collection<Mapping> mappings) throws IOException {
        if (!mappings.stream().flatMap(TO_QUERY_PARAMETERS).findAny().isPresent()) {
            return;
        }
        ClassNameGeneration generatedEnum = className.append("QueryParameters");
        new QueryParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }
}
