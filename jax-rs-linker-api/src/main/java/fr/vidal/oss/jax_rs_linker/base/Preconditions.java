package fr.vidal.oss.jax_rs_linker.base;

import java.util.function.Supplier;

public class Preconditions {

    public static void checkState(boolean expression, Supplier<String> errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage.get());
        }
    }

    private Preconditions() {
        // Hide me!
    }
}
