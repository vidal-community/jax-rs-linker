
package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum BrandResourcePathParameters implements PathParameters {
    CODE("code", Optional.<String>absent()),

    ID("id", Optional.<String>absent()),

    ZIP("zip", Optional.<String>absent());

    private final String placeholder;

    private final Optional<String> regex;

    BrandResourcePathParameters(String placeholder, Optional<String> regex) {
        this.placeholder = placeholder;
        this.regex = regex;
    }

    @Override
    public final String placeholder() {
        return this.placeholder;
    }

    @Override
    public final Optional<String> regex() {
        return this.regex;
    }
}
