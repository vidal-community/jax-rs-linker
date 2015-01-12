package com.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.vidal.oss.jax_rs_linker.api.PathParameters;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.filter;
import static com.vidal.oss.jax_rs_linker.predicates.PathParameterPredicate.byName;
import static java.lang.String.format;
import static com.vidal.oss.jax_rs_linker.functions.PathParameterToName.TO_NAME;

public class TemplatedPath {

    private final String path;
    private final Collection<PathParameter> parameters;

    public TemplatedPath(String path, Collection<PathParameter> parameters) {
        this.path = path;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    public <T extends PathParameters> TemplatedPath replace(T parameter, String value) {
        checkState(!parameters.isEmpty(), "No more parameters to replace");
        
        return new TemplatedPath(
            path.replace(placeholder(parameter.placeholder()), value),
            filter(parameters, not(byName(parameter.placeholder())))
        );
    }

    public String value() {
        checkState(parameters.isEmpty(), format("Parameters to replace: %s", parameterNames()));
        return path;
    }

    private String parameterNames() {
        return FluentIterable.from(parameters)
                .transform(TO_NAME)
                .join(Joiner.on(","));
    }

    private String placeholder(String name) {
        return format("{%s}", name);
    }

}
