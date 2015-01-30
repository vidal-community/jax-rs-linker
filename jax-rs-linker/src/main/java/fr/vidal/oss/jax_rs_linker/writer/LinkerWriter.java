package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.model.*;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.predicates.MappingByApiLinkTargetPredicate;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static java.lang.String.format;
import static javax.lang.model.element.Modifier.*;

public class LinkerWriter {

    private final Filer filer;

    public LinkerWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(ClassName generatedClass, Collection<Mapping> mappings) throws IOException {
        Optional<Mapping> selfMapping = FluentIterable.from(mappings)
            .firstMatch(new Predicate<Mapping>() {
                @Override
                public boolean apply(@Nullable Mapping input) {
                    return input.getApi().getApiLink().getApiLinkType() == ApiLinkType.SELF;
                }
            });

        ApiPath selfApiPath = selfMapping.get().getApi().getApiPath();

        ClassName typeParameter = templatedPathTypeParameter(selfApiPath, generatedClass.fullyQualifiedName());
        TypeName templatedPathClass =
            ParameterizedTypeName.get(
                com.squareup.javapoet.ClassName.get(TemplatedPath.class),
                com.squareup.javapoet.ClassName.bestGuess(typeParameter.fullyQualifiedName())
            );

        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(generatedClass.className())
            .addModifiers(PUBLIC, FINAL)
            .addAnnotation(AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", LinkerAnnotationProcessor.class.getName())
                .build())
            .addField(FieldSpec.builder(String.class, "contextPath", PRIVATE, FINAL).build())
            .addMethod(constructorBuilder()
                .addModifiers(PUBLIC)
                .addCode("this($S);\n", "")
                .build())
            .addMethod(constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(
                    ParameterSpec.builder(String.class, "contextPath", FINAL).build())
                .addStatement("this.$L = $L", "contextPath", "contextPath")
                .build())
            .addMethod(
                linkerMethod(
                    "self",
                    selfApiPath,
                    templatedPathClass
                )
            );


        for (Mapping mapping : linked(mappings)) {
            Api api = mapping.getApi();
            typeBuilder.addMethod(
                linkerMethod(
                    format("related%s", api.getApiLink().getTarget().get().className()),
                    api.getApiPath(),
                    templatedPathClass
                )
            );
        }
        typeBuilder = typeBuilder.addMethod(pathParameterMethod());
        JavaFile.builder(generatedClass.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);

    }

    private MethodSpec linkerMethod(String methodName, ApiPath apiPath, TypeName returnType) {
        return MethodSpec.methodBuilder(methodName)
            .addModifiers(PUBLIC, FINAL)
            .returns(returnType)
            .addStatement(
                "return new $T(contextPath + $S, $T.<$T>asList($L))",
                returnType,
                apiPath.getPath(),
                Arrays.class,
                PathParameter.class,
                parametersAsList(apiPath.getPathParameters())
            )
            .build();
    }

    private ClassName templatedPathTypeParameter(ApiPath apiPath, String generatedClass) {
        if (apiPath.getPathParameters().isEmpty()) {
            return ClassName.valueOf(NoPathParameters.class.getName());
        }
        return ClassName.valueOf(generatedClass.replace("Linker", "PathParameters"));
    }

    private Iterable<Mapping> linked(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
            .filter(MappingByApiLinkTargetPredicate.BY_API_LINK_TARGET_PRESENCE)
            .toList();
    }

    private String parametersAsList(Collection<PathParameter> pathParameters) {
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


}
