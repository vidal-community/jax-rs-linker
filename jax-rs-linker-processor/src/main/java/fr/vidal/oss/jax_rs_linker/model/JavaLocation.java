package fr.vidal.oss.jax_rs_linker.model;

import java.util.Objects;

public final class JavaLocation {

    private final ClassNameGeneration classNameGeneration;
    private final String methodName;


    public JavaLocation(ClassNameGeneration classNameGeneration, String methodName) {
        this.classNameGeneration = classNameGeneration;
        this.methodName = methodName;
    }

    public ClassNameGeneration getClassNameGeneration() {
        return classNameGeneration;
    }

    public String getMethodName() {
        return methodName;
    }


    @Override
    public int hashCode() {
        return Objects.hash(classNameGeneration, methodName);
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
        return Objects.equals(this.classNameGeneration, other.classNameGeneration)
                && Objects.equals(this.methodName, other.methodName);
    }

    @Override
    public String toString() {
        return String.format("%s#%s", classNameGeneration, methodName);
    }
}
