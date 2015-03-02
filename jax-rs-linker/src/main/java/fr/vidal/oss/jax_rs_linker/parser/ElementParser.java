package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;
import fr.vidal.oss.jax_rs_linker.errors.CompilationError;
import fr.vidal.oss.jax_rs_linker.model.*;
import fr.vidal.oss.jax_rs_linker.predicates.ElementHasAnnotation;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.ws.rs.BeanParam;
import javax.ws.rs.QueryParam;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Lists.newArrayList;
import static fr.vidal.oss.jax_rs_linker.functions.AnnotationMirrorToMethodValueEntryFunction.TO_METHOD_VALUE_ENTRIES;
import static fr.vidal.oss.jax_rs_linker.functions.EntriesToStringValueFunction.TO_STRING_VALUE;
import static fr.vidal.oss.jax_rs_linker.functions.VariableElementToQueryParameter.INTO_QUERY_PARAMETER;
import static fr.vidal.oss.jax_rs_linker.predicates.AnnotationMirrorByNamePredicate.byName;
import static fr.vidal.oss.jax_rs_linker.predicates.UnparseableValuePredicate.IS_UNPARSEABLE;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ElementParser {

    private final Messager messager;
    private final PathVisitor pathVisitor;
    private final ClassWorkLoad workLoad;
    private final HttpVerbVisitor httpVerbVisitor;
    private final Types typeUtils;

    public ElementParser(Messager messager,
                         Types typeUtils) {

        this.messager = messager;
        this.typeUtils = typeUtils;
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
                apiQuery(methodElement.getParameters())
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

    private Api api(ApiLink link, HttpVerb httpVerb, ApiPath apiPath, ApiQuery apiQuery) {
        return new Api(
            httpVerb,
            link,
            apiPath,
            apiQuery);
    }

    private ApiPath apiPath(ExecutableElement methodElement, String path) {
        return new ApiPath(
            path,
            pathVisitor.visitPathParameters(methodElement)
        );
    }

    private ApiQuery apiQuery(Collection<? extends VariableElement> parameters) {
        Collection<QueryParameter> queryParameters = newArrayList();
        for (VariableElement variableElement : parameters) {
            final QueryParam annotation = variableElement.getAnnotation(QueryParam.class);
            if (annotation != null) {
                queryParameters.add(INTO_QUERY_PARAMETER.apply(variableElement));
            }
            BeanParam beanParam = variableElement.getAnnotation(BeanParam.class);
            if (beanParam != null) {
                Element beanParamTargetClass = typeUtils.asElement(variableElement.asType());
                List<ExecutableElement> ctors = ElementFilter.constructorsIn(beanParamTargetClass.getEnclosedElements());
                for (ExecutableElement ctor : ctors) {
                    for (VariableElement ctorParameter : ctor.getParameters()) {
                        queryParameters.add(INTO_QUERY_PARAMETER.apply(ctorParameter));
                    }
                }
            }
        }
        return new ApiQuery(queryParameters);
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
