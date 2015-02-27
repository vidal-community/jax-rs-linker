package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import fr.vidal.oss.jax_rs_linker.functions.PathParameterToName;
import fr.vidal.oss.jax_rs_linker.predicates.PathParameterPredicate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.filter;
import static fr.vidal.oss.jax_rs_linker.functions.QueryParametersToQueryString.TO_QUERY_STRING;
import static java.lang.String.format;

public class TemplatedUrl<T extends PathParameters, U extends QueryParameters> {

    private final String path;
    private final Collection<PathParameter> pathParameters;
    private final Map<String, Collection<String>> queryParameters = new LinkedHashMap<>();

    public TemplatedUrl(String path, Collection<PathParameter> pathParameters, Collection<QueryParameter> queryParameters) {
        this.path = path;
        this.pathParameters = ImmutableList.copyOf(pathParameters);
        for (QueryParameter queryParameter : queryParameters) {
            this.queryParameters.put(queryParameter.getName(), Lists.<String>newArrayList());
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
            filter(pathParameters, not(PathParameterPredicate.byName(parameter.placeholder()))),
            queryParameters);
    }

    public TemplatedUrl<T,U> append(U queryParameter, String value) {
        this.queryParameters.get(queryParameter.value()).add(value);
        return new TemplatedUrl<>(
            path,
            pathParameters,
            queryParameters);
    }

    public String value() {
        checkState(pathParameters.isEmpty(), format("Parameters to replace: %s", parameterNames()));
        return path + TO_QUERY_STRING.apply(queryParameters);
    }

    private void validateParamValue(Optional<Pattern> regex, String value) {
        if (regex.isPresent()) {
            if (!regex.get().matcher(value).matches()) {
                throw new IllegalArgumentException(String.format("The given value doesn't match the parameter regex: %s", regex.get()));
            }
        }
    }

    private String parameterNames() {
        return FluentIterable.from(pathParameters)
                .transform(PathParameterToName.TO_NAME)
                .join(Joiner.on(","));
    }

    private String placeholder(String name) {
        return format("{%s}", name);
    }

}
