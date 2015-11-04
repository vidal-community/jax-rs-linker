package fr.vidal.oss.jax_rs_linker.writer;

import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.api.NoQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.Api;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ApiQuery;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.squareup.javapoet.ClassName.bestGuess;
import static fr.vidal.oss.jax_rs_linker.predicates.HasSelfMapping.HAS_SELF;
import static fr.vidal.oss.jax_rs_linker.predicates.MappingByApiLinkTargetPredicate.BY_API_LINK_TARGET_PRESENCE;
import static java.lang.String.format;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import fr.vidal.oss.jax_rs_linker.predicates.HasSelfMapping;

public class LinkerWriter {

    private final Filer filer;

    public LinkerWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(ClassName generatedClass, Collection<Mapping> mappings, ClassName contextPathHolder) throws IOException {
        Api selfApi = FluentIterable.from(mappings).firstMatch(HAS_SELF).get().getApi();

        String lowerCamelClassName = UPPER_CAMEL.to(LOWER_CAMEL, generatedClass.className());
        TypeSpec.Builder typeBuilder = TypeSpec.enumBuilder(generatedClass.className())
            .addModifiers(PUBLIC)
            .addAnnotation(AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", LinkerAnnotationProcessor.class.getName())
                .build())
            .addEnumConstant("INSTANCE")
            .addField(FieldSpec
                .builder(String.class, "contextPath", PRIVATE, FINAL)
                .initializer("$T.getContextPath()", toClassName(contextPathHolder))
                .build())
            .addMethod(MethodSpec.methodBuilder(lowerCamelClassName)
                .addModifiers(PUBLIC, STATIC)
                .returns(toClassName(generatedClass))
                .addStatement("return INSTANCE")
                .build())
            .addMethod(
                linkerMethod(
                    "self",
                    selfApi.getApiPath(),
                    templatedUrlType(generatedClass, selfApi),
                    selfApi.getApiQuery()
                )
            );


        for (Mapping mapping : linked(mappings)) {
            Api apiMapping = mapping.getApi();
            typeBuilder.addMethod(
                linkerMethod(
                    format("related%s", apiMapping.getApiLink().getQualifiedTarget().get()),
                    apiMapping.getApiPath(),
                    templatedUrlType(generatedClass, apiMapping),
                    apiMapping.getApiQuery()
                )
            );
        }
        typeBuilder = typeBuilder.addMethod(pathParameterMethod());
        typeBuilder = typeBuilder.addMethod(queryParameterMethod());
        JavaFile.builder(generatedClass.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);

    }

    private TypeName templatedUrlType(ClassName generatedClass, Api api) {
        ClassName pathTypeParameter = templatedPathTypeParameter(api.getApiPath(), generatedClass.fullyQualifiedName());
        ClassName queryTypeParameter = templatedQueryTypeParameter(api.getApiQuery(), generatedClass.fullyQualifiedName());
        return ParameterizedTypeName.get(
            com.squareup.javapoet.ClassName.get(TemplatedUrl.class),
            bestGuess(pathTypeParameter.fullyQualifiedName()),
            bestGuess(queryTypeParameter.fullyQualifiedName())
        );
    }

    private com.squareup.javapoet.ClassName toClassName(ClassName generatedClass) {
        return com.squareup.javapoet.ClassName.get(generatedClass.packageName(), generatedClass.className());
    }

    private MethodSpec linkerMethod(String methodName, ApiPath apiPath, TypeName returnType, ApiQuery apiQuery) {
        return MethodSpec.methodBuilder(methodName)
            .addModifiers(PUBLIC, FINAL)
            .returns(returnType)
            .addStatement(
                "return new $T(contextPath + $S, $T.<$T>asList($L), $T.<$T>asList($L))",
                returnType,
                apiPath.getPath(),
                Arrays.class,
                PathParameter.class,
                pathParametersAsList(apiPath.getPathParameters()),
                Arrays.class,
                QueryParameter.class,
                queryParametersAsList(apiQuery.getQueryParameters())
            )
            .build();
    }

    private ClassName templatedPathTypeParameter(ApiPath apiPath, String generatedClass) {
        if (apiPath.getPathParameters().isEmpty()) {
            return ClassName.valueOf(NoPathParameters.class.getName());
        }
        return ClassName.valueOf(generatedClass.replace("Linker", "PathParameters"));
    }

    private ClassName templatedQueryTypeParameter(ApiQuery apiQuery, String generatedClass) {
        if (apiQuery.getQueryParameters().isEmpty()) {
            return ClassName.valueOf(NoQueryParameters.class.getName());
        }
        return ClassName.valueOf(generatedClass.replace("Linker", "QueryParameters"));
    }

    private Iterable<Mapping> linked(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
            .filter(BY_API_LINK_TARGET_PRESENCE)
            .toList();
    }

    private String pathParametersAsList(Collection<PathParameter> pathParameters) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<PathParameter> iterator = pathParameters.iterator(); iterator.hasNext(); ) {
            PathParameter parameter = iterator.next();
            String separator = iterator.hasNext() ? "," : "";
            builder.append(String.format(
                "pathParameter(\"%s\", \"%s\")%s",
                parameter.getType(),
                parameter.getName(),
                separator
            ));
        }

        return builder.toString();
    }


    private String queryParametersAsList(Collection<QueryParameter> queryParameters) {
        StringBuilder builder = new StringBuilder();
        Iterator<QueryParameter> iterator = queryParameters.iterator();
        while (iterator.hasNext()) {
            QueryParameter parameter = iterator.next();
            String separator = iterator.hasNext() ? "," : "";
            builder.append(String.format(
                "queryParameter(\"%s\")%s",
                parameter.getName(),
                separator
            ));
        }

        return builder.toString();
    }

    private MethodSpec pathParameterMethod() {
        return MethodSpec.methodBuilder("pathParameter")
            .returns(PathParameter.class)
            .addModifiers(PRIVATE, STATIC)
            .addParameter(
                ParameterSpec.builder(String.class, "type", FINAL).build())
            .addParameter(
                ParameterSpec.builder(String.class, "name", FINAL).build())
            .addStatement(
                "return new $T($T.valueOf($L), $L)",
                PathParameter.class,
                ClassName.class,
                "type",
                "name"
            )
            .build();
    }

    private MethodSpec queryParameterMethod() {
        return MethodSpec.methodBuilder("queryParameter")
            .returns(QueryParameter.class)
            .addModifiers(PRIVATE, STATIC)
            .addParameter(
                ParameterSpec.builder(String.class, "name", FINAL).build())
            .addStatement(
                "return new $T($L)",
                QueryParameter.class,
                "name"
            )
            .build();
    }

}
