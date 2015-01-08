import com.vidal.oss.jax_rs_linker.api.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/selfie")
public class SelfObsessedResource {

    @Self
    @Path("/{id}")
    @GET
    public void getMe(@PathParam("id") int id) {}

    @Self
    @Path("/{id}/myself")
    @GET
    public void getMoreSelf(@PathParam("id") int id) {}
}
