package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

public enum PathParameterToName implements Function<PathParameter, String> {
    TO_NAME;

    @Override
    public String apply(PathParameter input) {
        return input.getName();
    }
}
