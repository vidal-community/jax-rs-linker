package net.biville.florent.jax_rs_linker.it;

import org.junit.Test;

import static net.biville.florent.jax_rs_linker.api.PathArgument.argument;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkerTest {

    private BrandResourceLinker linker = new BrandResourceLinker();

    @Test
    public void should_return_self_link() throws Exception {
        String result = linker.self().replace(argument("id", "super-brand")).value();

        assertThat(result).isEqualTo("/brand/super-brand");
    }
}
