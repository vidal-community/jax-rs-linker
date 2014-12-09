package net.biville.florent.jax_rs_linker.processor.model;

import com.google.common.base.Objects;

public final class ClassName {

    private final String name;

    private ClassName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ClassName valueOf(String name) {
        return new ClassName(name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ClassName other = (ClassName) obj;
        return Objects.equal(this.name, other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
