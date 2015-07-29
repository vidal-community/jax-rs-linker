package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

enum PatternToRegexString implements Function<Pattern, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(Pattern input) {
        return input.pattern();
    }
}
