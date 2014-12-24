package net.biville.florent.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.collect.*;
import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;
import net.biville.florent.jax_rs_linker.functions.ClassToName;
import net.biville.florent.jax_rs_linker.functions.OptionalFunctions;
import net.biville.florent.jax_rs_linker.model.ClassName;
import net.biville.florent.jax_rs_linker.model.Mapping;
import net.biville.florent.jax_rs_linker.parser.ElementParser;
import net.biville.florent.jax_rs_linker.predicates.OptionalPredicates;
import net.biville.florent.jax_rs_linker.writer.LinkerWriter;
import net.biville.florent.jax_rs_linker.writer.LinkersWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static java.lang.String.format;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;
import static net.biville.florent.jax_rs_linker.functions.JavaxElementToMappings.intoOptionalMapping;
import static net.biville.florent.jax_rs_linker.functions.MappingToClassName.INTO_CLASS_NAME;
import static net.biville.florent.jax_rs_linker.functions.TypeElementToElement.intoElement;
import static net.biville.florent.jax_rs_linker.predicates.ElementHasKind.byKind;


@AutoService(Processor.class)
public class LinkerAnnotationProcessor extends AbstractProcessor {

    public static final String GENERATED_CLASSNAME_SUFFIX = "Linker";
    private static final Set<String> SUPPORTED_ANNOTATIONS =
            FluentIterable
                    .from(Lists.<Class<?>>newArrayList(Self.class, SubResource.class))
                    .transform(ClassToName.INSTANCE)
                    .toSet();

    private ElementParser elementParser;
    private Multimap<ClassName, Mapping> elements = HashMultimap.create();

    public static ImmutableMap<String, String> processorQualifiedName() {
        return ImmutableMap.of("value", format("%s", StringLiteral.forValue(LinkerAnnotationProcessor.class.getName()).literal()));
    }

    @Override
    public Set<String> getSupportedOptions() {
        return ImmutableSet.of();
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

        Multimap<ClassName, Mapping> elements =
                FluentIterable.from(annotations)
                        .transformAndConcat(intoElement(roundEnv))
                        .filter(byKind(METHOD))
                        .transform(intoOptionalMapping(elementParser))
                        .filter(OptionalPredicates.<Mapping>byPresence())
                        .transform(OptionalFunctions.<Mapping>intoUnwrapped())
                        .filter(notNull())
                        .index(INTO_CLASS_NAME);

        this.elements.putAll(elements);
        try {
            generateLinkers(elements);
            if (roundEnv.processingOver()) {
                generateEntryPoint(this.elements.keySet());
            }
        }
        catch (IOException ioe) {
            throw propagate(ioe);
        }

        return false;
    }

    private void generateEntryPoint(Set<ClassName> classes) throws IOException {
        ClassName linkers = ClassName.valueOf("net.biville.florent.jax_rs_linker.Linkers");
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(linkers.getName());
        try (LinkersWriter writer = new LinkersWriter(new JavaWriter(sourceFile.openWriter()))) {
            writer.write(linkers, classes);
        }
    }

    private void generateLinkers(Multimap<ClassName, Mapping> elements) throws IOException {
        for (ClassName className : elements.keySet()) {
            generate(className, elements.get(className));
        }
    }

    private void generate(ClassName className, Collection<Mapping> mappings) throws IOException {
        ClassName generatedClass = className.append(GENERATED_CLASSNAME_SUFFIX);
        String generatedClassName = generatedClass.getName();
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(generatedClassName);
        try (LinkerWriter writer = new LinkerWriter(new JavaWriter(sourceFile.openWriter()))) {
            writer.write(generatedClass, mappings);
        }
    }

}
