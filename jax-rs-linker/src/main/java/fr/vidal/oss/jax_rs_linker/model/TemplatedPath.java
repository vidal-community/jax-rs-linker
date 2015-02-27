package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.functions.PathParameterToName;
import fr.vidal.oss.jax_rs_linker.predicates.PathParameterPredicate;

import java.util.Collection;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.filter;
import static java.lang.String.format;

public class TemplatedPath<T extends PathParameters> {

    private final String path;
    private final Collection<PathParameter> parameters;

    public TemplatedPath(String path, Collection<PathParameter> parameters) {
        this.path = path;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    public TemplatedPath<T> replace(T parameter, String value) {
        checkState(!parameters.isEmpty(), "No more parameters to replace");
        validateParamValue(parameter.regex(), value);

        return new TemplatedPath<>(
            path.replace(placeholder(parameter.placeholder()), value),
            filter(parameters, not(PathParameterPredicate.byName(parameter.placeholder())))
        );
    }

    private void validateParamValue(Optional<String> regex, String value) {
        if(regex.isPresent()) {
            Pattern pattern = Pattern.compile(regex.get());
            if(!pattern.matcher(value).matches()) {
                throw new IllegalArgumentException(String.format("The given value doesn't match the parameter regex: %s", regex.get()));
            }
        }
    }

    public String value() {
        checkState(parameters.isEmpty(), format("Parameters to replace: %s", parameterNames()));
        return path;
    }

    private String parameterNames() {
        return FluentIterable.from(parameters)
                .transform(PathParameterToName.TO_NAME)
                .join(Joiner.on(","));
    }

    private String placeholder(String name) {
        return format("{%s}", name);
    }

}
