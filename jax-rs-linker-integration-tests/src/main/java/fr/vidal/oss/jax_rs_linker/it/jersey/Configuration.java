package fr.vidal.oss.jax_rs_linker.it.jersey;

import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
@ExposedApplication
public class Configuration extends Application {
}
