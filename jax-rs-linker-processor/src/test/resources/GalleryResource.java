import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/gallery")
public class GalleryResource {

    @Self
    @Path("/{id}")
    @GET
    public void getById(@PathParam("id") int id) {

    }

    @SubResource(SelfObsessedResource.class)
    @Path("/{id}/selfie")
    @GET
    public void getSelfieByGalleryId(@PathParam("id") int galleryId) {

    }
}
