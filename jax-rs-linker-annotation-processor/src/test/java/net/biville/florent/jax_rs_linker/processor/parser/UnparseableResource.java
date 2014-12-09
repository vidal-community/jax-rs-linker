package net.biville.florent.jax_rs_linker.processor.parser;

import net.biville.florent.catalog.model.Self;
import net.biville.florent.catalog.model.SubResource;

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
    @SubResource("net.biville.florent.processor.parser.ProductResource")
    public void methodWithTooManyLinkTypes() {}
}
