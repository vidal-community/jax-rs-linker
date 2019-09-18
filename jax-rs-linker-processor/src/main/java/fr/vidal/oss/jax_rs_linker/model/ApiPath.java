package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import static fr.vidal.oss.jax_rs_linker.parser.ApiPaths.decorate;
import static fr.vidal.oss.jax_rs_linker.parser.ApiPaths.sanitize;
import static java.util.stream.Collectors.joining;

public final class ApiPath {

    private final String path;
    private final Collection<PathParameter> pathParameters;

    public ApiPath(String path, Collection<PathParameter> pathParameters) {
        this.path = sanitize(path);
        this.pathParameters = decorate(pathParameters, path);
    }

    public String getPath() {
        return path;
    }

    public Collection<PathParameter> getPathParameters() {
        return ImmutableList.copyOf(pathParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, pathParameters);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiPath other = (ApiPath) obj;
        return Objects.equals(this.path, other.path) && Objects.equals(this.pathParameters, other.pathParameters);
    }

    @Override
    public String toString() {
        if (pathParameters.isEmpty()) {
            return path;
        }
        return String.format("%s (%s)",
            path,
            pathParameters.stream().map(PathParameter::toString).collect(joining(",")));
    }

}
