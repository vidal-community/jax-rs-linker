package net.biville.florent.jax_rs_linker.processor.parser;

import javax.ws.rs.Path;

@Path("/compound")
class CompoundPathClass {

    @Path("/path")
    public void something() {}
}
