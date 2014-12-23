package net.biville.florent.jax_rs_linker.model;

import com.google.common.base.Objects;

public class PathArgument {

    private final String name;
    private final String value;

    public PathArgument(String name, String value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PathArgument other = (PathArgument) obj;
        return Objects.equal(this.name, other.name) && Objects.equal(this.value, other.value);
    }
}
