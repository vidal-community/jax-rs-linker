
package net.biville.florent.jax_rs_linker.processor;

import com.google.common.base.Optional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import net.biville.florent.jax_rs_linker.model.ApiPath;
import net.biville.florent.jax_rs_linker.model.ClassName;
import net.biville.florent.jax_rs_linker.model.PathParameter;
import net.biville.florent.jax_rs_linker.model.TemplatedPath;
import net.biville.florent.jax_rs_linker.processor.functions.ClassToName;


@Generated("net.biville.florent.jax_rs_linker.processor.LinkerAnnotationProcessor")
public class ProductResourceLinker {

    private final Map<ClassName, ApiPath> relatedMappings = new HashMap<>();

    public ProductResourceLinker() {
        relatedMappings.put(
                ClassName.valueOf("net.biville.florent.jax_rs_linker.processor.BrandResource"),
                new ApiPath("/product/{id}/brand", Arrays.asList(new PathParameter(ClassName.valueOf("int"), "id"))));
    }

    public TemplatedPath self() {
        return new TemplatedPath("/product/{id}", Arrays.asList(new PathParameter(ClassName.valueOf("int"), "id")));
    }

    public Optional<TemplatedPath> related(Class<?> resourceClass) {
        ApiPath path = relatedMappings.get(ClassToName.INSTANCE.apply(resourceClass));
        if (path == null) {
            return Optional.<TemplatedPath>absent();
        }
        return Optional.of(new TemplatedPath(path.getPath(), path.getPathParameters()));
    }
}