package net.biville.florent.jax_rs_linker.api;

import java.lang.annotation.Documented;

@Documented
public @interface SubResource {

    public Class<?> value();
}
