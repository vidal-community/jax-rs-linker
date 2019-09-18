package fr.vidal.oss.jax_rs_linker.model;


import java.util.Objects;

public final class QueryParameter {

    private final String name;

    public QueryParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final QueryParameter other = (QueryParameter) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "QueryParameter{" +
            "name='" + name + '\'' +
            '}';
    }
}
