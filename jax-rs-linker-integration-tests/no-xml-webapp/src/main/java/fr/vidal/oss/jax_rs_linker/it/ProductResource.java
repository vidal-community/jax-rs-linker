package fr.vidal.oss.jax_rs_linker.it;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import static fr.vidal.oss.jax_rs_linker.it.ProductResourceLinker.productResourceLinker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


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
        return productResourceLinker().relatedCompanyResource().replace(ProductResourcePathParameters.ID, String.valueOf(productId)).value();
    }
}
