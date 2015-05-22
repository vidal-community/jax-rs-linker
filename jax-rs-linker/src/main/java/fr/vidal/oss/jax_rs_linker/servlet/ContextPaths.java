package fr.vidal.oss.jax_rs_linker.servlet;

import javax.servlet.ServletContext;
import java.util.Collection;

public class ContextPaths {

    public static String contextPath(ServletContext servletContext, String registeredKey) {
        String mappedPath = readMappingOrDefault(servletContext, registeredKey);
        return servletContext.getContextPath() + mappedPath;
    }

    /**
     * This is a workaround because of a current bug we have observed in
     * *some* environments with Spring Boot.
     *
     * This bug is filed:
     *   - here {@see https://github.com/softwarevidal/jax-rs-linker/issues/41}
     *   - and there {@see https://github.com/spring-projects/spring-boot/issues/3028}
     *
     * If no mapping is found, we rely on an external environment variable to return
     * a default value (variable: "LINKERS_DEFAULT_PATH").
     */
    @Deprecated
    private static String readMappingOrDefault(ServletContext servletContext, String registeredKey) {
        Collection<String> mappings = servletContext.getServletRegistration(registeredKey).getMappings();
        if (mappings.isEmpty()) {
            return System.getProperty("LINKERS_DEFAULT_PATH");
        }
        return stripWildcard(mappings.iterator().next());
    }

    private static String stripWildcard(String path) {
        if (!path.endsWith("/*")) {
            return path;
        }
        return path.substring(0, path.length() - 2);
    }
}
