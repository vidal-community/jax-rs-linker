
package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import java.lang.Override;
import java.lang.String;
import java.util.regex.Pattern;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum BrandResourcePathParameters implements PathParameters {
    CODE("code", Optional.<Pattern>absent()),

    ID("id", Optional.<Pattern>absent()),

    ZIP("zip", Optional.<Pattern>absent());

    private final String placeholder;

    private final Optional<Pattern> regex;

    BrandResourcePathParameters(String placeholder, Optional<Pattern> regex) {
        this.placeholder = placeholder;
        this.regex = regex;
    }

    @Override
    public final String placeholder() {
        return this.placeholder;
    }

    @Override
    public final Optional<Pattern> regex() {
        return this.regex;
    }
}
