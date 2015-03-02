package fr.vidal.oss.jax_rs_linker.functions;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalFunctionsTest {

    @Test
    public void should_return_null_if_null_input_is_provided() {
        assertThat(OptionalFunctions.intoUnwrapped().apply(null)).isNull();
    }
}
