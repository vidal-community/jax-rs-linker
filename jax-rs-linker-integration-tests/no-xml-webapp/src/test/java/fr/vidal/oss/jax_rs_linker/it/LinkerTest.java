package fr.vidal.oss.jax_rs_linker.it;

import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;

import static fr.vidal.oss.jax_rs_linker.it.BrandResourceLinker.brandResourceLinker;
import static fr.vidal.oss.jax_rs_linker.it.ProductResourceLinker.productResourceLinker;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class LinkerTest {

    private BrandResourceLinker brandLinker = brandResourceLinker();
    private ProductResourceLinker productLinker = productResourceLinker();

    @Test
    public void should_return_self_link() throws Exception {
        String result = brandLinker.self().replace(BrandResourcePathParameters.ID, "super-brand").value();

        assertThat(result).isEqualTo("/brand/super-brand");
    }

    @Test
    public void should_return_sub_resource_link_for_brand() throws Exception {
        TemplatedUrl<ProductResourcePathParameters, NoQueryParameters> brandLink = productLinker.relatedBrandResource();

        String result = brandLink.replace(ProductResourcePathParameters.ID, "product-id").value();
        assertThat(result).isEqualTo("/product/product-id/brand");
    }

    @Test
    public void should_return_sub_resource_link_for_company() throws Exception {
        TemplatedUrl<ProductResourcePathParameters, NoQueryParameters> companyLink = productLinker.relatedCompanyResource();

        String result = companyLink.replace(ProductResourcePathParameters.ID, "product-id").value();
        assertThat(result).isEqualTo("/product/product-id/company");
    }
}
