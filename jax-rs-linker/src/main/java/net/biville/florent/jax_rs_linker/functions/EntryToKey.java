package net.biville.florent.jax_rs_linker.functions;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class EntryToKey<K,V> implements Function<Map.Entry<K,V>,K> {

    private EntryToKey() {}

    public static <K,V> Function<Map.Entry<K,V>,K> intoKey() {
        return new EntryToKey<>();
    }

    @Nullable
    @Override
    public K apply(Map.Entry<K, V> entry) {
        return entry.getKey();
    }
}
