package fr.vidal.oss.jax_rs_linker.base;

public class Preconditions {

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private Preconditions() {
        // Hide me!
    }
}
