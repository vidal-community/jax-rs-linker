package net.biville.florent.jax_rs_linker.functions;

import com.google.common.base.Function;
import net.biville.florent.jax_rs_linker.model.PathParameter;

public enum PathParameterToName implements Function<PathParameter, String> {
    TO_NAME;

    @Override
    public String apply(PathParameter input) {
        return input.getName();
    }
}
