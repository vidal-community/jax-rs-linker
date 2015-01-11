package com.vidal.oss.jax_rs_linker.parser;

import javax.annotation.Generated;

@Generated("com.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum BrandResourcePathParameters {
    ID("id");

    private final String value;

    BrandResourcePathParameters(String value) {
        this.value = value;
    }

    String value() {
        return this.value;
    }
}
