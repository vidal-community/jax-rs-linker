package fr.vidal.oss.jax_rs_linker.parser;

import javax.ws.rs.*;

public class JaxRsAnnotatedClass {

    @GET
    public void get() {}

    @POST
    public void post() {}

    @HEAD
    public void head() {}

    @OPTIONS
    public void options() {}

    @PUT
    public void put() {}

    @DELETE
    public void delete() {}

    public void nothing() {}
}
