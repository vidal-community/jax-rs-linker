package fr.vidal.oss.jax_rs_linker.model;

import java.util.Objects;

public class JavaLocation {

    private final ClassName className;
    private final String methodName;


    public JavaLocation(ClassName className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public ClassName getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }


    @Override
    public int hashCode() {
        return Objects.hash(className, methodName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final JavaLocation other = (JavaLocation) obj;
        return Objects.equals(this.className, other.className)
                && Objects.equals(this.methodName, other.methodName);
    }

    @Override
    public String toString() {
        return String.format("%s#%s", className, methodName);
    }
}
