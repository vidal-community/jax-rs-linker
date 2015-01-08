package com.vidal.oss.jax_rs_linker.it;

import com.vidal.oss.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
@ExposedApplication(name = "com.vidal.oss.jax_rs_linker.it.jersey.Configuration")
public class Configuration extends Application {
}
