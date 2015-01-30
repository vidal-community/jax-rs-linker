
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public final class ProductResourceLinker {
    private final String contextPath;

    public ProductResourceLinker() {
        this("");
    }

    public ProductResourceLinker(final String contextPath) {
        this.contextPath = contextPath;
    }

    public final TemplatedPath<ProductResourcePathParameters> self() {
        return new TemplatedPath<ProductResourcePathParameters>(contextPath + "/product/{id}", Arrays.<PathParameter>asList(pathParameter("int", "id")));
    }

    public final TemplatedPath<ProductResourcePathParameters> relatedBrandResource() {
        return new TemplatedPath<ProductResourcePathParameters>(contextPath + "/product/{id}/brand", Arrays.<PathParameter>asList(pathParameter("int", "id")));
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }
}
