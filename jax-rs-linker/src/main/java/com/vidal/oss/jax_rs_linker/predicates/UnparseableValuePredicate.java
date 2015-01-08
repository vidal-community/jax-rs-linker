package com.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

public enum UnparseableValuePredicate implements Predicate<String> {

    IS_UNPARSEABLE;

    @Override
    public boolean apply(@Nullable String input) {
        return "<error>".equals(input);
    }
}
