package subresource_without_self;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/selfie")
public class SelflessResource {

    @SubResource(SelflessResource.class)
    @Path("/{id}")
    @GET
    public void getMe(@PathParam("id") int id) {}
}
