package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import javax.annotation.Nullable;

public class OptionalFunctions {

    public static <T> Function<Optional<T>, T> intoUnwrapped() {
        return new Function<Optional<T>, T>() {
            @Nullable
            @Override
            public T apply(@Nullable Optional<T> input) {
                if (input == null) {
                    return null;
                }
                return input.get();
            }
        };
    }
}
