package net.biville.florent.jax_rs_linker.processor;

import com.google.testing.compile.CompilationRule;
import org.junit.Rule;
import org.junit.Test;

import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;


public class LinkerAnnotationProcessorTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();

    @Test
    public void generates_a_single_linker() {
        ASSERT.about(javaSource())
            .that(forResource("ProductResource.java"))
            .processedWith(new LinkerAnnotationProcessor())
            .compilesWithoutError()
            .and().generatesSources(forResource("ProductResourceLinker.java"));
    }
}