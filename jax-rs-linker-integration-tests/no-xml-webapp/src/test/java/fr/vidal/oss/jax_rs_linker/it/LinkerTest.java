package fr.vidal.oss.jax_rs_linker.it;

import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;
import org.junit.Test;

import static fr.vidal.oss.jax_rs_linker.it.BrandResourceLinker.brandResourceLinker;
import static fr.vidal.oss.jax_rs_linker.it.CompanyResourceLinker.companyResourceLinker;
import static fr.vidal.oss.jax_rs_linker.it.ProductResourceLinker.productResourceLinker;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkerTest {

    private BrandResourceLinker brandLinker = brandResourceLinker();
    private ProductResourceLinker productLinker = productResourceLinker();
    private CompanyResourceLinker companyResourceLinker = companyResourceLinker();

    @Test
    public void should_return_self_link_for_brand() throws Exception {
        String result = brandLinker.self().replace(BrandResourcePathParameters.ID, "super-brand").value();

        assertThat(result).isEqualTo("/brand/super-brand");
    }

    @Test
    public void should_return_self_link_for_product() throws Exception {
        String result = productLinker.self().replace(ProductResourcePathParameters.ID, "super-product").value();

        assertThat(result).isEqualTo("/product/super-product");
    }

    @Test
    public void should_return_self_link_for_company() throws Exception {
        String result = companyResourceLinker.self().replace(CompanyResourcePathParameters.ID, "super-company").value();

        assertThat(result).isEqualTo("/company/super-company");
    }

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
        TemplatedUrl<ProductResourcePathParameters, ProductResourceQueryParameters> companyLink = productLinker.relatedCompanyResource();

        String result = companyLink.replace(ProductResourcePathParameters.ID, "product-id").value();
        assertThat(result).isEqualTo("/product/product-id/company");
    }

    @Test
    public void should_return_sub_resource_link_for_company_with_query_parameter() throws Exception {
        TemplatedUrl<ProductResourcePathParameters, ProductResourceQueryParameters> companyLink = productLinker.relatedCompanyResource();

        String result = companyLink
            .replace(ProductResourcePathParameters.ID, "product-id")
            .append(ProductResourceQueryParameters.Q_PARAMETER, "true")
            .value();
        assertThat(result).isEqualTo("/product/product-id/company?qParameter=true");
    }
}
