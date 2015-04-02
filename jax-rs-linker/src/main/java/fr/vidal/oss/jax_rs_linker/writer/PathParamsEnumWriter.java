package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Pattern;

import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static javax.lang.model.element.Modifier.*;

public class PathParamsEnumWriter {

    private final Filer filer;

    public PathParamsEnumWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(ClassName generatedClass, Collection<Mapping> mappings) throws IOException {

        TypeSpec.Builder typeBuilder = TypeSpec.enumBuilder(generatedClass.className())
            .addModifiers(PUBLIC)
            .addSuperinterface(PathParameters.class)
            .addAnnotation(AnnotationSpec.builder(Generated.class)
                    .addMember("value", "$S", LinkerAnnotationProcessor.class.getName())
                    .build()
            );

        writeEnumeration(mappings, typeBuilder);

        TypeName pattern =
                ParameterizedTypeName.get(
                        com.squareup.javapoet.ClassName.get(Optional.class),
                        com.squareup.javapoet.ClassName.get(Pattern.class)
                );

        typeBuilder.addField(String.class, "placeholder", PRIVATE, FINAL)
                .addField(pattern, "regex", PRIVATE, FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "placeholder")
                        .addParameter(pattern, "regex")
                        .addCode("this.$L = $L;\n", "placeholder", "placeholder")
                        .addCode("this.$L = $L;\n", "regex", "regex")
                        .build())
                .addMethod(MethodSpec.methodBuilder("placeholder")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC, FINAL)
                        .returns(String.class)
                        .addCode("return this.$L;\n", "placeholder")
                        .build())
                .addMethod(MethodSpec.methodBuilder("regex")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC, FINAL)
                        .returns(pattern)
                        .addCode("return this.$L;\n", "regex")
                        .build());
        ;


        JavaFile.builder(generatedClass.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);

    }

    private void writeEnumeration(Collection<Mapping> mappings, TypeSpec.Builder typeBuilder) throws IOException {
        for (PathParameter parameter : enumConstants(mappings)) {
            Optional<Pattern> regex = parameter.getRegex();
            String name = parameter.getName();
            if (regex.isPresent()) {
                typeBuilder.addEnumConstant(
                        EnumConstants.constantName(name),
                        TypeSpec.anonymousClassBuilder("$S, Optional.<Pattern>of(Pattern.compile($S))", name, regex.get())
                                .build()
                );
            } else {
                typeBuilder.addEnumConstant(
                        EnumConstants.constantName(name),
                        TypeSpec.anonymousClassBuilder("$S, Optional.<Pattern>absent()", name)
                                .build()
                );
            }
        }
    }

    private Collection<PathParameter> enumConstants(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
                .transformAndConcat(TO_PATH_PARAMETERS)
                .toSortedSet(new Comparator<PathParameter>() {
                    @Override
                    public int compare(PathParameter firstParam, PathParameter secondParam) {
                        return firstParam.getName().compareTo(secondParam.getName());
                    }
                });
    }

}
