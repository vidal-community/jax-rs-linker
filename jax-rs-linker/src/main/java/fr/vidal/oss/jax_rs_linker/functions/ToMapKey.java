package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;

import java.util.Map;


public class ToMapKey<K> implements Function<Map.Entry<K,?>, K> {

    private ToMapKey() {}

    public static <K> ToMapKey<K> intoKey() {
        return new ToMapKey<>();
    }

    @Override
    public K apply(Map.Entry<K, ?> entry) {
        return entry.getKey();
    }
}
