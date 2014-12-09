package net.biville.florent.jax_rs_linker.processor.functions;

import com.google.common.base.Function;
import net.biville.florent.jax_rs_linker.processor.model.Mapping;
import net.biville.florent.jax_rs_linker.processor.model.ClassName;

public enum MappingToClassName implements Function<Mapping, ClassName> {

    INTO_CLASS_NAME;

    @Override
    public ClassName apply(Mapping input) {
        return input.getJavaLocation().getClassName();
    }
}
