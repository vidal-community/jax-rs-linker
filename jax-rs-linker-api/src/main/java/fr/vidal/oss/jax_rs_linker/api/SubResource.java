package fr.vidal.oss.jax_rs_linker.api;

import java.lang.annotation.Documented;

@Documented
public @interface SubResource {

    /**
     * Targetted JAX-RS resource
     */
    Class<?> value();

    /**
     * Qualifier when several {@link fr.vidal.oss.jax_rs_linker.api.SubResource}
     * are present in the same class.
     * Qualifier name will be part of Linker method names, be sure to use compatible characters.
     */
    String qualifier() default "";
}
