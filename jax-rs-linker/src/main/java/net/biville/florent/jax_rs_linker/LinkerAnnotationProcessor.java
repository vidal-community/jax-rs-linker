package net.biville.florent.jax_rs_linker;

import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.squareup.javawriter.JavaWriter;
import net.biville.florent.jax_rs_linker.functions.MappingToClassName;
import net.biville.florent.jax_rs_linker.model.ClassName;
import net.biville.florent.jax_rs_linker.model.Mapping;
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;
import net.biville.florent.jax_rs_linker.parser.ElementParser;
import net.biville.florent.jax_rs_linker.predicates.ElementHasKind;
import net.biville.florent.jax_rs_linker.functions.ClassToName;
import net.biville.florent.jax_rs_linker.functions.OptionalFunctions;
import net.biville.florent.jax_rs_linker.functions.TypeElementToElement;
import net.biville.florent.jax_rs_linker.predicates.OptionalPredicates;
import net.biville.florent.jax_rs_linker.writer.LinkerWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static javax.lang.model.SourceVersion.latest;
import static javax.lang.model.element.ElementKind.METHOD;
import static net.biville.florent.jax_rs_linker.functions.JavaxElementToMappings.INTO_OPTIONAL_MAPPING;

public class LinkerAnnotationProcessor extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATIONS =
            FluentIterable
                    .from(Lists.<Class<?>>newArrayList(Self.class, SubResource.class))
                    .transform(ClassToName.INSTANCE)
                    .toSet();

    private static final String GENERATED_CLASSNAME_SUFFIX = "Linker";

    private ElementParser elementParser;

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
        elementParser = new ElementParser(processingEnv.getMessager(), processingEnv.getTypeUtils());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        Multimap<ClassName, Mapping> elements =
                FluentIterable.from(annotations)
                        .transformAndConcat(TypeElementToElement.INTO_ELEMENT(roundEnv))
                        .filter(ElementHasKind.BY_KIND(METHOD))
                        .transform(INTO_OPTIONAL_MAPPING(elementParser))
                        .filter(OptionalPredicates.<Mapping>BY_PRESENCE())
                        .transform(OptionalFunctions.<Mapping>INTO_UNWRAPPED())
                        .filter(notNull())
                        .index(MappingToClassName.INTO_CLASS_NAME);

        try {
            generateLinkers(elements);
        }
        catch (IOException ioe) {
            throw Throwables.propagate(ioe);
        }
        return true;
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
