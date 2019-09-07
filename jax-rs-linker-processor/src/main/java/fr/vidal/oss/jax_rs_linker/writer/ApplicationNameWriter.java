package fr.vidal.oss.jax_rs_linker.writer;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.*;

public class ApplicationNameWriter {

    private final Filer filer;

    public ApplicationNameWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(String name, TypeElement originatingElement) throws IOException {

        TypeSpec type = classBuilder("ApplicationName")
            .addOriginatingElement(originatingElement)
            .addModifiers(PUBLIC, FINAL)
            .addMethod(methodBuilder("get")
                .returns(String.class)
                .addModifiers(PUBLIC, STATIC)
                .addStatement("return $S", name)
                .build())
            .build();

        JavaFile.builder("fr.vidal.oss.jax_rs_linker", type)
            .indent("\t")
            .build()
            .writeTo(filer);
    }
}
