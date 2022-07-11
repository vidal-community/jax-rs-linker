package fr.vidal.oss.jax_rs_linker.base;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PreconditionsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void do_not_throw_IllegalStateException_when_condition_is_true() {
        Preconditions.checkState(true, () -> "should not happen!");
    }

    @Test
    public void throw_IllegalStateException_when_condition_is_false() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("must fail!");

        Preconditions.checkState(false, () -> "must fail!");
    }
}
