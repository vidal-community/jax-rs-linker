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
        return new ClassNameGeneration(ClassName.valueOf(className.fullyQualifiedName() + suffix), originatingElement);
    }

    public String className() {
        String name = className.fullyQualifiedName();
        if (!name.contains(".")) {
            return name;
        }
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public String packageName() {
        String name = className.fullyQualifiedName();
        if (!name.contains(".")) {
            return "";
        }
        return name.substring(0, name.lastIndexOf('.'));
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
        return className.fullyQualifiedName();
    }

    @Override
    public int compareTo(ClassNameGeneration other) {
        return className.fullyQualifiedName().compareTo(other.getClassName().fullyQualifiedName());
    }
}
