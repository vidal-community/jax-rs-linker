import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("wtf")
@ExposedApplication(servletName = "omg")
public class Misconfiguration {

}
