package fr.vidal.oss.jax_rs_linker.functions;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class OptionalFunctions {

    private OptionalFunctions() {
    }

    public static <T> Function<Optional<T>, T> intoUnwrapped() {
        return new Function<Optional<T>, T>() {
            @Nullable
            @Override
            public T apply(Optional<T> input) {
                if (input == null) {
                    return null;
                }
                return input.orElse(null);
            }
        };
    }
}
