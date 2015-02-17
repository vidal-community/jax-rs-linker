package fr.vidal.oss.jax_rs_linker.it;


import fr.vidal.oss.jax_rs_linker.Linkers;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/brand")
public class BrandResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id, @QueryParam("view") String view) {
        return Linkers.brandResourceLinker()
            .self()
            .replace(BrandResourcePathParameters.ID, String.valueOf(id))
            .append(BrandResourceQueryParameters.VIEW, view)
            .value();
    }

    @SubResource(BrandResource.class)
    @Path("/{brand:[a-zA-Z]+}")
    @GET
    public String getBy(@PathParam("brand") String brand) {
        return Linkers.brandResourceLinker()
            .relatedBrandResource()
            .replace(BrandResourcePathParameters.BRAND, brand)
            .value();
    }

    @SubResource(value = BrandResource.class, qualifier = "byCode")
    @Path("/{code:[a-cA-C]+}")
    @GET
    public String getByCode(@PathParam("code") String code) {
        return Linkers.brandResourceLinker()
            .relatedBrandResourceByCode()
            .replace(BrandResourcePathParameters.CODE, code)
            .value();
    }

    @SubResource(value = BrandResource.class, qualifier = "byZipCode")
    @Path("/{zip:[d-zD-Z]+}")
    @GET
    public String getByZip(@PathParam("zip") String zip) {
        return Linkers.brandResourceLinker()
            .relatedBrandResourceByZipCode()
            .replace(BrandResourcePathParameters.ZIP, zip)
            .value();
    }
}
