package net.biville.florent.jax_rs_linker.processor.parser;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.jax_rs_linker.processor.model.ClassName;
import net.biville.florent.jax_rs_linker.processor.model.PathParameter;
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
            "net.biville.florent.jax_rs_linker.processor.parser.SimplePathClass",
            "hello"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("simple-path");
    }

    @Test
    public void aggregates_simple_path_at_class_level() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.ClassLevelSimplePathClass",
            "foo"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("/simple-path-at-class-level");
    }

    @Test
    public void aggregates_compound_path_at_one_level() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.CompoundPathClass",
            "something"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("/compound/path");
    }

    @Test
    public void aggregates_inherited_compound_path() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.InheritedCompoundPathClass",
            "bar"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("/compound/and-inherited/path-for-the-win");
    }

    @Test
    public void aggregates_path_from_classes_implementing_interfaces() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.ImplementingClassWithPath",
            "fooFighters"
        );

        assertThat(visitor.visitPath(method).get()).isEqualTo("super-path");
    }

    @Test
    public void detects_empty_path_parameters_when_no_argument_specified() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.SimplePathClass",
            "hello"
        );

        assertThat(visitor.visitParameters(method)).isEmpty();
    }

    @Test
    public void detects_single_path_parameter() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.SimplePathClass",
            "world"
        );

        assertThat(visitor.visitParameters(method)).containsExactly(
            new PathParameter(ClassName.valueOf("int"), "id")
        );
    }

    @Test
    public void detects_many_path_parameters() {
        ExecutableElement method = methodElements.of(
            "net.biville.florent.jax_rs_linker.processor.parser.SimplePathClass",
            "monde"
        );

        assertThat(visitor.visitParameters(method)).containsExactly(
            new PathParameter(ClassName.valueOf("java.lang.String"), "super"),
            new PathParameter(ClassName.valueOf("int[]"), "params"),
            new PathParameter(ClassName.valueOf("java.lang.Number"), "everywhere")
        );
    }
}