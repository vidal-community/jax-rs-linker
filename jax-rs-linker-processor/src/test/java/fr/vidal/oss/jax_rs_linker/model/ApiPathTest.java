package fr.vidal.oss.jax_rs_linker.model;

import org.junit.Test;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiPathTest {

    @Test
    public void equals_should_return_true_if_objects_are_identical() {
        Collection<PathParameter> pathParameters = newArrayList(new PathParameter(ClassName.valueOf("fr.vidal.oss.Foo"),"Foo"),new PathParameter(ClassName.valueOf("fr.vidal.oss.Bar"),"Bar"));
        ApiPath apiPath = new ApiPath("/dev/null",pathParameters);
        ApiPath apiPath1 = apiPath;

        assertThat(apiPath).isEqualTo(apiPath1);
    }

    @Test
    public void equals_should_return_false_if_object_is_null() {
        Collection<PathParameter> pathParameters = newArrayList(new PathParameter(ClassName.valueOf("fr.vidal.oss.Foo"),"Foo"),new PathParameter(ClassName.valueOf("fr.vidal.oss.Bar"),"Bar"));
        ApiPath apiPath = new ApiPath("/dev/null",pathParameters);
        ApiPath apiPath1 = null;

        assertThat(apiPath).isNotEqualTo(apiPath1);
    }

    @Test
    public void equals_should_return_false_Objects_have_not_the_same_class() {
        Collection<PathParameter> pathParameters = newArrayList(new PathParameter(ClassName.valueOf("fr.vidal.oss.Foo"),"Foo"),new PathParameter(ClassName.valueOf("fr.vidal.oss.Bar"),"Bar"));
        ApiPath apiPath = new ApiPath("/dev/null",pathParameters);
        String baz = "baz";

        assertThat(apiPath).isNotEqualTo(baz);
    }

    @Test
    public void equals_should_return_false_objects_not_deeply_indentical() {
        Collection<PathParameter> pathParameters = newArrayList(new PathParameter(ClassName.valueOf("fr.vidal.oss.Foo"),"Foo"),new PathParameter(ClassName.valueOf("fr.vidal.oss.Bar"),"Bar"));
        ApiPath apiPath = new ApiPath("/dev/null",pathParameters);
        Collection<PathParameter> pathParameters1 = newArrayList(new PathParameter(ClassName.valueOf("fr.vidal.oss.Bar"),"Bar"));
        ApiPath apiPath1 = new ApiPath("/dev/null/2",pathParameters);
        ApiPath apiPath2 = new ApiPath("/dev/null",pathParameters1);

        assertThat(apiPath).isNotEqualTo(apiPath1);
        assertThat(apiPath).isNotEqualTo(apiPath2);
    }



    @Test
    public void toString_should_return_a_valid_string() {
        Collection<PathParameter> pathParameters = newArrayList(new PathParameter(ClassName.valueOf("fr.vidal.oss.Foo"),"Foo"),new PathParameter(ClassName.valueOf("fr.vidal.oss.Bar"),"Bar"));
        ApiPath apiPath = new ApiPath("/dev/null",pathParameters);

        assertThat(apiPath.toString()).isEqualTo("/dev/null (fr.vidal.oss.Foo Foo,fr.vidal.oss.Bar Bar)");
    }
}
