package fr.vidal.oss.jax_rs_linker.model;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.String.format;

public final class PathParameter {

    private final ClassName type;
    private final String name;
    private final Optional<Pattern> regex;

    public PathParameter(ClassName type, String name) {
        this(type, name, Optional.empty());
    }

    public PathParameter(ClassName type, String name, String regex) {
        this.type = type;
        this.name = name;
        this.regex = Optional.of(Pattern.compile(regex));
    }

    private PathParameter(ClassName type, String name, Optional<Pattern> regex) {
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

    public Optional<Pattern> getRegex() {
        return regex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, regex);
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
        return Objects.equals(this.type, other.type)
            && Objects.equals(this.name, other.name)
            && Objects.equals(this.regex, other.regex);
    }

    @Override
    public String toString() {
        return format("%s %s", type, name);
    }
}
