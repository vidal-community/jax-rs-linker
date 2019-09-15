
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
public enum PersonResourceLinker {
    INSTANCE;

    private final String contextPath = ContextPathHolder.getContextPath();

    public static PersonResourceLinker personResourceLinker() {
        return INSTANCE;
    }

    public final TemplatedUrl<PersonResourcePathParameters, PersonResourceQueryParameters> self() {
        return new TemplatedUrl<PersonResourcePathParameters, PersonResourceQueryParameters>(contextPath + "/person/{id}", Arrays.<PathParameter>asList(pathParameter("int", "id")), Arrays.<QueryParameter>asList(queryParameter("alive-flag")));
    }

    public final TemplatedUrl<PersonResourcePathParameters, NoQueryParameters> relatedPersonResource() {
        return new TemplatedUrl<PersonResourcePathParameters, NoQueryParameters>(contextPath + "/person/name/{firstName}", Arrays.<PathParameter>asList(pathParameter("java.lang.String", "firstName")), Arrays.<QueryParameter>asList());
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }

    private static QueryParameter queryParameter(final String name) {
        return new QueryParameter(name);
    }
}
