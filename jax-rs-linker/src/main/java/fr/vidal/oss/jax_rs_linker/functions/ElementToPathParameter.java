package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.lang.model.element.Element;
import javax.ws.rs.PathParam;
import com.google.common.base.Function;

public enum ElementToPathParameter implements Function<Element, PathParameter> {

    ELEMENT_INTO_PATH_PARAMETER;

    @Override
    public PathParameter apply(Element input) {
        return new PathParameter(
            ClassName.valueOf(input.asType().toString()),
            input.getAnnotation(PathParam.class).value()
        );
    }

}
