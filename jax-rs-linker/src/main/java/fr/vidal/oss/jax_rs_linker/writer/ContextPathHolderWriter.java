package fr.vidal.oss.jax_rs_linker.writer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.servlet.ContextPaths;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

import static com.squareup.javapoet.ClassName.get;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class ContextPathHolderWriter {

    private final Filer filer;

    public ContextPathHolderWriter(Filer filer) {
        this.filer = filer;
    }

    public void write(ClassName linkers) throws IOException {
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
                .initializer("$T.get()", get("fr.vidal.oss.jax_rs_linker", "ApplicationName"))
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
                .build())
            .addMethod(MethodSpec.methodBuilder("getContextPath")
                .addModifiers(PUBLIC, STATIC)
                .returns(String.class)
                .addCode(
                    "return contextPath;\n")
                .build());


        JavaFile.builder(linkers.packageName(), typeBuilder.build())
            .indent("\t")
            .build()
            .writeTo(filer);
    }

}
