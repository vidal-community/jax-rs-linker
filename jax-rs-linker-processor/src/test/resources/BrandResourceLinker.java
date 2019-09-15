
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.ContextPathHolder;
import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum  BrandResourceLinker {
    INSTANCE;

    private final String contextPath = ContextPathHolder.getContextPath();

    public static BrandResourceLinker brandResourceLinker() {
        return INSTANCE;
    }

    public final TemplatedUrl<BrandResourcePathParameters, NoQueryParameters> self() {
        return new TemplatedUrl<BrandResourcePathParameters, NoQueryParameters>(contextPath + "/brand/{id}", Arrays.<PathParameter>asList(pathParameter("int", "id")), Arrays.<QueryParameter>asList());
    }

    public final TemplatedUrl<BrandResourcePathParameters, NoQueryParameters> relatedBrandResource() {
        return new TemplatedUrl<BrandResourcePathParameters, NoQueryParameters>(contextPath + "/brand/{code}", Arrays.<PathParameter>asList(pathParameter("int", "code")), Arrays.<QueryParameter>asList());
    }

    public final TemplatedUrl<BrandResourcePathParameters, NoQueryParameters> relatedBrandResourceZip() {
        return new TemplatedUrl<BrandResourcePathParameters, NoQueryParameters>(contextPath + "/brand/{zip}", Arrays.<PathParameter>asList(pathParameter("int", "zip")), Arrays.<QueryParameter>asList());
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }

    private static QueryParameter queryParameter(final String name) {
        return new QueryParameter(name);
    }
}
