package fr.vidal.oss.jax_rs_linker;

import com.google.testing.compile.CompilationRule;
import org.junit.Rule;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ExposedApplicationAnnotationProcessorTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();
    private ExposedApplicationAnnotationProcessor processor = new ExposedApplicationAnnotationProcessor();


    @Test
    public void generates_application_name_enum() {
        assert_().about(javaSource())
            .that(forResource("Configuration.java"))
            .processedWith(processor)
            .compilesWithoutError()
            .and()
            .generatesSources(forResource("ApplicationName.java"));


    }
}
