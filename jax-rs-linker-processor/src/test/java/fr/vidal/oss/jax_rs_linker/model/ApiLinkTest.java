package fr.vidal.oss.jax_rs_linker.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ApiLinkTest {

    @Test
    public void equals_contract() {
        EqualsVerifier.forClass(ApiLink.class).verify();
    }
}
