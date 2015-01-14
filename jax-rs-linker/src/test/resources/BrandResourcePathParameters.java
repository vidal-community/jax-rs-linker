package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum BrandResourcePathParameters
    implements PathParameters {
    ID("id");

    private final String placeholder;

    BrandResourcePathParameters(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String placeholder() {
        return this.placeholder;
    }
}
