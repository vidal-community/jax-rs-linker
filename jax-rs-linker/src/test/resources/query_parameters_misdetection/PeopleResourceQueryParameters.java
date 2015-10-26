package query_parameters_misdetection;

import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import java.lang.String;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum PeopleResourceQueryParameters implements QueryParameters {
    PAYS("pays"),

    VILLE("ville");

    private final String value;

    PeopleResourceQueryParameters(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
