package com.vidal.oss.jax_rs_linker.parser;

import javax.ws.rs.Path;

@Path("/compound")
class CompoundPathClass {

    @Path("/path")
    public void something() {}
}
