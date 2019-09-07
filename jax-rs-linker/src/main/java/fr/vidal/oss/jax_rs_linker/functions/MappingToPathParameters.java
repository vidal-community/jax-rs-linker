package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import java.util.function.Function;
import java.util.stream.Stream;

public enum MappingToPathParameters implements Function<Mapping, Stream<PathParameter>> {

    TO_PATH_PARAMETERS;

    @Override
    public Stream<PathParameter> apply(Mapping mapping) {
        return mapping.getApi().getApiPath().getPathParameters().stream();
    }
}
