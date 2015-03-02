package fr.vidal.oss.jax_rs_linker.parser;

import javax.ws.rs.*;

public class PagingTest {
    private final int pageStart;
    private final int pageSize;

    @QueryParam("is-applicable")
    private boolean isApplicable = false;

    public PagingTest(@DefaultValue("1") @QueryParam("start-page") int pageStart,
                      @DefaultValue("25") @QueryParam("page-size") int pageSize) {

        this.pageStart = pageStart;
        this.pageSize = pageSize;
    }

    @QueryParam("to be ignored")
    public void testOfAnnotatedMethodIgnored(String value) {

    }

    @QueryParam("haters-gonna-hate")
    public void setUselessParameter(String value) {
    }

    @QueryParam("non-void-method")
    public String setNonVoidMethod(String value) {
        return "";
    }

}
