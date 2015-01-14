package fr.vidal.oss.jax_rs_linker.it;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class LinkerTest {

    private BrandResourceLinker brandLinker = new BrandResourceLinker();
    private ProductResourceLinker productLinker = new ProductResourceLinker();

    @Test
    public void should_return_self_link() throws Exception {
        String result = brandLinker.self().replace(BrandResourcePathParameters.ID, "super-brand").value();

        assertThat(result).isEqualTo("/brand/super-brand");
    }

    @Test
    public void should_return_sub_resource_link_for_brand() throws Exception {
        Optional<TemplatedPath<ProductResourcePathParameters>> brandLink = productLinker.related(BrandResource.class);
        assertThat(brandLink).isPresent();

        String result = brandLink.get().replace(ProductResourcePathParameters.ID, "product-id").value();
        assertThat(result).isEqualTo("/product/product-id/brand");
    }

    @Test
    public void should_return_sub_resource_link_for_company() throws Exception {
        Optional<TemplatedPath<ProductResourcePathParameters>> companyLink = productLinker.related(CompanyResource.class);
        assertThat(companyLink).isPresent();

        String result = companyLink.get().replace(ProductResourcePathParameters.ID, "product-id").value();
        assertThat(result).isEqualTo("/product/product-id/company");
    }
}
