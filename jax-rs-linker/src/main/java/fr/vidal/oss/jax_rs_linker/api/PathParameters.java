package fr.vidal.oss.jax_rs_linker.api;

import com.google.common.base.Optional;

import java.util.regex.Pattern;

public interface PathParameters {
    String placeholder();
    Optional<Pattern> regex();
}
