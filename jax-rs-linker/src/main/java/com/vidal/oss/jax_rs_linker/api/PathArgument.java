package com.vidal.oss.jax_rs_linker.api;

import com.google.common.base.Function;
import com.google.common.base.Objects;

public class PathArgument {

    private final String name;
    private final String value;

    public static PathArgument argument(String name, String value) {
        return new PathArgument(name, value);
    }

    public static <T extends Number> PathArgument argument(String name, T value) {
        return new PathArgument(name, value.toString());
    }

    public static <T> PathArgument argument(String name, T value, Function<T, String> toString) {
        return new PathArgument(name, toString.apply(value));
    }

    private PathArgument(String name, String value) {
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
