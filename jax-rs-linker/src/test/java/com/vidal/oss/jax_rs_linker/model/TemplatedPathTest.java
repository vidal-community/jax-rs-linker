package com.vidal.oss.jax_rs_linker.model;

import com.google.common.collect.Lists;
import com.vidal.oss.jax_rs_linker.parser.ProductResource;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class TemplatedPathTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fails_to_render_value_with_unreplaced_parameters() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Parameters to replace: id");

        TemplatedPath templatedPath = templatedPath(
                "/product/{id}/brand",
                newArrayList(pathParameter(className("int"), "id"))
        );

        templatedPath.value();
    }

    @Test
    public void renders_parameterless_path() {
        TemplatedPath templatedPath = templatedPath(
                "/product/",
                Lists.<PathParameter>newArrayList()
        );

        assertThat(templatedPath.value()).isEqualTo("/product/");
    }

    @Test
    @Ignore
    public void renders_parameterized_path_after_replacement() {
        TemplatedPath templatedPath = templatedPath(
                "/product/{id}",
                newArrayList(pathParameter(className("int"), "id"))
        );

        assertThat(
                templatedPath
//                        .replace(ProductResourcePathParameters.ID, "42")
                        .value()
        ).isEqualTo("/product/42");
    }

    private TemplatedPath templatedPath(String path, Collection<PathParameter> parameters) {
        return new TemplatedPath(path, parameters);
    }

    private PathParameter pathParameter(ClassName className, String name) {
        return new PathParameter(className, name);
    }

    private ClassName className(String type) {
        return ClassName.valueOf(type);
    }
}