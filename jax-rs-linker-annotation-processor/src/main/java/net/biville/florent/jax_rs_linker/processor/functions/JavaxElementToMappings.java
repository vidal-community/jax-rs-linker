package net.biville.florent.jax_rs_linker.processor.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import net.biville.florent.jax_rs_linker.model.Mapping;
import net.biville.florent.jax_rs_linker.processor.parser.ElementParser;

import javax.annotation.Nullable;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

public class JavaxElementToMappings implements Function<Element, Optional<Mapping>> {

    private final ElementParser parser;
    private final RoundEnvironment roundEnvironment;

    private JavaxElementToMappings(ElementParser parser,
                                   RoundEnvironment roundEnvironment) {

        this.parser = parser;
        this.roundEnvironment = roundEnvironment;
    }

    public static Function<Element, Optional<Mapping>> INTO_OPTIONAL_MAPPING(ElementParser parser,
                                                                             RoundEnvironment roundEnvironment) {

        return new JavaxElementToMappings(parser, roundEnvironment);
    }

    @Nullable
    @Override
    public Optional<Mapping> apply(Element element) {
        return parser.parse(element);
    }
}
