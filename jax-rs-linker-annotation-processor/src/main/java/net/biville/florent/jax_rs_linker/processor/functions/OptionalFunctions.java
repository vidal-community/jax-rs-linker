package net.biville.florent.jax_rs_linker.processor.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import javax.annotation.Nullable;

public class OptionalFunctions {

    public static <T> Function<Optional<T>, T> INTO_UNWRAPPED() {
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
