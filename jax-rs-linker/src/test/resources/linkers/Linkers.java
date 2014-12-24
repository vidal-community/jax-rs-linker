
package net.biville.florent.jax_rs_linker;

import javax.annotation.Generated;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.biville.florent.jax_rs_linker.parser.BrandResourceLinker;
import net.biville.florent.jax_rs_linker.parser.ProductResourceLinker;

@Generated("net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor")
public class Linkers
        implements ServletContextListener {

    private String contextPath = "";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextPath = sce.getServletContext().getContextPath();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public BrandResourceLinker brandResourceLinker() {
        return new BrandResourceLinker(contextPath);
    }

    public ProductResourceLinker productResourceLinker() {
        return new ProductResourceLinker(contextPath);
    }
}