
package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import java.lang.Override;
import java.lang.String;
import java.util.regex.Pattern;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum PersonResourcePathParameters implements PathParameters {
    FIRST_NAME("firstName", Optional.<Pattern>of(Pattern.compile(".*"))),

    ID("id", Optional.<Pattern>absent());

    private final String placeholder;
    private final Optional<Pattern> regex;

    PersonResourcePathParameters(String placeholder, Optional<Pattern> regex) {
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
