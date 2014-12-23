package net.biville.florent.jax_rs_linker.processor.parser;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.jax_rs_linker.model.HttpVerb;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpVerbVisitorTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();
    private MethodElements elements;
    private HttpVerbVisitor visitor = new HttpVerbVisitor();

    @Before
    public void setup() {
        elements = new MethodElements(compilation.getElements());
    }

    @Test
    public void parses_jax_rs_annotations_into_http_verbs() {
        String jaxRsClass = "net.biville.florent.jax_rs_linker.processor.parser.JaxRsAnnotatedClass";
        assertThat(visitor.visit(elements.of(jaxRsClass, "nothing")).isPresent()).isFalse();
        assertThat(visitor.visit(elements.of(jaxRsClass, "get")).get()).isEqualTo(HttpVerb.GET);
        assertThat(visitor.visit(elements.of(jaxRsClass, "post")).get()).isEqualTo(HttpVerb.POST);
        assertThat(visitor.visit(elements.of(jaxRsClass, "put")).get()).isEqualTo(HttpVerb.PUT);
        assertThat(visitor.visit(elements.of(jaxRsClass, "delete")).get()).isEqualTo(HttpVerb.DELETE);
        assertThat(visitor.visit(elements.of(jaxRsClass, "head")).get()).isEqualTo(HttpVerb.HEAD);
        assertThat(visitor.visit(elements.of(jaxRsClass, "options")).get()).isEqualTo(HttpVerb.OPTIONS);
    }
}