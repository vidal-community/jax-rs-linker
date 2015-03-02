package fr.vidal.oss.jax_rs_linker.api;

import com.google.common.base.Optional;

import java.util.regex.Pattern;

public enum NoPathParameters implements PathParameters {
    ;

    @Override
    public String placeholder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Pattern> regex() {
        throw new UnsupportedOperationException();
    }
}
