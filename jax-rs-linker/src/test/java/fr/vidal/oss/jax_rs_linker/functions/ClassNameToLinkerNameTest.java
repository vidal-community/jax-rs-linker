package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassNameToLinkerNameTest {


    @Test
    public void should_return_valid_linker_name() {

        ClassName className = ClassName.valueOf("fr.vidal.oss.Foo");

        assertThat(ClassNameToLinkerName.TO_LINKER_NAME.apply(className)).isEqualTo(className.fullyQualifiedName().concat(LinkerAnnotationProcessor.GENERATED_CLASSNAME_SUFFIX));
    }
}
