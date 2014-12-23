package net.biville.florent.jax_rs_linker.predicates;

import com.google.common.base.Predicate;
import net.biville.florent.jax_rs_linker.model.PathParameter;

public class PathParameterPredicate implements Predicate<PathParameter> {

    private final String parameter;

    private PathParameterPredicate(String parameter) {
        this.parameter = parameter;
    }

    public static Predicate<PathParameter> byName(String parameter) {
        return new PathParameterPredicate(parameter);
    }

    @Override
    public boolean apply(PathParameter input) {
        return parameter.equals(input.getName());
    }
}
