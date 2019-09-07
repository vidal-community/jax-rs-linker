package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;

import java.util.function.Function;
import java.util.stream.Stream;

public enum MappingToQueryParameters implements Function<Mapping, Stream<QueryParameter>> {

    TO_QUERY_PARAMETERS;

    @Override
    public Stream<QueryParameter> apply(Mapping mapping) {
        return mapping.getApi().getApiQuery().getQueryParameters().stream();
    }
}
