
package net.biville.florent.jax_rs_linker;

import javax.annotation.Generated;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import net.biville.florent.jax_rs_linker.parser.BrandResourceLinker;
import net.biville.florent.jax_rs_linker.parser.ProductResourceLinker;

@WebListener
@Generated("net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor")
public class Linkers
        implements ServletContextListener {

    private static String contextPath = "";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextPath = sce.getServletContext().getContextPath();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public static BrandResourceLinker brandResourceLinker() {
        return new BrandResourceLinker(contextPath);
    }

    public static ProductResourceLinker productResourceLinker() {
        return new ProductResourceLinker(contextPath);
    }
}
