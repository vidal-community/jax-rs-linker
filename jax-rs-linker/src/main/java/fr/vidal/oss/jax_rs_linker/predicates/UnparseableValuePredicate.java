package fr.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.model.SubResourceTarget;

import javax.annotation.Nullable;

public enum UnparseableValuePredicate implements Predicate<SubResourceTarget> {

    IS_UNPARSEABLE;

    @Override
    public boolean apply(SubResourceTarget input) {
        return input.isUnparseable();
    }
}
