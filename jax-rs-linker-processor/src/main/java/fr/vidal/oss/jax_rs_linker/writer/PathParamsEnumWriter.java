package fr.vidal.oss.jax_rs_linker.writer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static fr.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static java.util.stream.Collectors.toCollection;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

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

        TypeName pattern = com.squareup.javapoet.ClassName.get(Pattern.class);

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


        JavaFile.builder(generatedClass.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);

    }

    private void writeEnumeration(Collection<Mapping> mappings, TypeSpec.Builder typeBuilder) {
        for (PathParameter parameter : enumConstants(mappings)) {
            Optional<Pattern> pattern = parameter.getRegex();
            String name = parameter.getName();
            typeBuilder.addEnumConstant(
                EnumConstants.constantName(name),
                enumConstructorExpression(pattern, name)
            );
        }
    }

    private TypeSpec enumConstructorExpression(Optional<Pattern> pattern, String name) {
        return pattern
            .map(value -> TypeSpec.anonymousClassBuilder("$S, Pattern.compile($S)", name, value.pattern()).build())
            .orElseGet(() -> TypeSpec.anonymousClassBuilder("$S, null", name).build());
    }

    private Collection<PathParameter> enumConstants(Collection<Mapping> mappings) {
        return mappings.stream()
            .flatMap(TO_PATH_PARAMETERS)
            .collect(toCollection(() -> new TreeSet<>(Comparator.comparing(PathParameter::getName))));
    }

}
