
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;
import java.util.Arrays;
import javax.annotation.Generated;


@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public class ProductResourceLinker {

    private final String contextPath;

    public ProductResourceLinker() {
        this("");
    }

    public ProductResourceLinker(String contextPath) {
        this.contextPath = contextPath;
    }

    public TemplatedPath<ProductResourcePathParameters> self() {
        return new TemplatedPath<ProductResourcePathParameters>(contextPath + "/product/{id}", Arrays.<PathParameter>asList(new PathParameter(ClassName.valueOf("int"), "id")));
    }

    public TemplatedPath<ProductResourcePathParameters> relatedBrandResource() {
        ApiPath path = new ApiPath("/product/{id}/brand", Arrays.<PathParameter>asList(new PathParameter(ClassName.valueOf("int"), "id")));
        return new TemplatedPath<ProductResourcePathParameters>(contextPath + path.getPath(), path.getPathParameters());
    }

}