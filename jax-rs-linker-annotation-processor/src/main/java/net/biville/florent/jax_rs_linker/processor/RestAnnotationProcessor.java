package net.biville.florent.jax_rs_linker.processor;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.biville.florent.catalog.model.Self;
import net.biville.florent.catalog.model.SubResource;
import net.biville.florent.jax_rs_linker.processor.functions.JavaxElementToMappings;
import net.biville.florent.jax_rs_linker.processor.functions.TypeElementToElement;
import net.biville.florent.jax_rs_linker.processor.parser.ElementParser;
import net.biville.florent.jax_rs_linker.processor.predicates.OptionalPredicates;
import net.biville.florent.jax_rs_linker.processor.functions.ClassToName;
import net.biville.florent.jax_rs_linker.processor.functions.MappingToClassName;
import net.biville.florent.jax_rs_linker.processor.functions.OptionalFunctions;
import net.biville.florent.jax_rs_linker.processor.model.ClassName;
import net.biville.florent.jax_rs_linker.processor.model.Mapping;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static javax.lang.model.element.ElementKind.METHOD;
import static net.biville.florent.jax_rs_linker.processor.predicates.ElementHasKind.BY_KIND;

public class RestAnnotationProcessor extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATIONS =
        FluentIterable
            .from(Lists.<Class<?>>newArrayList(Self.class, SubResource.class))
            .transform(ClassToName.INSTANCE)
            .toSet();

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
        return SourceVersion.RELEASE_7;
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
                .filter(BY_KIND(METHOD))
                .transform(JavaxElementToMappings.INTO_OPTIONAL_MAPPING(elementParser, roundEnv))
                .filter(OptionalPredicates.<Mapping>BY_PRESENCE())
                .transform(OptionalFunctions.<Mapping>INTO_UNWRAPPED())
                .filter(notNull())
                .index(MappingToClassName.INTO_CLASS_NAME);

        //TODO: generate
        return false;
    }

}
