package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.ContextPathHolder;
import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;
import java.lang.String;
import java.util.Arrays;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum  DevNullResourceLinker {
    INSTANCE;

    private final String contextPath = ContextPathHolder.getContextPath();

    public static DevNullResourceLinker devNullResourceLinker() {
        return INSTANCE;
    }

    public final TemplatedUrl<NoPathParameters, NoQueryParameters> self() {
        return new TemplatedUrl<NoPathParameters, NoQueryParameters>(contextPath + "/dev/null", Arrays.<PathParameter>asList(), Arrays.<QueryParameter>asList());
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }

    private static QueryParameter queryParameter(final String name) {
        return new QueryParameter(name);
    }
}
