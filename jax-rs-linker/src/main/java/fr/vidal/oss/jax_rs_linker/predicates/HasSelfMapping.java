package fr.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;
import fr.vidal.oss.jax_rs_linker.model.ApiLinkType;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import javax.annotation.Nullable;

public enum HasSelfMapping implements Predicate<Mapping> {
    HAS_SELF;

    @Override
    public boolean apply(Mapping input) {
        return input.getApi().getApiLink().getApiLinkType() == ApiLinkType.SELF;
    }
}
