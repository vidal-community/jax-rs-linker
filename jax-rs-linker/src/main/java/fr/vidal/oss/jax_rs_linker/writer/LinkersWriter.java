package fr.vidal.oss.jax_rs_linker.writer;

import com.squareup.javapoet.*;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.servlet.ContextPaths;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.beans.Introspector;
import java.io.IOException;
import java.util.Set;

import static javax.lang.model.element.Modifier.*;

public class LinkersWriter {

    private final Filer filer;

    public LinkersWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(ClassName linkers, Set<ClassName> classes, String applicationName) throws IOException {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(linkers.className())
            .addModifiers(PUBLIC, FINAL)
            .addSuperinterface(ServletContextListener.class)
            .addAnnotation(AnnotationSpec.builder(WebListener.class).build())
            .addAnnotation(AnnotationSpec.builder(Generated.class)
                    .addMember("value", "$S", LinkerAnnotationProcessor.class.getName())
                    .build()
            )
            .addField(FieldSpec.builder(String.class, "contextPath", PRIVATE, STATIC)
                .initializer("\"\"")
                .build())
            .addField(FieldSpec.builder(String.class, "applicationName", PRIVATE, STATIC)
                .initializer("$S", applicationName)
                .build())
            .addMethod(MethodSpec.methodBuilder("contextInitialized")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addParameter(ServletContextEvent.class, "sce")
                .addCode(
                    "$L = $T.contextPath($L.getServletContext(), $L);\n",
                    "contextPath",
                    ContextPaths.class,
                    "sce",
                    "applicationName")
                .build())
            .addMethod(MethodSpec.methodBuilder("contextDestroyed")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addParameter(ServletContextEvent.class, "sce")
                .build());


        for (ClassName linker : classes) {
            String linkerName = linkerName(linker);
            com.squareup.javapoet.ClassName linkerClassName = com.squareup.javapoet.ClassName.bestGuess(linkerName);
            typeBuilder.addMethod(
                MethodSpec.methodBuilder(Introspector.decapitalize(linkerClassName.simpleName()))
                    .returns(linkerClassName)
                    .addModifiers(PUBLIC, STATIC)
                    .addCode("return new $T($L);\n", linkerClassName, "contextPath")
                    .build()
            );
        }

        JavaFile.builder(linkers.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);
    }

    private static String linkerName(ClassName linker) {
        return linker.fullyQualifiedName() + LinkerAnnotationProcessor.GENERATED_CLASSNAME_SUFFIX;
    }
}
