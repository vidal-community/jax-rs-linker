package net.biville.florent.jax_rs_linker.parser;

import javax.ws.rs.Path;

@Path("/and-inherited")
public class InheritedCompoundPathClass extends CompoundPathClass {

    @Path("path-for-the-win")
    public void bar() {

    }
}
