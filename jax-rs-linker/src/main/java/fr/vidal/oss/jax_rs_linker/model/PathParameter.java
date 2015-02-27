package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.*;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class PathParameter {

    private final ClassName type;
    private final String name;
    private final Optional<String> regex;

    public PathParameter(ClassName type, String name) {
        this(type, name, Optional.<String>absent());
    }

    public PathParameter(ClassName type, String name, String regex) {
        this(type, name, fromNullable(regex));
    }

    private PathParameter(ClassName type, String name, Optional<String> regex) {
        this.type = type;
        this.name = name;
        this.regex = regex;
    }



    public ClassName getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getRegex() {
        return regex;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(type, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PathParameter other = (PathParameter) obj;
        return Objects.equal(this.type, other.type) && Objects.equal(this.name, other.name);
    }

    @Override
    public String toString() {
        return format("%s %s", type, name);
    }
}
