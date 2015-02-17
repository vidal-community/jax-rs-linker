
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;
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

    public final TemplatedUrl<ProductResourcePathParameters, NoQueryParameters> self() {
        return new TemplatedUrl<ProductResourcePathParameters, NoQueryParameters>(contextPath + "/product/{id}", Arrays.<PathParameter>asList(pathParameter("int", "id")), Arrays.<QueryParameter>asList());
    }

    public final TemplatedUrl<ProductResourcePathParameters, NoQueryParameters> relatedBrandResource() {
        return new TemplatedUrl<ProductResourcePathParameters, NoQueryParameters>(contextPath + "/product/{id}/brand", Arrays.<PathParameter>asList(pathParameter("int", "id")), Arrays.<QueryParameter>asList());
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }

    private static QueryParameter queryParameter(final String type, final String name) {
        return new QueryParameter(ClassName.valueOf(type), name);
    }
}
