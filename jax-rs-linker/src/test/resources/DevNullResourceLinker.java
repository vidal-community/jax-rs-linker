package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.functions.ClassToName;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public class DevNullResourceLinker {

	private final Map<ClassName, ApiPath> relatedMappings = new HashMap<>();
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

	public Optional<TemplatedPath<NoPathParameters>> related(Class<?> resourceClass) {
		ApiPath path = relatedMappings.get(ClassName.valueOf(ClassToName.INSTANCE.apply(resourceClass)));
		if (path == null) {
			return Optional.<TemplatedPath<NoPathParameters>>absent();
		}
		return Optional.of(new TemplatedPath<NoPathParameters>(contextPath + path.getPath(), path.getPathParameters()));
	}
}