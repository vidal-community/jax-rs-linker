package fr.vidal.oss.jax_rs_linker.api;

import java.util.regex.Pattern;

public interface PathParameters {
    String placeholder();
    Pattern regex();
}
