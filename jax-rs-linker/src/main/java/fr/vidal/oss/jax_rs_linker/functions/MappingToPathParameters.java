package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.annotation.Nullable;
import java.util.Collection;

public enum MappingToPathParameters implements Function<Mapping, Collection<PathParameter>> {

    TO_PATH_PARAMETERS;

    @Nullable
    @Override
    public Collection<PathParameter> apply(@Nullable Mapping mapping) {
        return mapping.getApi().getApiPath().getPathParameters();
    }
}
