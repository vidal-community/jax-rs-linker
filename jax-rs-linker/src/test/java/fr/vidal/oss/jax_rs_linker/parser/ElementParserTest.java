package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import com.google.testing.compile.CompilationRule;
import fr.vidal.oss.jax_rs_linker.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;

import static javax.tools.Diagnostic.Kind.ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ElementParserTest {

    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private MethodElements methodElements;
    private Messager messager;
    private ElementParser parser;

    @Before
    public void setup() {
        methodElements = new MethodElements(compilationRule.getElements());
        messager = mock(Messager.class);
        parser = new ElementParser(messager, compilationRule.getTypes());
    }

    @Test
    public void parses_method_to_mapping_representation() {
        ExecutableElement method = methodElements.of(
            "fr.vidal.oss.jax_rs_linker.parser.ProductResource",
            "productById"
        );

        Mapping mapping = parser.parse(method).get();

        JavaLocation javaLocation = mapping.getJavaLocation();
        assertThat(javaLocation.getClassName().fullyQualifiedName())
            .isEqualTo("fr.vidal.oss.jax_rs_linker.parser.ProductResource");
        assertThat(javaLocation.getMethodName())
            .isEqualTo("productById");

        Api api = mapping.getApi();
        assertThat(api.getApiLink().getApiLinkType())
            .isEqualTo(ApiLinkType.SELF);
        assertThat(api.getApiLink().getTarget().isPresent())
            .isFalse();
        assertThat(api.getApiPath().getPath())
            .isEqualTo("/api/product/{id}");
        assertThat(api.getApiPath().getPathParameters())
            .containsExactly(new PathParameter(ClassName.valueOf("int"), "id"));
    }

    @Test
    public void fails_to_parse_because_of_absence_of_path() {
        ExecutableElement method = methodElements.of(
            "fr.vidal.oss.jax_rs_linker.parser.UnparseableResource",
            "methodWithoutPath"
        );

        Optional<Mapping> mapping = parser.parse(method);

        String errorMessage =
            "\n\tNo path could be found. \n" +
                "\tPlease make sure JAX-RS @Path is set on the method, its class or superclasses.\n" +
                "\tGiven method: <fr.vidal.oss.jax_rs_linker.parser.UnparseableResource#methodWithoutPath>";
        assertThat(mapping.isPresent()).isFalse();
        verify(messager).printMessage(ERROR, errorMessage, method);
    }

    @Test
    public void fails_to_parse_because_of_absence_of_jax_rs_http_verb_annotation() {
        ExecutableElement method = methodElements.of(
            "fr.vidal.oss.jax_rs_linker.parser.UnparseableResource",
            "methodWithoutHttpVerb"
        );

        Optional<Mapping> mapping = parser.parse(method);

        String errorMessage =
            "\n\tNo JAX-RS HTTP verb annotation found (e.g. @GET, @POST...).\n" +
                "\tGiven method: <fr.vidal.oss.jax_rs_linker.parser.UnparseableResource#methodWithoutHttpVerb>";
        assertThat(mapping.isPresent()).isFalse();
        verify(messager).printMessage(ERROR, errorMessage, method);
    }

    @Test
    public void fails_to_parse_because_no_link_type_annotation_is_present() {
        ExecutableElement method = methodElements.of(
            "fr.vidal.oss.jax_rs_linker.parser.UnparseableResource",
            "methodWithoutLinkTypes"
        );

        Optional<Mapping> mapping = parser.parse(method);

        String errorMessage =
            "\n\tMethod should be annotated with exactly one annotation: @Self or @SubResource.\n" +
                "\tGiven method: <fr.vidal.oss.jax_rs_linker.parser.UnparseableResource#methodWithoutLinkTypes>";
        assertThat(mapping.isPresent()).isFalse();
        verify(messager).printMessage(ERROR, errorMessage, method);
    }

    @Test
    public void fails_to_parse_because_too_many_link_type_annotations_are_present() {
        ExecutableElement method = methodElements.of(
            "fr.vidal.oss.jax_rs_linker.parser.UnparseableResource",
            "methodWithTooManyLinkTypes"
        );

        Optional<Mapping> mapping = parser.parse(method);

        String errorMessage =
            "\n\tMethod should be annotated with exactly one annotation: @Self or @SubResource.\n" +
            "\tGiven method: <fr.vidal.oss.jax_rs_linker.parser.UnparseableResource#methodWithTooManyLinkTypes>";
        assertThat(mapping.isPresent()).isFalse();
        verify(messager).printMessage(ERROR, errorMessage, method);
    }
}
