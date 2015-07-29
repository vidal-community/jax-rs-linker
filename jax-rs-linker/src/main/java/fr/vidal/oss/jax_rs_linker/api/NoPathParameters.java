package fr.vidal.oss.jax_rs_linker.api;

import java.util.regex.Pattern;

public enum NoPathParameters implements PathParameters {
    ;

    @Override
    public String placeholder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pattern regex() {
        throw new UnsupportedOperationException();
    }
}
