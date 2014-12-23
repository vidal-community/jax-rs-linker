package net.biville.florent.jax_rs_linker.predicates;

import com.google.common.base.Predicate;
import net.biville.florent.jax_rs_linker.model.Mapping;

import javax.annotation.Nullable;

public enum MappingByApiLinkTargetPredicate implements Predicate<Mapping> {
     BY_API_LINK_TARGET_PRESENCE;

    @Override
    public boolean apply(@Nullable Mapping mapping) {
        return mapping != null && mapping.getApi().getApiLink().getTarget().isPresent();
    }
}
