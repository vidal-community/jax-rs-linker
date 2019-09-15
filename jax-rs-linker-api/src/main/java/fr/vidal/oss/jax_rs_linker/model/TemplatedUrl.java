package fr.vidal.oss.jax_rs_linker.model;

import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import fr.vidal.oss.jax_rs_linker.predicates.PathParameterPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.vidal.oss.jax_rs_linker.base.Preconditions.checkState;
import static fr.vidal.oss.jax_rs_linker.functions.QueryParametersToQueryString.TO_QUERY_STRING;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class TemplatedUrl<T extends PathParameters, U extends QueryParameters> {

    private final String path;
    private final Collection<PathParameter> pathParameters;
    private final Map<String, Collection<String>> queryParameters = new LinkedHashMap<>();

    public TemplatedUrl(String path, Collection<PathParameter> pathParameters, Collection<QueryParameter> queryParameters) {
        this.path = path;
        this.pathParameters = new ArrayList<>(pathParameters);
        for (QueryParameter queryParameter : queryParameters) {
            this.queryParameters.put(queryParameter.getName(), new ArrayList<>());
        }
    }

    private TemplatedUrl(String path, Collection<PathParameter> pathParameters, Map<String, Collection<String>> queryParameters) {
        this.path = path;
        this.pathParameters = pathParameters;
        this.queryParameters.putAll(queryParameters);
    }

    public TemplatedUrl<T,U> replace(T parameter, String value) {
        checkState(!pathParameters.isEmpty(), "No more path parameters to replace");
        validateParamValue(parameter.regex(), value);

        return new TemplatedUrl<>(
            path.replace(placeholder(parameter.placeholder()), value),
            pathParameters.stream().filter(PathParameterPredicate.byName(parameter.placeholder()).negate()).collect(toList()),
            queryParameters);
    }

    public TemplatedUrl<T,U> append(U queryParameter, String value) {
        this.queryParameters.get(queryParameter.value()).add(value);
        return new TemplatedUrl<>(
            path,
            pathParameters,
            queryParameters);
    }

    public TemplatedUrl<T,U> appendAll(U queryParameter, Collection<String> value) {
        this.queryParameters.get(queryParameter.value()).addAll(value);
        return new TemplatedUrl<>(
            path,
            pathParameters,
            queryParameters);
    }

    public String value() {
        checkState(pathParameters.isEmpty(), format("Parameters to replace: %s", parameterNames()));
        return path + TO_QUERY_STRING.apply(queryParameters);
    }

    private void validateParamValue(Pattern regex, String value) {
        if (regex != null && !regex.matcher(value).matches()) {
            throw new IllegalArgumentException(String.format("The given value doesn't match the parameter regex: %s", regex));
        }
    }

    private String parameterNames() {
        return pathParameters.stream()
            .map(PathParameter::getName)
            .collect(Collectors.joining(","));
    }

    private String placeholder(String name) {
        return format("{%s}", name);
    }

}
