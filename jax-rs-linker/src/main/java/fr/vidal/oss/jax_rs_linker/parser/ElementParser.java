package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.errors.CompilationError;
import fr.vidal.oss.jax_rs_linker.model.Api;
import fr.vidal.oss.jax_rs_linker.model.ApiLink;
import fr.vidal.oss.jax_rs_linker.model.ApiLinkType;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ApiQuery;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.HttpVerb;
import fr.vidal.oss.jax_rs_linker.model.JavaLocation;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.model.SubResourceTarget;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import static fr.vidal.oss.jax_rs_linker.functions.AnnotationMirrorToMethodValueEntryFunction.TO_METHOD_VALUE_ENTRIES;
import static fr.vidal.oss.jax_rs_linker.functions.ElementToPathParameter.ELEMENT_INTO_PATH_PARAMETER;
import static fr.vidal.oss.jax_rs_linker.functions.ElementToQueryParameter.ELEMENT_INTO_QUERY_PARAMETER;
import static fr.vidal.oss.jax_rs_linker.functions.EntriesToStringValueFunction.TO_STRING_VALUE;
import static fr.vidal.oss.jax_rs_linker.functions.SetterToPathParameter.SETTER_TO_PATH_PARAMETER;
import static fr.vidal.oss.jax_rs_linker.functions.SetterToQueryParameter.SETTER_TO_QUERY_PARAMETER;
import static fr.vidal.oss.jax_rs_linker.predicates.AnnotationMirrorByNamePredicate.byName;
import static fr.vidal.oss.jax_rs_linker.predicates.ElementHasAnnotation.BY_ANNOTATION;
import static fr.vidal.oss.jax_rs_linker.predicates.UnparseableValuePredicate.IS_UNPARSEABLE;
import static javax.tools.Diagnostic.Kind.ERROR;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class ElementParser {

    private final Messager messager;
    private final PathVisitor pathVisitor;
    private final ClassWorkLoad workLoad;
    private final HttpVerbVisitor httpVerbVisitor;

    private final ParameterVisitor<QueryParameter> queryParameterVisitor;
    private final ParameterVisitor<PathParameter> pathParameterVisitor;

    public ElementParser(Messager messager,
                         Types typeUtils) {

        this.messager = messager;
        this.pathVisitor = new PathVisitor(typeUtils);
        this.workLoad = ClassWorkLoad.init();
        this.httpVerbVisitor = new HttpVerbVisitor();
        this.queryParameterVisitor = new ParameterVisitor<>(
            typeUtils,
            new AnnotatedElementMapping<>(
                QueryParam.class,
                ELEMENT_INTO_QUERY_PARAMETER,
                SETTER_TO_QUERY_PARAMETER
            )
        );
        this.pathParameterVisitor = new ParameterVisitor<>(
            typeUtils,
            new AnnotatedElementMapping<>(
                PathParam.class,
                ELEMENT_INTO_PATH_PARAMETER,
                SETTER_TO_PATH_PARAMETER
            )
        );
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

        boolean withSelf = BY_ANNOTATION(Self.class).apply(method);
        boolean withSubResource = BY_ANNOTATION(SubResource.class).apply(method);
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
        Optional<SubResourceTarget> location = relatedResourceName(methodElement);
        if (!location.isPresent()) {
            return absent();
        }
        return Optional.of(ApiLink.SUB_RESOURCE(location.get()));
    }

    private Optional<SubResourceTarget> relatedResourceName(ExecutableElement methodElement) {
        return FluentIterable.from(methodElement.getAnnotationMirrors())
            .filter(byName("SubResource"))
            .transform(TO_METHOD_VALUE_ENTRIES)
            .transform(TO_STRING_VALUE)
            .firstMatch(not(IS_UNPARSEABLE));
    }

    private Mapping mapping(ExecutableElement methodElement, ApiLink link, HttpVerb httpVerb, String path) {
        return new Mapping(
            javaLocation(methodElement),
            api(
                link,
                httpVerb,
                apiPath(methodElement, path),
                apiQuery(methodElement)
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

    private ApiPath apiPath(ExecutableElement methodElement, String path) {
        return new ApiPath(path, pathParameterVisitor.visit(methodElement));
    }

    private ApiQuery apiQuery(ExecutableElement methodElement) {
        return new ApiQuery(queryParameterVisitor.visit(methodElement));
    }

    private Api api(ApiLink link, HttpVerb httpVerb, ApiPath apiPath, ApiQuery apiQuery) {
        return new Api(httpVerb, link, apiPath, apiQuery);
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
