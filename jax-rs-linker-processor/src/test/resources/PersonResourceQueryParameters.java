package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import java.lang.String;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum PersonResourceQueryParameters implements QueryParameters {
    ALIVE_FLAG("alive-flag");

    private final String value;

    PersonResourceQueryParameters(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
