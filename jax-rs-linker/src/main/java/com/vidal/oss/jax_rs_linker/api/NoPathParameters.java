package com.vidal.oss.jax_rs_linker.api;

public enum NoPathParameters implements PathParameters {
    UNSUPPORTED;

    @Override
    public String placeholder() {
        throw new UnsupportedOperationException();
    }
}
