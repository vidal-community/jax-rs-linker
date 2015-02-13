package fr.vidal.oss.jax_rs_linker.parser;

import org.junit.Test;

import static fr.vidal.oss.jax_rs_linker.parser.ApiPaths.sanitize;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiPathsTest {

    @Test
    public void removes_regex_path_from_path() {
        assertThat(sanitize("/api/boring/{parameterized : ([a-zA-Z0-9])+}/path"))
            .isEqualTo("/api/boring/{parameterized}/path");
    }

    @Test
    public void removes_regex_path_from_the_right_parameter() {
        assertThat(sanitize("/api/{param}/boring/{parameterized : ([a-zA-Z0-9])+}/path"))
            .isEqualTo("/api/{param}/boring/{parameterized}/path");
    }

    @Test
    public void removes_all_regex_from_path() {
        assertThat(sanitize("/api/{param:.*}/boring/{parameterized : ([a-zA-Z0-9])+}"))
            .isEqualTo("/api/{param}/boring/{parameterized}");
    }

    @Test
    public void removes_regex_path_from_path_when_parameter_is_at_string_end() {
        assertThat(sanitize("/api/boring/{parameterized : ([a-zA-Z0-9])+}"))
            .isEqualTo("/api/boring/{parameterized}");
    }

    @Test
    public void keeps_regexless_paths_untouched() {
        assertThat(sanitize("/api/boring/path"))
            .isEqualTo("/api/boring/path");
    }

    @Test
    public void keeps_regexless_parameterized_paths_untouched() {
        assertThat(sanitize("/api/boring/{parameterized}/path"))
            .isEqualTo("/api/boring/{parameterized}/path");
    }
}
