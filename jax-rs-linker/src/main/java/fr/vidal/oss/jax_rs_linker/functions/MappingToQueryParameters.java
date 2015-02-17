package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;

import javax.annotation.Nullable;
import java.util.Collection;

public enum MappingToQueryParameters implements Function<Mapping, Collection<QueryParameter>> {

    TO_QUERY_PARAMETERS;

    @Override
    public Collection<QueryParameter> apply(@Nullable Mapping mapping) {
        return mapping.getApi().getApiQuery().getQueryParameters();
    }
}
