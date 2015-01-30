package fr.vidal.oss.jax_rs_linker.api;

import java.lang.annotation.Documented;

@Documented
public @interface ExposedApplication {

    /**
     * Refers to the mapped servlet name.
     * Note that classes annotated with {@code javax.ws.rs.ApplicationPath} should not define any servlet name.
     */
    public String servletName() default "";
}
