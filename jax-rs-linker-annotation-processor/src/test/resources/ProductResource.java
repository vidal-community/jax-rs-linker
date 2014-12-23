package net.biville.florent.jax_rs_linker.processor;

import net.biville.florent.jax_rs_linker.model.Self;
import net.biville.florent.jax_rs_linker.model.SubResource;

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

    @SubResource("net.biville.florent.jax_rs_linker.processor.BrandResource")
    @Path("/{id}/brand")
    @GET
    public void getBrandByProductId(@PathParam("id") int productId) {

    }
}
