
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.model.*;

import java.util.Arrays;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public class PersonResourceLinker {

    private final String contextPath;

    public PersonResourceLinker() {
        this("");
    }

    public PersonResourceLinker(String contextPath) {
        this.contextPath = contextPath;
    }

    public TemplatedUrl<QueryParameter> self() {
        return new TemplatedUrl<QueryParameter>(contextPath, Arrays.<QueryParameter>asList(new PathParameter(ClassName.valueOf("int"), "id")), queryParameters);
    }

}
