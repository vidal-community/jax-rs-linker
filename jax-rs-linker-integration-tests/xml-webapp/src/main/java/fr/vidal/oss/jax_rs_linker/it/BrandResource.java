package fr.vidal.oss.jax_rs_linker.it;


import fr.vidal.oss.jax_rs_linker.api.Self;

import static fr.vidal.oss.jax_rs_linker.it.BrandResourceLinker.brandResourceLinker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/brand")
public class BrandResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return brandResourceLinker()
            .self()
            .replace(BrandResourcePathParameters.ID, String.valueOf(id))
            .value();
    }
}
