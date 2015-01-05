package net.biville.florent.jax_rs_linker.it;

import net.biville.florent.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
@ExposedApplication(name = "net.biville.florent.jax_rs_linker.it.jersey.Configuration")
public class Configuration extends Application {
}
