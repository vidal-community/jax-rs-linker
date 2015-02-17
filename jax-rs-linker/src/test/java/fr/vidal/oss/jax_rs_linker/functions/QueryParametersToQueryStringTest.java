package fr.vidal.oss.jax_rs_linker.functions;

import org.junit.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static fr.vidal.oss.jax_rs_linker.functions.QueryParametersToQueryString.TO_QUERY_STRING;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class QueryParametersToQueryStringTest {


    @Test
    public void should_output_a_empty_string_when_map_is_empty() {
        Map<String,Collection<String>> map = new LinkedHashMap<>();

        String output = TO_QUERY_STRING.apply(map);

        assertThat(output).isEmpty();

    }

    @Test
    public void should_output_query_string_with_1_parameter() {
        Map<String, Collection<String>> map = new LinkedHashMap<>();
        map.put("foo", asList("bar"));

        String output = TO_QUERY_STRING.apply(map);

        assertThat(output).isEqualTo("?foo=bar");
    }

    @Test
    public void should_output_query_string_with_2_parameters_with_same_key() {
        Map<String, Collection<String>> map = new LinkedHashMap<>();
        map.put("foo", asList("bar","bar"));

        String output = TO_QUERY_STRING.apply(map);

        assertThat(output).isEqualTo("?foo=bar&foo=bar");
    }

    @Test
    public void should_output_query_string_with_2_parameters() {
        Map<String,Collection<String>> map = new LinkedHashMap<>();
        map.put("foo", asList("bar"));
        map.put("bar", asList("baz"));

        String output = TO_QUERY_STRING.apply(map);

        assertThat(output).isEqualTo("?foo=bar&bar=baz");
    }

    @Test
    public void should_output_query_string_with_many_parameters() {
        Map<String,Collection<String>> map = new LinkedHashMap<>();
        map.put("foo", asList("foo", "bar"));
        map.put("bar", asList("bar", "baz"));

        String output = TO_QUERY_STRING.apply(map);

        assertThat(output).isEqualTo("?foo=foo&foo=bar&bar=bar&bar=baz");
    }
}
