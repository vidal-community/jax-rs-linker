package fr.vidal.oss.jax_rs_linker.it;

import static fr.vidal.oss.jax_rs_linker.it.BrandResourceLinker.brandResourceLinker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BrandResourceLinkerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void no_exception_is_thrown_when_argument_matches_regex() {
        brandResourceLinker()
                .relatedBrandResourceByZipCode()
                .replace(BrandResourcePathParameters.CODE, "aBc");
    }

    @Test
    public void illegal_argument_exception_is_thrown_when_regex_is_not_matched() {
        thrown.expect(IllegalArgumentException.class);
        brandResourceLinker()
                .relatedBrandResourceByZipCode()
                .replace(BrandResourcePathParameters.CODE, "aBD2");
    }
}
