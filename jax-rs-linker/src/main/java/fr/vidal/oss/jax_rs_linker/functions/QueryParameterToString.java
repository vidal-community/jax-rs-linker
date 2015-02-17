package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.writer.EnumConstants;

import javax.annotation.Nullable;

public enum QueryParameterToString implements Function<QueryParameter, String>{

    TO_STRING;

    @Override
    public String apply(QueryParameter queryParameter) {
        return queryParameter.getName();
    }
}
