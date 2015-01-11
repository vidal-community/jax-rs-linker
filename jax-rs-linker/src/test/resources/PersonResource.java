import com.vidal.oss.jax_rs_linker.api.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/person")
public class BrandResource {

    @Self
    @Path("/{id}")
    @GET
    public void getById(@PathParam("id") int id) {

    }

    @Self
    @Path("/name/{firstName}")
    @GET
    public void getByFirstName(@PathParam("firstName") String firstName) {

    }
}
