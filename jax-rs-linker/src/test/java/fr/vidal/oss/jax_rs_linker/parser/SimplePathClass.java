package fr.vidal.oss.jax_rs_linker.parser;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

class SimplePathClass {

    @Path("simple-path")
    public void hello() {

    }

    @Path("parameterized/{id}")
    public void world(@PathParam("id") int id) {

    }

    @Path("/{super}/{params}/{everywhere}")
    public void monde(Long notAParam,
                      @PathParam("super") String superParam,
                      @PathParam("params") int[] params,
                      boolean truthiness,
                      @PathParam("everywhere") Number number) {

    }
}
