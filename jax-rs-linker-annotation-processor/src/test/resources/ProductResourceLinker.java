package net.biville.florent.jax_rs_linker.processor;

import com.google.common.base.Optional;
import net.biville.florent.jax_rs_linker.processor.functions.ClassToName;
import net.biville.florent.jax_rs_linker.model.ApiPath;
import net.biville.florent.jax_rs_linker.model.ClassName;
import net.biville.florent.jax_rs_linker.model.Mapping;

import javax.annotation.Generated;
import java.util.Arrays;

@Generated("net.biville.florent.jax_rs_linker.processor.RestAnnotationProcessor")
public class ProductResourceLinker {

    private Map<ClassName, Mapping> relatedMappings;

    public TemplatedPath self() {
        return new TemplatedPath("/product/{id}", Arrays.asList("id"));
    }

    public Optional<TemplatedPath> related(Class<?> resourceClass) {
        Mapping mapping = relatedMappings.get(ClassToName.INSTANCE(resourceClass))
        if (mapping == null) {
            return Optional.<>absent();
        }

        ApiPath path = mapping.getApi().getApiPath();
        return Optional.of(
                new TemplatedPath(path.getPath(), path.getPathParameters())
        );
    }

}