package fr.vidal.oss.jax_rs_linker.predicates;

import fr.vidal.oss.jax_rs_linker.model.ApiLinkType;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import java.util.function.Predicate;

public enum HasSelfMapping implements Predicate<Mapping> {
    HAS_SELF;

    @Override
    public boolean test(Mapping input) {
        return input.getApi().getApiLink().getApiLinkType() == ApiLinkType.SELF;
    }
}
