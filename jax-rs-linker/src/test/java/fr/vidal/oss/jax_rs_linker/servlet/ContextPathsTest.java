package fr.vidal.oss.jax_rs_linker.servlet;

import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Collections;
import java.util.List;

import static fr.vidal.oss.jax_rs_linker.servlet.ContextPaths.contextPath;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContextPathsTest {

    @Test
    public void computes_mapped_context_path() {
        ServletContext servletContext = servletContext(
                servletRegistration(asList("what")),
                "w00t",
                "/prefix/"
        );

        assertThat(contextPath(servletContext, "w00t")).isEqualTo("/prefix/what");
    }

    @Test
    public void strips_wildcard() {
        ServletContext servletContext = servletContext(
                servletRegistration(asList("what/*")),
                "w00t",
                "/prefix/"
        );

        assertThat(contextPath(servletContext, "w00t")).isEqualTo("/prefix/what");
    }

    @Test
    public void keeps_first_mapping_only() {
        ServletContext servletContext = servletContext(
                servletRegistration(asList("what/*", "the", "hell")),
                "w00t",
                "/prefix/"
        );

        assertThat(contextPath(servletContext, "w00t")).isEqualTo("/prefix/what");
    }

    @Test
    public void defaults_to_environment_path_when_no_matching_registration() {
        System.setProperty("LINKERS_DEFAULT_PATH", "kikoo");
        ServletContext servletContext = servletContext(
                servletRegistration(Collections.<String>emptyList()),
                "w00t",
                "/prefix/"
        );

        assertThat(contextPath(servletContext, "w00t")).isEqualTo("/prefix/kikoo");
    }

    private ServletContext servletContext(ServletRegistration servletRegistration, String applicationName, String contextPath) {
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getServletRegistration(applicationName)).thenReturn(servletRegistration);
        when(servletContext.getContextPath()).thenReturn(contextPath);
        return servletContext;
    }

    private ServletRegistration servletRegistration(List<String> mappings) {
        ServletRegistration servletRegistration = mock(ServletRegistration.class);
        when(servletRegistration.getMappings()).thenReturn(mappings);
        return servletRegistration;
    }
}
