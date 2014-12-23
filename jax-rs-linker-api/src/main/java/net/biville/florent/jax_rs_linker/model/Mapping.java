package net.biville.florent.jax_rs_linker.model;

import com.google.common.base.Objects;

public class Mapping {

    private final JavaLocation javaLocation;
    private final Api api;

    public Mapping(JavaLocation javaLocation, Api api) {
        this.javaLocation = javaLocation;
        this.api = api;
    }

    public JavaLocation getJavaLocation() {
        return javaLocation;
    }

    public Api getApi() {
        return api;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(javaLocation, api);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Mapping other = (Mapping) obj;
        return Objects.equal(this.javaLocation, other.javaLocation) && Objects.equal(this.api, other.api);
    }

    @Override
    public String toString() {
        return String.format("SRC:%s, API:%s", javaLocation, api);
    }
}
