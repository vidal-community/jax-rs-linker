package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.collect.Lists;
import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static fr.vidal.oss.jax_rs_linker.model.ProductQueryParameters.LABEL;
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
            newArrayList(pathParameter(className("int"), "id")));

        templatedPath.value();
    }

    @Test
    public void renders_parameterless_path() {
        TemplatedUrl<ProductParameters,NoQueryParameters> templatedUrl = templatedUrl(
            "/product/",
            Lists.newArrayList());

        assertThat(templatedUrl.value()).isEqualTo("/product/");
    }

    @Test
    public void renders_parameterized_path_after_replacement() {
        TemplatedUrl<ProductParameters,NoQueryParameters> templatedUrl = templatedUrl(
            "/product/{id}",
            newArrayList(pathParameter(className("int"), "id")));

        assertThat(
            templatedUrl
                .replace(ProductParameters.ID, "42")
                .value()
        ).isEqualTo("/product/42");
    }

    @Test
    public void appends_a_single_query_parameter() {
        TemplatedUrl<ProductParameters, ProductQueryParameters> templatedUrl = templatedUrl(
            "/product/{id}",
            newArrayList(pathParameter(className("int"), "id")),
            newArrayList(queryParameter("label")));

        assertThat(
            templatedUrl
                .replace(ProductParameters.ID, "42")
                .append(LABEL, "bleue")
                .value()
        ).isEqualTo("/product/42?label=bleue");
    }

    @Test
    public void appends_query_parameters_one_by_one() {
        TemplatedUrl<ProductParameters, ProductQueryParameters> templatedUrl = templatedUrl(
            "/product/{id}",
            newArrayList(pathParameter(className("int"), "id")),
            newArrayList(queryParameter("label")));

        assertThat(
            templatedUrl
                .replace(ProductParameters.ID, "42")
                .append(LABEL, "bleue")
                .append(LABEL, "rouge")
                .value()
        ).isEqualTo("/product/42?label=bleue&label=rouge");
    }

    @Test
    public void appends_query_parameters_at_once() {
        TemplatedUrl<ProductParameters, ProductQueryParameters> templatedUrl = templatedUrl(
            "/product/{id}",
            newArrayList(pathParameter(className("int"), "id")),
            newArrayList(queryParameter("label")));

        assertThat(
            templatedUrl
                .replace(ProductParameters.ID, "42")
                .appendAll(LABEL, Arrays.asList("bleue", "rouge"))
                .value()
        ).isEqualTo("/product/42?label=bleue&label=rouge");
    }

    private TemplatedUrl<ProductParameters, NoQueryParameters> templatedUrl(String path, Collection<PathParameter> parameters) {
        return new TemplatedUrl<>(path, parameters, Lists.newArrayList());
    }

    private TemplatedUrl<ProductParameters, ProductQueryParameters> templatedUrl(String path, Collection<PathParameter> parameters, Collection<QueryParameter> queryParameters) {
        return new TemplatedUrl<>(path, parameters, queryParameters);
    }

    private PathParameter pathParameter(ClassName className, String name) {
        return new PathParameter(className, name);
    }

    private QueryParameter queryParameter(String name) {
        return new QueryParameter(name);
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
    public Pattern regex() {
        return null;
    }
}

enum ProductQueryParameters implements QueryParameters {
    LABEL("label");

    private final String value;

    ProductQueryParameters(String value) {

        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
