package fr.vidal.oss.jax_rs_linker.servlet;

import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.List;

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

        assertThat(ContextPaths.contextPath(servletContext, "w00t")).isEqualTo("/prefix/what");
    }

    @Test
    public void strips_wildcard() {
        ServletContext servletContext = servletContext(
                servletRegistration(asList("what/*")),
                "w00t",
                "/prefix/"
        );

        assertThat(ContextPaths.contextPath(servletContext, "w00t")).isEqualTo("/prefix/what");
    }

    @Test
    public void keeps_first_mapping_only() {
        ServletContext servletContext = servletContext(
                servletRegistration(asList("what/*", "the", "hell")),
                "w00t",
                "/prefix/"
        );

        assertThat(ContextPaths.contextPath(servletContext, "w00t")).isEqualTo("/prefix/what");
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
