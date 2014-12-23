package net.biville.florent.jax_rs_linker.it;

import net.biville.florent.jax_rs_linker.model.PathArgument;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkerTest {

    private BrandResourceLinker linker = new BrandResourceLinker();

    @Test
    public void should_return_self_link() throws Exception {
        String result = linker.self().replace(new PathArgument("id", "super-brand")).value();

        assertThat(result).isEqualTo("/brand/super-brand");
    }
}
