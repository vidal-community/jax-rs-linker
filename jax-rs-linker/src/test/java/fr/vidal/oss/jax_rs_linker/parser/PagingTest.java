package fr.vidal.oss.jax_rs_linker.parser;

import javax.ws.rs.*;

public class PagingTest {
    private final int pageStart;
    private final int pageSize;

    public PagingTest(@DefaultValue("1") @QueryParam("start-page") int pageStart,
                      @DefaultValue("25") @QueryParam("page-size") int pageSize) {

        this.pageStart = pageStart;
        this.pageSize = pageSize;
    }

}
