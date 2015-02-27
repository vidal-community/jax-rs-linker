package fr.vidal.oss.jax_rs_linker.api;

import com.google.common.base.Optional;

public interface PathParameters {
    String placeholder();
    Optional<String> regex();
}
