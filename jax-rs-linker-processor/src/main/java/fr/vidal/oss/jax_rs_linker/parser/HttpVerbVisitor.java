package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.collect.ImmutableMap;
import fr.vidal.oss.jax_rs_linker.model.HttpVerb;
import fr.vidal.oss.jax_rs_linker.predicates.AnnotationMatchesElement;

import javax.lang.model.element.ExecutableElement;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

class HttpVerbVisitor {

    private static final Map<Class<? extends Annotation>, HttpVerb> JAX_RS_HTTP_VERBS =
        ImmutableMap.<Class<? extends Annotation>, HttpVerb>builder()
            .put(OPTIONS.class, HttpVerb.OPTIONS)
            .put(HEAD.class, HttpVerb.HEAD)
            .put(GET.class, HttpVerb.GET)
            .put(POST.class, HttpVerb.POST)
            .put(PUT.class, HttpVerb.PUT)
            .put(DELETE.class, HttpVerb.DELETE)
            .build();

    public Optional<HttpVerb> visit(final ExecutableElement methodElement) {
        return matchingAnnotation(methodElement).map(JAX_RS_HTTP_VERBS::get);
    }

    private static Optional<Class<? extends Annotation>> matchingAnnotation(ExecutableElement methodElement) {
        return JAX_RS_HTTP_VERBS.keySet().stream()
            .filter(AnnotationMatchesElement.BY_ELEMENT(methodElement))
            .findFirst();
    }
}
