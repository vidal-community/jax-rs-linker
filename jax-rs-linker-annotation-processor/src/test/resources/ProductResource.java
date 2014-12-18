package net.biville.florent.jax_rs_linker.processor;

import net.biville.florent.jax_rs_linker.model.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/product")
public class ProductResource {

    @Self
    @Path("/{id}")
    @GET
    public void getById(@PathParam("id") int id) {

    }

    @Self
    @Path("/{id}/brand")
    @GET
    public void getBrandByProductId(@PathParam("id") int productId) {

    }
}
