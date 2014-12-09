package net.biville.florent.jax_rs_linker.processor.parser;

import net.biville.florent.catalog.model.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/api/product")
public class ProductResource {

    @GET
    @Path("/{id}")
    @Self
    public String productById(@PathParam("id") int id) {
        return "";
    }
}
