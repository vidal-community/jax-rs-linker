package net.biville.florent.jax_rs_linker.parser;

import com.google.common.base.Optional;
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;
import net.biville.florent.jax_rs_linker.model.*;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static com.google.common.base.Optional.absent;
import static javax.tools.Diagnostic.Kind.ERROR;
import static net.biville.florent.jax_rs_linker.predicates.ElementHasAnnotation.BY_ANNOTATION;
import static net.biville.florent.jax_rs_linker.errors.Messages.*;

public class ElementParser {

    private final Messager messager;
    private final PathVisitor pathVisitor;
    private final HttpVerbVisitor httpVerbVisitor;

    public ElementParser(Messager messager,
                         Types typeUtils) {

        this.messager = messager;
        this.pathVisitor = new PathVisitor(typeUtils);
        this.httpVerbVisitor = new HttpVerbVisitor();
    }

    public Optional<Mapping> parse(Element element) {
        if (element.getKind() != ElementKind.METHOD) {
            return compilationError(element, NOT_A_METHOD.format(element));
        }

        ExecutableElement method = (ExecutableElement) element;
        Optional<String> maybePath = pathVisitor.visitPath(method);
        if (!maybePath.isPresent()) {
            return compilationError(method, NO_PATH_FOUND.format(qualified(method)));
        }

        Optional<HttpVerb> maybeHttpVerb = httpVerbVisitor.visit(method);
        if (!maybeHttpVerb.isPresent()) {
            return compilationError(method, NO_HTTP_ANNOTATION_FOUND.format(qualified(method)));
        }

        boolean withSelf = BY_ANNOTATION(Self.class).apply(method);
        boolean withSubResource = BY_ANNOTATION(SubResource.class).apply(method);
        if (!(withSelf ^ withSubResource)) {
            return compilationError(method, ANNOTATION_MISUSE.format(qualified(method)));
        }

        return Optional.of(
            mapping(method, link(method, withSelf), maybeHttpVerb.get(), maybePath.get())
        );
    }

    private ApiLink link(ExecutableElement methodElement, boolean withSelf) {
        if (withSelf) {
            return ApiLink.SELF();
        }
        String location = methodElement.getAnnotation(SubResource.class).value();
        return ApiLink.SUB_RESOURCE(ClassName.valueOf(location));
    }

    private Mapping mapping(ExecutableElement methodElement, ApiLink link, HttpVerb httpVerb, String path) {
        return new Mapping(
            javaLocation(methodElement),
            api(
                link,
                httpVerb,
                apiPath(methodElement, path)
            )
        );
    }

    private JavaLocation javaLocation(ExecutableElement methodElement) {
        return new JavaLocation(
            className(methodElement),
            methodElement.getSimpleName().toString()
        );
    }

    private Api api(ApiLink link, HttpVerb httpVerb, ApiPath apiPath) {
        return new Api(
            httpVerb,
            link,
            apiPath
        );
    }

    private ApiPath apiPath(ExecutableElement methodElement, String path) {
        return new ApiPath(
            path,
            pathVisitor.visitParameters(methodElement)
        );
    }

    private ClassName className(ExecutableElement element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        return ClassName.valueOf(classElement.getQualifiedName().toString());
    }

    private Optional<Mapping> compilationError(Element element, String errorMsg) {
        messager.printMessage(ERROR, errorMsg, element);
        return absent();
    }

    private String qualified(Element element) {
        TypeElement enclosingClass = (TypeElement) element.getEnclosingElement();
        
        return String.format(
            "%s#%s", 
            enclosingClass.getQualifiedName(),
            element.getSimpleName()
        );
    }

}
