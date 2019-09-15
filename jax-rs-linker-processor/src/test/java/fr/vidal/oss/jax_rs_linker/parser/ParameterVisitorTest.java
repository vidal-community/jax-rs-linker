package fr.vidal.oss.jax_rs_linker.parser;

import com.google.testing.compile.CompilationRule;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.ws.rs.PathParam;
import java.util.Collection;
import java.util.NoSuchElementException;

import static fr.vidal.oss.jax_rs_linker.functions.ElementToPathParameter.ELEMENT_INTO_PATH_PARAMETER;
import static fr.vidal.oss.jax_rs_linker.functions.SetterToPathParameter.SETTER_TO_PATH_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;

public class ParameterVisitorTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();
    private Elements elements;

    @Before
    public void prepare() {
        elements = compilation.getElements();
    }

    @Test
    public void extracts_path_parameters_from_bean_parameters() {
        TypeElement resource = elements.getTypeElement("parameter_visitor.StupidResource");
        Element method = extract(resource, "doSomething");

        ParameterVisitor<PathParameter> visitor = new ParameterVisitor<>(
            compilation.getTypes(),
            new AnnotatedElementMapping<>(
                PathParam.class,
                ELEMENT_INTO_PATH_PARAMETER::apply,
                SETTER_TO_PATH_PARAMETER::apply
            )
        );

        Collection<PathParameter> parameters = visitor.visit(method);

        assertThat(parameters).extracting("name")
            .containsOnly("{foo}", "{bar}", "{baz}");

    }

    private Element extract(TypeElement resource, String methodName) {
        for (Element element : elements.getAllMembers(resource)) {
            if (element.getSimpleName().contentEquals(methodName)) {
                return element;
            }
        }
        throw new NoSuchElementException(
            String.format("No method <%s> found for <%s>", methodName, resource.getQualifiedName())
        );
    }
}
