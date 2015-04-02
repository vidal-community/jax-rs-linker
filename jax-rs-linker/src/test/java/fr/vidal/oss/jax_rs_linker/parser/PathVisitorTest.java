package fr.vidal.oss.jax_rs_linker.parser;

import com.google.testing.compile.CompilationRule;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.lang.model.element.ExecutableElement;

import static org.assertj.core.api.Assertions.assertThat;

public class PathVisitorTest {

    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private MethodElements methodElements;

    private PathVisitor visitor;

    @Before
    public void setup() {
        methodElements = new MethodElements(compilationRule.getElements());
        visitor = new PathVisitor(compilationRule.getTypes());
    }

    @Test
    public void aggregates_nothing_when_no_path_specified() {
        ExecutableElement method = methodElements.of(
            "java.lang.String",
            "toString"
        );

        assertThat(visitor.visitPath(method).isPresent()).isFalse();
    }

    @Test
    public void aggregates_simple_path() {
        ExecutableElement method = methodElements.of(
            SimplePathClass.class.getName(),
            "hello"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("simple-path");
    }

    @Test
    public void aggregates_simple_path_at_class_level() {
        ExecutableElement method = methodElements.of(
            ClassLevelSimplePathClass.class.getName(),
            "foo"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("/simple-path-at-class-level");
    }

    @Test
    public void aggregates_compound_path_at_one_level() {
        ExecutableElement method = methodElements.of(
            CompoundPathClass.class.getName(),
            "something"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("/compound/path");
    }

    @Test
    public void aggregates_inherited_compound_path() {
        ExecutableElement method = methodElements.of(
            InheritedCompoundPathClass.class.getName(),
            "bar"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("/compound/and-inherited/path-for-the-win");
    }

    @Test
    public void aggregates_path_from_classes_implementing_interfaces() {
        ExecutableElement method = methodElements.of(
            ImplementingClassWithPath.class.getName(),
            "fooFighters"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("super-path");
    }

}
