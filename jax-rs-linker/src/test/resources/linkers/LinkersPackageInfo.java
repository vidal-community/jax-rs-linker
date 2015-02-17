
package fr.vidal.oss.jax_rs_linker;

import fr.vidal.oss.jax_rs_linker.parser.BrandResourceLinker;
import fr.vidal.oss.jax_rs_linker.servlet.ContextPaths;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public final class Linkers implements ServletContextListener {
    private static String contextPath = "";

    private static String applicationName = ApplicationName.get();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextPath = ContextPaths.contextPath(sce.getServletContext(), applicationName);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public static BrandResourceLinker brandResourceLinker() {
        return new BrandResourceLinker(contextPath);
    }
}
