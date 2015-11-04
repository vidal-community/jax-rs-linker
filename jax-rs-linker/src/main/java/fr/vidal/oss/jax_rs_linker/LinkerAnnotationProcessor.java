package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.functions.OptionalFunctions;
import fr.vidal.oss.jax_rs_linker.functions.ToMapKey;
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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Sets.newHashSet;
import static fr.vidal.oss.jax_rs_linker.errors.CompilationError.MISSING_SELF;
import static fr.vidal.oss.jax_rs_linker.functions.JavaxElementToMappings.intoOptionalMapping;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToClassName.INTO_CLASS_NAME;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToQueryParameters.TO_QUERY_PARAMETERS;
import static fr.vidal.oss.jax_rs_linker.functions.TypeElementToElement.intoElement;
import static fr.vidal.oss.jax_rs_linker.predicates.ElementHasKind.byKind;
import static fr.vidal.oss.jax_rs_linker.predicates.HasSelfMapping.HAS_SELF;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.tools.Diagnostic.Kind.ERROR;


@AutoService(Processor.class)
public class LinkerAnnotationProcessor extends AbstractProcessor {

    public static final String GENERATED_CLASSNAME_SUFFIX = "Linker";
    private static final ClassName CONTEXT_PATH_HOLDER = ClassName.valueOf("fr.vidal.oss.jax_rs_linker.ContextPathHolder");
    private static final String GRAPH_OPTION = "graph";

    private final Multimap<ClassName, Mapping> elements = LinkedHashMultimap.create();
    private ResourceFileWriters resourceFiles;
    private Messager messager;
    private ElementParser elementParser;

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
        resourceFiles = new ResourceFileWriters(processingEnv.getFiler());
        messager = processingEnv.getMessager();
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

        if (validateMappings(roundElements)) {
            tryGenerateSources(roundElements);
            tryExportGraph(roundEnv);
        }

        return false;
    }

    private boolean validateMappings(Multimap<ClassName, Mapping> roundElements) {
        Collection<ClassName> classesWithoutSelf = classesWithoutSelf(roundElements);
        if (!classesWithoutSelf.isEmpty()) {
            String classes = Joiner.on(System.lineSeparator() + "\t - ").join(classesWithoutSelf);
            messager.printMessage(ERROR, MISSING_SELF.format(classes));
            return false;
        }
        return true;
    }

    private void tryGenerateSources(Multimap<ClassName, Mapping> roundElements) {
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
            generateLinkerClasses(className, elements.get(className));
            generatePathParamEnums(className, elements.get(className));
            generateQueryParamEnums(className, elements.get(className));
        }
    }

    private void generateLinkerClasses(ClassName className, Collection<Mapping> mappings) throws IOException {
        ClassName generatedClass = className.append(GENERATED_CLASSNAME_SUFFIX);
        new LinkerWriter(processingEnv.getFiler()).write(generatedClass, mappings, CONTEXT_PATH_HOLDER);
    }

    private void generatePathParamEnums(ClassName className, Collection<Mapping> mappings) throws IOException {
        if (FluentIterable.from(mappings).transformAndConcat(TO_PATH_PARAMETERS).isEmpty()) {
            return;
        }
        ClassName generatedEnum = className.append("PathParameters");
        new PathParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }

    private void generateQueryParamEnums(ClassName className, Collection<Mapping> mappings) throws IOException {
        if (FluentIterable.from(mappings).transformAndConcat(TO_QUERY_PARAMETERS).isEmpty()) {
            return;
        }
        ClassName generatedEnum = className.append("QueryParameters");
        new QueryParamsEnumWriter(processingEnv.getFiler()).write(generatedEnum, mappings);
    }

    private Collection<ClassName> classesWithoutSelf(Multimap<ClassName, Mapping> roundElements) {
        return FluentIterable.from(roundElements.asMap().entrySet())
            .filter(new Predicate<Map.Entry<ClassName, Collection<Mapping>>>() {
                @Override
                public boolean apply(Map.Entry<ClassName, Collection<Mapping>> classMappings) {
                    return !FluentIterable.from(classMappings.getValue())
                        .firstMatch(HAS_SELF)
                        .isPresent();
                }
            })
            .transform(ToMapKey.<ClassName>intoKey())
            .toSortedSet(Ordering.<ClassName>natural());
    }

}
