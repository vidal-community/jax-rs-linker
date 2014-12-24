
package net.biville.florent.jax_rs_linker.parser;

import com.google.common.base.Optional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import net.biville.florent.jax_rs_linker.functions.ClassToName;
import net.biville.florent.jax_rs_linker.model.ApiPath;
import net.biville.florent.jax_rs_linker.model.ClassName;
import net.biville.florent.jax_rs_linker.model.PathParameter;
import net.biville.florent.jax_rs_linker.model.TemplatedPath;


@Generated("net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor")
public class ProductResourceLinker {

    private final Map<ClassName, ApiPath> relatedMappings = new HashMap<>();

    public ProductResourceLinker() {
        relatedMappings.put(
                ClassName.valueOf("net.biville.florent.jax_rs_linker.parser.BrandResource"),
                new ApiPath("/product/{id}/brand", Arrays.asList(new PathParameter(ClassName.valueOf("int"), "id"))));
    }

    public TemplatedPath self() {
        return new TemplatedPath("/product/{id}", Arrays.asList(new PathParameter(ClassName.valueOf("int"), "id")));
    }

    public Optional<TemplatedPath> related(Class<?> resourceClass) {
        ApiPath path = relatedMappings.get(ClassName.valueOf(ClassToName.INSTANCE.apply(resourceClass)));
        if (path == null) {
            return Optional.<TemplatedPath>absent();
        }
        return Optional.of(new TemplatedPath(path.getPath(), path.getPathParameters()));
    }
}