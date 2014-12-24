package net.biville.florent.jax_rs_linker.functions;

import com.google.common.base.Function;
import net.biville.florent.jax_rs_linker.model.ClassName;

import javax.annotation.Nullable;

import static net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor.GENERATED_CLASSNAME_SUFFIX;

public enum ClassNameToLinkerName implements Function<ClassName, String> {
    TO_LINKER_NAME;

    @Nullable
    @Override
    public String apply(ClassName className) {
        return className.getName() + GENERATED_CLASSNAME_SUFFIX;
    }
}
