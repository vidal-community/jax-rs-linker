package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.model.QueryParameter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.Types;
import javax.ws.rs.QueryParam;
import java.util.Collection;

import static fr.vidal.oss.jax_rs_linker.functions.ElementToQueryParameter.INTO_QUERY_PARAMETER;

/**
* Element visitor designed to check for QueryParam annotations on BeanParams
*/
class QueryParamElementVisitor extends SimpleElementVisitor7<Void, Void> {
    private final Collection<QueryParameter> queryParameters;
    private final Types typeUtils;

    public QueryParamElementVisitor(Collection<QueryParameter> queryParameters,
                                    Types typeUtils) {
        this.queryParameters = queryParameters;
        this.typeUtils = typeUtils;
    }

    @Override
    public Void visitVariable(VariableElement field, Void aVoid) {
        addToQueryParametersIfApplicable(queryParameters, field);
        return super.visitVariable(field, aVoid);
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        if (e.getKind().equals(ElementKind.CONSTRUCTOR)) {
            for (VariableElement ctorParameter : e.getParameters()) {
                addToQueryParametersIfApplicable(queryParameters, ctorParameter);
            }
        } else if (e.getKind().equals(ElementKind.METHOD) &&
            e.getSimpleName().toString().startsWith("set")
            && typeUtils.isSameType(e.getReturnType(),typeUtils.getNoType(TypeKind.VOID))) {
            addToQueryParametersIfApplicable(queryParameters, e);
        }
        return super.visitExecutable(e, aVoid);
    }

    private void addToQueryParametersIfApplicable(Collection<QueryParameter> queryParameters,
                                                  Element ctorParameter) {
        QueryParam queryParam = ctorParameter.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            queryParameters.add(INTO_QUERY_PARAMETER.apply(ctorParameter));
        }
    }
}
