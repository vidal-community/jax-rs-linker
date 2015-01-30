package fr.vidal.oss.jax_rs_linker.writer;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

public class EnumConstants {

    public static String constantName(String name) {
        return UPPER_CAMEL.to(UPPER_UNDERSCORE, name);
    }
}
