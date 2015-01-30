package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.errors.CompilationError;
import fr.vidal.oss.jax_rs_linker.functions.EntryToStringValueFunction;
import fr.vidal.oss.jax_rs_linker.model.*;
import fr.vidal.oss.jax_rs_linker.predicates.AnnotationMirrorByNamePredicate;
import fr.vidal.oss.jax_rs_linker.predicates.ElementHasAnnotation;
import fr.vidal.oss.jax_rs_linker.predicates.EntryByMethodNamePredicate;
import fr.vidal.oss.jax_rs_linker.predicates.UnparseableValuePredicate;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import static fr.vidal.oss.jax_rs_linker.functions.AnnotationMirrorToMethodValueEntryFunction.TO_METHOD_VALUE_ENTRIES;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ElementParser {

    private final Messager messager;
    private final PathVisitor pathVisitor;
    private final ClassWorkLoad workLoad;
    private final HttpVerbVisitor httpVerbVisitor;

    public ElementParser(Messager messager,
                         Types typeUtils) {

        this.messager = messager;
        this.pathVisitor = new PathVisitor(typeUtils);
        this.workLoad = ClassWorkLoad.init();
        this.httpVerbVisitor = new HttpVerbVisitor();
    }

    public Optional<Mapping> parse(Element element) {
        if (element.getKind() != ElementKind.METHOD) {
            return compilationError(element, CompilationError.NOT_A_METHOD.format(element));
        }

        ExecutableElement method = (ExecutableElement) element;
        Optional<String> maybePath = pathVisitor.visitPath(method);
        if (!maybePath.isPresent()) {
            return compilationError(method, CompilationError.NO_PATH_FOUND.format(qualified(method)));
        }

        Optional<HttpVerb> maybeHttpVerb = httpVerbVisitor.visit(method);
        if (!maybeHttpVerb.isPresent()) {
            return compilationError(method, CompilationError.NO_HTTP_ANNOTATION_FOUND.format(qualified(method)));
        }

        boolean withSelf = ElementHasAnnotation.BY_ANNOTATION(Self.class).apply(method);
        boolean withSubResource = ElementHasAnnotation.BY_ANNOTATION(SubResource.class).apply(method);
        if (!(withSelf ^ withSubResource)) {
            return compilationError(method, CompilationError.ANNOTATION_MISUSE.format(qualified(method)));
        }

        Optional<ApiLink> link = link(method, withSelf);
        if (!link.isPresent()) {
            return compilationError(method, CompilationError.MISSING_ANNOTATIONS.format(qualified(method)));
        }
        Mapping mapping = mapping(method, link.get(), maybeHttpVerb.get(), maybePath.get());

        Optional<CompilationError> maybeError = trackMandatoryParsing(link.get(), mapping);
        if (maybeError.isPresent()) {
            return compilationError(method, CompilationError.TOO_MANY_SELF.format(qualified(method)));
        }

        return Optional.of(mapping);
    }

    private Optional<ApiLink> link(ExecutableElement methodElement, boolean withSelf) {
        if (withSelf) {
            return Optional.of(ApiLink.SELF());
        }
        Optional<String> location = relatedResourceName(methodElement);
        if (!location.isPresent()) {
            return absent();
        }
        ClassName className = ClassName.valueOf(location.get());
        return Optional.of(ApiLink.SUB_RESOURCE(className));
    }

    private Optional<String> relatedResourceName(ExecutableElement methodElement) {
        return FluentIterable.from(methodElement.getAnnotationMirrors())
                .filter(AnnotationMirrorByNamePredicate.byName("SubResource"))
                .transformAndConcat(TO_METHOD_VALUE_ENTRIES)
                .filter(EntryByMethodNamePredicate.byMethodName("value"))
                .transform(EntryToStringValueFunction.TO_STRING_VALUE)
                .firstMatch(not(UnparseableValuePredicate.IS_UNPARSEABLE));
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

    private Optional<CompilationError> trackMandatoryParsing(ApiLink link, Mapping mapping) {
        Optional<ClassName> relatedResource = link.getTarget();
        if (relatedResource.isPresent()) {
            workLoad.addPendingIfNone(relatedResource.get());
            return absent();
        }

        checkArgument(link.getApiLinkType() == ApiLinkType.SELF, "SubResource should define a target");
        ClassName className = mapping.getJavaLocation().getClassName();
        if (workLoad.isCompleted(className)) {
            return Optional.of(CompilationError.TOO_MANY_SELF);
        }
        workLoad.complete(className);
        return absent();
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
