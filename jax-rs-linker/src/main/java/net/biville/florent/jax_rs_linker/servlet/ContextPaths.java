package net.biville.florent.jax_rs_linker.servlet;

import javax.servlet.ServletContext;

public class ContextPaths {

    public static String contextPath(ServletContext servletContext, String registeredKey) {
        String mappedPath = stripWildcard(servletContext.getServletRegistration(registeredKey).getMappings().iterator().next());
        return servletContext.getContextPath() + mappedPath;
    }

    private static String stripWildcard(String path) {
        if (!path.endsWith("/*")) {
            return path;
        }
        return path.substring(0, path.length() - 2);
    }
}
