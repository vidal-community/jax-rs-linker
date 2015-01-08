package com.vidal.oss.jax_rs_linker.it;


import com.vidal.oss.jax_rs_linker.api.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/company")
public class CompanyResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Company " + String.valueOf(id);
    }
}
