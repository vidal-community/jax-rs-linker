package fr.vidal.oss.jax_rs_linker.writer;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumConstantsTest {

    @Test
    public void should_generate_enum_compatible_names() {
        assertThat(EnumConstants.constantName("camelCaseName")).isEqualTo("CAMEL_CASE_NAME");
        assertThat(EnumConstants.constantName("space name")).isEqualTo("SPACE_NAME");
        assertThat(EnumConstants.constantName("lowercase")).isEqualTo("LOWERCASE");
    }

    @Test
    public void cannot_handle_only_uppercase_form() {
        assertThat(EnumConstants.constantName("SPACE-COWBOY")).isNotEqualTo("SPACE_COWBOY");
    }
}
