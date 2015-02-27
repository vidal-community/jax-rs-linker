package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static fr.vidal.oss.jax_rs_linker.parser.ApiPaths.decorate;
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

    @Test
    public void adds_regex_to_path_parameters() {
        HashSet<PathParameter> pathParameters = newHashSet(new PathParameter(ClassName.valueOf(String.class.getName()), "param"));
        Collection<PathParameter> result = decorate(pathParameters, "/api/boring/{param:([a-zA-Z0-9])+}");

        assertThat(result).extracting("regex").containsExactly(Optional.of("([a-zA-Z0-9])+"));
    }

    @Test
    public void adds_correct_regex_to_multiple_path_parameters() {
        HashSet<PathParameter> pathParameters = newHashSet(
                new PathParameter(ClassName.valueOf(String.class.getName()), "param"),
                new PathParameter(ClassName.valueOf(String.class.getName()), "other")
        );
        Collection<PathParameter> result = decorate(pathParameters,
                "/api/boring/{param:([a-zA-Z0-9])+}/{other:[1-9]}");

        assertThat(result).extracting("regex").contains(Optional.of("([a-zA-Z0-9])+"), Optional.of("[1-9]"));
    }

    @Test
    public void keeps_regexless_path_parameters_untouched() {
        Set<PathParameter> pathParameters = newHashSet(new PathParameter(ClassName.valueOf(String.class.getName()), "param"));
        Collection<PathParameter> result = decorate(pathParameters, "/api/boring/{param}");

        assertThat(result).extracting("regex").containsExactly(Optional.absent());
    }

    @Test
    public void keeps_regexless_multiple_path_parameters_untouched() {
        Set<PathParameter> pathParameters = newHashSet(
                new PathParameter(ClassName.valueOf(String.class.getName()), "param"),
                new PathParameter(ClassName.valueOf(String.class.getName()), "other")
        );
        Collection<PathParameter> result = decorate(pathParameters, "/api/boring/{param}/{other}");

        assertThat(result).extracting("regex").containsExactly(Optional.absent(), Optional.absent());
    }

    @Test
    public void adds_regex_to_corresponding_path_parameter_and_keeps_regexless_path_parameter_untouched() {
        Set<PathParameter> pathParameters = newHashSet(
                new PathParameter(ClassName.valueOf(String.class.getName()), "param"),
                new PathParameter(ClassName.valueOf(String.class.getName()), "other"),
                new PathParameter(ClassName.valueOf(String.class.getName()), "last")
        );
        Collection<PathParameter> result = decorate(pathParameters, "/api/boring/{param}/{other:[1-9]}/{last}");

        assertThat(result).extracting("regex").contains(Optional.absent(), Optional.of("[1-9]"), Optional.absent());
    }
}
