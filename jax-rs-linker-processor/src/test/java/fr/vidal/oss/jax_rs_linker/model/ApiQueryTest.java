package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.collect.ImmutableList;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;


public class ApiQueryTest {

    @Test
    public void equals_should_return_true_if_object_are_identical() {
        Collection<QueryParameter> queryParameters = newArrayList();

        ApiQuery  apiQuery = new ApiQuery(queryParameters);
        ApiQuery apiQuery1 = new ApiQuery(ImmutableList.copyOf(queryParameters));

        assertThat(apiQuery).isEqualTo(apiQuery1);
    }

    @Test
    public void toString_should_return_a_valid_string_representation() {
        Collection<QueryParameter> queryParameters = newArrayList(new QueryParameter("Foo"),new QueryParameter("Bar") );
        ApiQuery apiQuery = new ApiQuery(queryParameters);

        assertThat(apiQuery.toString()).isEqualTo("ApiQuery{queryParameters=[FooBar]}");
    }

    @Test
    public void equals_contract() {
        EqualsVerifier.forClass(ApiQuery.class).verify();
    }
}
