package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.Self;

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
