package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;
import java.util.Arrays;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public class DevNullResourceLinker {

	private final String contextPath;

	public DevNullResourceLinker() {
		this("");
	}

	public DevNullResourceLinker(String contextPath) {
		this.contextPath = contextPath;
	}

	public TemplatedPath<NoPathParameters> self() {
		return new TemplatedPath<NoPathParameters>(contextPath + "/dev/null", Arrays.<PathParameter>asList());
	}

}