package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/brand")
public class BrandResource {

    @Self
    @Path("/{id}")
    @GET
    public void getById(@PathParam("id") int id) {

    }

    @SubResource(BrandResource.class)
    @Path("/{code}")
    @GET
    public void getByCode(@PathParam("code") int code) {

    }

    @SubResource(value = BrandResource.class, qualifier = "zip")
    @Path("/{zip}")
    @GET
    public void getByZip(@PathParam("zip") int zip) {

    }
}
