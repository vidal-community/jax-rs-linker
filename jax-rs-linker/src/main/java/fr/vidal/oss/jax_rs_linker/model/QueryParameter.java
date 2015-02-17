package fr.vidal.oss.jax_rs_linker.model;


import com.google.common.base.Objects;

public class QueryParameter {

    private String name;
    private ClassName className;

    public QueryParameter(ClassName className, String name) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public ClassName getClassName() {
        return className;
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
        return Objects.equal(this.className, other.className) && Objects.equal(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(className, name);
    }

    @Override
    public String toString() {
        return "QueryParameter { " +
            "name='" + name + "\'" +
            ", className='" + className +
            "' }";
    }
}
