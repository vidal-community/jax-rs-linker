package com.vidal.oss.jax_rs_linker.parser;

import com.vidal.oss.jax_rs_linker.api.Self;
import com.vidal.oss.jax_rs_linker.api.SubResource;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class UnparseableResource {

    @GET
    @Self
    public void methodWithoutPath() {}

    @Self
    @Path("path")
    public void methodWithoutHttpVerb() {}

    @POST
    @Path("path2")
    public void methodWithoutLinkTypes() {}

    @POST
    @Path("path2")
    @Self
    @SubResource(ProductResource.class)
    public void methodWithTooManyLinkTypes() {}
}
