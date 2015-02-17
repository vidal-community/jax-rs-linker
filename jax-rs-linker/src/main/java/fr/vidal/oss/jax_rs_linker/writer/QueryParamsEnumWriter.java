package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import fr.vidal.oss.jax_rs_linker.functions.MappingToQueryParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.Collection;

import static fr.vidal.oss.jax_rs_linker.functions.QueryParameterToString.TO_STRING;
import static javax.lang.model.element.Modifier.*;

public class QueryParamsEnumWriter {

    private final Filer filer;

    public QueryParamsEnumWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(ClassName generatedClass, Collection<Mapping> mappings) throws IOException {
        TypeSpec.Builder typeBuilder = TypeSpec.enumBuilder(generatedClass.className())
            .addModifiers(PUBLIC)
            .addSuperinterface(QueryParameters.class)
            .addAnnotation(AnnotationSpec.builder(Generated.class)
                    .addMember("value", "$S", LinkerAnnotationProcessor.class.getName())
                    .build()
            );

        writeEnumeration(mappings, typeBuilder);

        typeBuilder.addField(String.class, "value", PRIVATE, FINAL);
        typeBuilder.addMethod(MethodSpec.constructorBuilder()
            .addParameter(String.class, "value")
            .addCode("this.$L = $L;\n", "value", "value")
            .build());
        typeBuilder.addMethod(MethodSpec.methodBuilder("value")
            .addModifiers(PUBLIC)
            .returns(String.class)
            .addCode("return this.$L;\n", "value")
            .build());


        JavaFile.builder(generatedClass.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);
    }

    private void writeEnumeration(Collection<Mapping> mappings, TypeSpec.Builder typeBuilder) throws IOException {
        Collection<String> apiQueryEnums = getApiQuerysEnums(mappings);
        for (String parameterName : apiQueryEnums) {
            typeBuilder.addEnumConstant(
                EnumConstants.constantName(parameterName),
                TypeSpec.anonymousClassBuilder("$S", parameterName)
                    .build()

            );
        }
    }

    private Collection<String> getApiQuerysEnums(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
            .transformAndConcat(MappingToQueryParameters.TO_QUERY_PARAMETERS)
            .transform(TO_STRING)
            .toSortedSet(Ordering.natural());
    }
}
