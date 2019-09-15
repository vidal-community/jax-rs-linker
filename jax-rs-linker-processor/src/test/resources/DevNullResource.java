package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/dev")
public class DevNullResource {

    @Self
    @Path("/null")
    @GET
    public void getByNothing() {

    }
}
