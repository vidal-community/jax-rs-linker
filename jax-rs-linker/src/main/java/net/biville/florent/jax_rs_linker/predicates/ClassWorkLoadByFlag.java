package net.biville.florent.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import java.util.Map;

public class ClassWorkLoadByFlag<K> implements Predicate<Map.Entry<K, Boolean>> {

    private final boolean flag;

    private ClassWorkLoadByFlag(boolean flag) {
        this.flag = flag;
    }

    public static <K> ClassWorkLoadByFlag<K> byFlag(boolean flag) {
        return new ClassWorkLoadByFlag<>(flag);
    }

    @Override
    public boolean apply(Map.Entry<K, Boolean> entry) {
        return flag == entry.getValue();
    }
}
