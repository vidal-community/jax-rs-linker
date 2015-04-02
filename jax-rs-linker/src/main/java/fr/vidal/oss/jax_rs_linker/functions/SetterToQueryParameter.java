package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.model.QueryParameter;

import javax.lang.model.element.ExecutableElement;
import javax.ws.rs.QueryParam;
import com.google.common.base.Function;

public enum SetterToQueryParameter implements Function<ExecutableElement, QueryParameter> {

    SETTER_TO_QUERY_PARAMETER;

    @Override
    public QueryParameter apply(ExecutableElement executableElement) {
        return new QueryParameter(executableElement.getAnnotation(QueryParam.class).value());
    }
}
