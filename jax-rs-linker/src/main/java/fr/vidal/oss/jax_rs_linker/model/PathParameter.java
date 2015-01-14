package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Objects;

import static java.lang.String.format;

public class PathParameter {

    private final ClassName type;
    private final String name;

    public PathParameter(ClassName type, String name) {
        this.type = type;
        this.name = name;
    }

    public ClassName getType() {
        return type;
    }

    public String getName() {
        return name;
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
