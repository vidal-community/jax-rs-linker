package fr.vidal.oss.jax_rs_linker.predicates;

import fr.vidal.oss.jax_rs_linker.model.Mapping;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public enum MappingByApiLinkTargetPredicate implements Predicate<Mapping> {
     BY_API_LINK_TARGET_PRESENCE;

    @Override
    public boolean test(@Nullable Mapping mapping) {
        return mapping != null && mapping.getApi().getApiLink().getTarget().isPresent();
    }
}
