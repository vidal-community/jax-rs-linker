package parameter_visitor;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class StupidResource {

    @GET
    @Path("/{foo}/{bar}/{baz}")
    public Response doSomething(@BeanParam SuperBeanParam superBeanParam) {
        return null;
    }
}
