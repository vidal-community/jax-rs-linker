package fr.vidal.oss.jax_rs_linker.api;

import com.google.common.base.Optional;

public enum NoPathParameters implements PathParameters {
    ;

    @Override
    public String placeholder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> regex() {
        throw new UnsupportedOperationException();
    }
}
