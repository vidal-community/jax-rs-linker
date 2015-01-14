package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.parser.ElementParser;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;

public class JavaxElementToMappings implements Function<Element, Optional<Mapping>> {

    private final ElementParser parser;

    private JavaxElementToMappings(ElementParser parser) {
        this.parser = parser;
    }

    public static Function<Element, Optional<Mapping>> intoOptionalMapping(ElementParser parser) {
        return new JavaxElementToMappings(parser);
    }

    @Nullable
    @Override
    public Optional<Mapping> apply(Element element) {
        return parser.parse(element);
    }
}
