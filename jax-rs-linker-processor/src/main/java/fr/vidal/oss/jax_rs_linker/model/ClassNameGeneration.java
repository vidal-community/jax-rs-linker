package fr.vidal.oss.jax_rs_linker.model;

import javax.lang.model.element.TypeElement;
import java.util.Objects;

public class ClassNameGeneration implements Comparable<ClassNameGeneration> {

    private final ClassName className;
    private final TypeElement originatingElement;

    public ClassNameGeneration(ClassName className, TypeElement originatingElement) {
        this.className = className;
        this.originatingElement = originatingElement;
    }

    public ClassNameGeneration(TypeElement originatingElement) {
        this.className = ClassName.valueOf(originatingElement.getQualifiedName().toString());
        this.originatingElement = originatingElement;
    }

    public ClassNameGeneration append(String suffix) {
        return new ClassNameGeneration(className.append(suffix), originatingElement);
    }

    public String className() {
        return className.className();
    }

    public String packageName() {
        return className.packageName();
    }

    public ClassName getClassName() {
        return className;
    }

    public TypeElement getOriginatingElement() {
        return originatingElement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, originatingElement);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ClassNameGeneration other = (ClassNameGeneration) obj;
        return Objects.equals(this.className, other.className)
            && Objects.equals(this.originatingElement, other.originatingElement);
    }

    @Override
    public String toString() {
        return className.toString();
    }

    @Override
    public int compareTo(ClassNameGeneration other) {
        return className.compareTo(other.className);
    }
}
