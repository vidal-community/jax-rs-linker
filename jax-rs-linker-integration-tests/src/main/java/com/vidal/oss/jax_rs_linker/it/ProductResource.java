package com.vidal.oss.jax_rs_linker.it;

import com.vidal.oss.jax_rs_linker.Linkers;
import com.vidal.oss.jax_rs_linker.api.Self;
import com.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static com.vidal.oss.jax_rs_linker.Linkers.productResourceLinker;

@Path("/product")
public class ProductResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Product " + String.valueOf(id);
    }

    @SubResource(BrandResource.class)
    @Path("/{id}/brand")
    @GET
    public String getBrandByProductId(@PathParam("id") int productId) {
        return "Brand for Product " + String.valueOf(productId);
    }

    @SubResource(CompanyResource.class)
    @Path("/{id}/company")
    @GET
    public String getCompanyByProductId(@PathParam("id") int productId) {
        return "Company for Product " + productId;
    }

    @Path("/{id}/self")
    @GET
    public String getSelfLink(@PathParam("id") int productId) {
        return productResourceLinker().self().replace(ProductResourcePathParameters.ID, String.valueOf(productId)).value();
    }

    @Path("/{id}/related-company")
    @GET
    public String getRelatedLink(@PathParam("id") int productId) {
        return productResourceLinker().related(CompanyResource.class).get().replace(ProductResourcePathParameters.ID, String.valueOf(productId)).value();
    }
}
