package net.biville.florent.jax_rs_linker.api;

import java.lang.annotation.Documented;

@Documented
public @interface ExposedApplication {

    public String name();
}