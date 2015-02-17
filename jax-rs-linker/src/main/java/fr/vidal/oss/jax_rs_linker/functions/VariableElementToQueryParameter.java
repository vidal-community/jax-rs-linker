package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;

import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;

public enum VariableElementToQueryParameter implements Function<VariableElement, QueryParameter>{

    INTO_QUERY_PARAMETER;

    @Nullable
    @Override
    public QueryParameter apply(VariableElement input) {
        return new QueryParameter(ClassName.valueOf(input.getEnclosingElement().getEnclosingElement().getSimpleName().toString()), input.getSimpleName().toString());
    }

}
