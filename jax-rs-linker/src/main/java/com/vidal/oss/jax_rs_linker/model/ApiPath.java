package com.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

public class ApiPath {

    private final String path;
    private final Collection<PathParameter> pathParameters;

    public ApiPath(String path, Collection<PathParameter> pathParameters) {
        this.path = path;
        this.pathParameters = pathParameters;
    }

    public String getPath() {
        return path;
    }

    public Collection<PathParameter> getPathParameters() {
        return ImmutableList.copyOf(pathParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path, pathParameters);
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
        return Objects.equal(this.path, other.path) && Objects.equal(this.pathParameters, other.pathParameters);
    }

    @Override
    public String toString() {
        if (pathParameters.isEmpty()) {
            return path;
        }
        return String.format("%s (%s)", path, Joiner.on(",").join(pathParameters));
    }
}
