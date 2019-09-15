package fr.vidal.oss.jax_rs_linker.model;

import java.util.Objects;

public class SubResourceTarget {

    private static final String UNPARSEABLE = "<error>";
    private final ClassName className;
    private final String qualifier;

    public SubResourceTarget(ClassName className, String qualifier) {
        this.className = className;
        this.qualifier = qualifier;
    }

    public ClassName getClassName() {
        return className;
    }

    public String getQualifier() {
        return qualifier;
    }

    public boolean isUnparseable() {
        return className.className().equals(UNPARSEABLE) || qualifier.equals(UNPARSEABLE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, qualifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SubResourceTarget other = (SubResourceTarget) obj;
        return Objects.equals(this.className, other.className)
            && Objects.equals(this.qualifier, other.qualifier);
    }
}
