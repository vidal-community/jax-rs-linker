
package query_parameters_misdetection;

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
public enum PeopleResourceLinker {
    INSTANCE;

    private final String contextPath = ContextPathHolder.getContextPath();

    public static PeopleResourceLinker peopleResourceLinker() {
        return INSTANCE;
    }

    public final TemplatedUrl<PeopleResourcePathParameters, NoQueryParameters> self() {
        return new TemplatedUrl<PeopleResourcePathParameters, NoQueryParameters>(contextPath + "/{id}", Arrays.<PathParameter>asList(pathParameter("java.lang.Integer", "id")), Arrays.<QueryParameter>asList());
    }

    public final TemplatedUrl<PeopleResourcePathParameters, PeopleResourceQueryParameters> relatedPeopleResourceFriends() {
        return new TemplatedUrl<PeopleResourcePathParameters, PeopleResourceQueryParameters>(contextPath + "/{id}/friends", Arrays.<PathParameter>asList(pathParameter("java.lang.Integer", "id")), Arrays.<QueryParameter>asList(queryParameter("pays"),queryParameter("ville")));
    }

    private static PathParameter pathParameter(final String type, final String name) {
        return new PathParameter(ClassName.valueOf(type), name);
    }

    private static QueryParameter queryParameter(final String name) {
        return new QueryParameter(name);
    }
}
