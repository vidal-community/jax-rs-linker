package net.biville.florent.jax_rs_linker.functions;

import com.google.common.base.Function;
import net.biville.florent.jax_rs_linker.model.ClassName;
import net.biville.florent.jax_rs_linker.model.Mapping;

public enum MappingToClassName implements Function<Mapping, ClassName> {

    INTO_CLASS_NAME;

    @Override
    public ClassName apply(Mapping input) {
        return input.getJavaLocation().getClassName();
    }
}
