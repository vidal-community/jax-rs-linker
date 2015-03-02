package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.*;

@Path("/person")
public class PersonResource {

    @Self
    @Path("/{id}")
    @GET
    public void getById(@PathParam("id") int id, @QueryParam("alive-flag") boolean alive) {

    }

    @SubResource(PersonResource.class)
    @Path("/name/{firstName:.*}")
    @GET
    public void getByFirstName(@PathParam("firstName") String firstName) {

    }
}
