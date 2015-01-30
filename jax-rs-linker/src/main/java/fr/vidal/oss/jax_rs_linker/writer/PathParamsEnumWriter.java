package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
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

        typeBuilder.addField(String.class, "placeholder", PRIVATE, FINAL)
            .addMethod(MethodSpec.constructorBuilder()
                .addParameter(String.class, "placeholder")
                .addCode("this.$L = $L;\n", "placeholder", "placeholder")
                .build())
            .addMethod(MethodSpec.methodBuilder("placeholder")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC, FINAL)
                .returns(String.class)
                .addCode("return this.$L;\n", "placeholder")
                .build());


        JavaFile.builder(generatedClass.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);

    }

    private void writeEnumeration(Collection<Mapping> mappings, TypeSpec.Builder typeBuilder) throws IOException {
        for (PathParameter parameter : enumConstants(mappings)) {
            typeBuilder.addEnumConstant(
                EnumConstants.constantName(parameter.getName()),
                TypeSpec.anonymousClassBuilder("$S", parameter.getName()).build()
            );
        }
    }

    private Collection<PathParameter> enumConstants(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
                .transformAndConcat(MappingToPathParameters.TO_PATH_PARAMETERS)
                .toSortedSet(new Comparator<PathParameter>() {
                    @Override
                    public int compare(PathParameter firstParam, PathParameter secondParam) {
                        return firstParam.getName().compareTo(secondParam.getName());
                    }
                });
    }

}
