package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class TemplatedUrlTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fails_to_render_value_with_unreplaced_parameters() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Parameters to replace: id");

        TemplatedUrl<ProductParameters, NoQueryParameters> templatedPath = templatedUrl(
            "/product/{id}/brand",
            newArrayList(pathParameter(className("int"), "id")),
            Lists.<QueryParameter>newArrayList());

        templatedPath.value();
    }

    @Test
    public void renders_parameterless_path() {
        TemplatedUrl<ProductParameters,NoQueryParameters> templatedUrl = templatedUrl(
            "/product/",
            Lists.<PathParameter>newArrayList(),
            Lists.<QueryParameter>newArrayList());

        assertThat(templatedUrl.value()).isEqualTo("/product/");
    }

    @Test
    public void renders_parameterized_path_after_replacement() {
        TemplatedUrl<ProductParameters,NoQueryParameters> templatedUrl = templatedUrl(
            "/product/{id}",
            newArrayList(pathParameter(className("int"), "id")),
            Lists.<QueryParameter>newArrayList());

        assertThat(
            templatedUrl
                .replace(ProductParameters.ID, "42")
                .value()
        ).isEqualTo("/product/42");
    }

    private TemplatedUrl<ProductParameters,NoQueryParameters> templatedUrl(String path, Collection<PathParameter> parameters, Collection<QueryParameter> queryParameters) {
        return new TemplatedUrl<>(path, parameters, queryParameters);
    }

    private PathParameter pathParameter(ClassName className, String name) {
        return new PathParameter(className, name);
    }

    private ClassName className(String type) {
        return ClassName.valueOf(type);
    }
}

enum ProductParameters implements PathParameters {
    ID;

    @Override
    public String placeholder() {
        return "id";
    }

    @Override
    public Optional<Pattern> regex() {
        return Optional.absent();
    }
}
