package query_parameters_misdetection;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.*;

public class PeopleResource {

    @GET
    @Path("/{id}")
    @Self
    public Stuff searchById(@PathParam("id") Integer id) {
        return null;
    }

    @GET
    @Path("/{id}/friends")
    @SubResource(value = PeopleResource.class, qualifier = "friends")
    public Stuff findFriendsFilteredByCityOrCountry(@PathParam("id") Integer id,
                                                    @BeanParam Localization localization) {
        return null;
    }

    private static class Stuff {}

    private static class Localization {
        private final String country;
        private final String city;

        public Localization(@QueryParam("pays") String country,
                            @QueryParam("ville") String city) {

            this.country = country;
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }
    }
}
