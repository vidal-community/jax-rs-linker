package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.*;

@Path("/api/product")
public class ProductResource {

    @GET
    @Path("/{id}")
    @Self
    public String productById(@PathParam("id") int id, @BeanParam PagingTest paging) {
        return "";
    }
}
