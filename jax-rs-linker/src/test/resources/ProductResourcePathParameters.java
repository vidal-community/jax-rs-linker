package com.vidal.oss.jax_rs_linker.parser;

import com.vidal.oss.jax_rs_linker.api.PathParameters;
import javax.annotation.Generated;


@Generated("com.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum BrandResourcePathParameters
    implements PathParameters {
    ID("id");

    private final String placeholder;

    BrandResourcePathParameters(String placeholder) {
        this.placeholder = placeholder;
    }

    public String placeholder() {
        return this.placeholder;
    }

}