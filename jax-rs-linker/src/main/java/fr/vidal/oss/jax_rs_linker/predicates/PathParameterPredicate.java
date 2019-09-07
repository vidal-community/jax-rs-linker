package fr.vidal.oss.jax_rs_linker.predicates;

import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import java.util.function.Predicate;

public class PathParameterPredicate implements Predicate<PathParameter> {

    private final String parameter;

    private PathParameterPredicate(String parameter) {
        this.parameter = parameter;
    }

    public static Predicate<PathParameter> byName(String parameter) {
        return new PathParameterPredicate(parameter);
    }

    @Override
    public boolean test(PathParameter input) {
        return parameter.equals(input.getName());
    }
}
