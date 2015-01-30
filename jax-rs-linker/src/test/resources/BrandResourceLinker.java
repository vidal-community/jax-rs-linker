
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public final class BrandResourceLinker {
    private final String contextPath;

    public BrandResourceLinker() {
        this("");
    }

    public BrandResourceLinker(final String contextPath) {
        this.contextPath = contextPath;
    }

    public final TemplatedPath<BrandResourcePathParameters> self() {
        return new TemplatedPath<BrandResourcePathParameters>(contextPath + "/brand/{id}", Arrays.<PathParameter>asList(pathParameter("int", "id")));
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }
}
