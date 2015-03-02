package fr.vidal.oss.jax_rs_linker.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryParameterTest {

    @Test
    public void equals_should_return_true_if_objects_are_identical() {
        QueryParameter queryParameter = new QueryParameter("Foo");
        QueryParameter queryParameter1 = queryParameter;

        assertThat(queryParameter).isEqualTo(queryParameter1);
    }

    @Test
    public void equals_should_return_false_if_object_is_null() {
        QueryParameter queryParameter = new QueryParameter("Foo");
        QueryParameter queryParameter1 = null;

        assertThat(queryParameter).isNotEqualTo(queryParameter1);
    }

    @Test
    public void equals_should_return_false_Objects_have_not_the_same_class() {
        QueryParameter queryParameter = new QueryParameter("Foo");
        String queryParameter1 = "baz";

        assertThat(queryParameter).isNotEqualTo(queryParameter1);
    }

    @Test
    public void toString_should_return_a_valid_string() {
        QueryParameter queryParameter = new QueryParameter("Foo");

        assertThat(queryParameter.toString()).isEqualTo("QueryParameter{name='Foo'}");
    }
}
