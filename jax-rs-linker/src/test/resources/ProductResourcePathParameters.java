
package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum ProductResourcePathParameters implements PathParameters {
    ID("id");

    private final String placeholder;

    ProductResourcePathParameters(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public final String placeholder() {
        return this.placeholder;
    }
}
