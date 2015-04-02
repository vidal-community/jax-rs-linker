package parameter_visitor;

import javax.ws.rs.PathParam;

public class SuperBeanParam {

    @PathParam("{foo}")
    private final String field1 = "foo!";

    @PathParam("{bar}")
    private final String field2 = "bar!";

    private String field3;

    @PathParam("{baz}")
    public void setBaz(String field3) {
        this.field3 = field3;
    }
}
