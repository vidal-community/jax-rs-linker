package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.model.QueryParameter;

import static fr.vidal.oss.jax_rs_linker.functions.ElementToQueryParameter.INTO_QUERY_PARAMETER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.Types;
import javax.ws.rs.QueryParam;

/**
* Element visitor designed to check for QueryParam annotations on BeanParams
*/
class JaxRsParamElementVisitor extends SimpleElementVisitor7<Collection<QueryParameter>, Void> {
    private final Types typeUtils;

    public JaxRsParamElementVisitor(Types typeUtils) {
        this.typeUtils = typeUtils;
    }

    @Override
    public Collection<QueryParameter> visitVariable(VariableElement field, Void aVoid) {
        QueryParameter queryParameter = parseElement(field);
        if (queryParameter == null) {
            return emptyList();
        }
        return asList(queryParameter);
    }

    @Override
    public Collection<QueryParameter> visitExecutable(ExecutableElement executableElement, Void aVoid) {
        if (isConstructor(executableElement)) {
            Collection<QueryParameter> results = new ArrayList<>();
            for (VariableElement ctorParameter : executableElement.getParameters()) {
                QueryParameter queryParameter = parseElement(ctorParameter);
                if (queryParameter != null) {
                    results.add(queryParameter);
                }
            }
            return results;
        }

        if (isSetter(executableElement)) {
            QueryParameter queryParameter = parseElement(executableElement);
            if (queryParameter == null) {
                return emptyList();
            }
            return asList(queryParameter);
        }

        return emptyList();
    }

    private boolean isConstructor(ExecutableElement executableElement) {
        return executableElement.getKind().equals(ElementKind.CONSTRUCTOR);
    }

    private boolean isSetter(ExecutableElement executableElement) {
        return executableElement.getKind().equals(ElementKind.METHOD)
            && executableElement.getSimpleName().toString().startsWith("set")
            && typeUtils.isSameType(executableElement.getReturnType(), typeUtils.getNoType(TypeKind.VOID));
    }

    private QueryParameter parseElement(Element ctorParameter) {
        QueryParam queryParam = ctorParameter.getAnnotation(QueryParam.class);
        if (queryParam == null) {
            return null;
        }
        return INTO_QUERY_PARAMETER.apply(ctorParameter);
    }
}
