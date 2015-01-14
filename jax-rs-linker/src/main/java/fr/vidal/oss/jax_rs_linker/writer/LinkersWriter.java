package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Throwables;
import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.functions.ClassNameToLinkerName;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.servlet.ContextPaths;

import javax.annotation.Generated;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.beans.Introspector;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.immutableEnumSet;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class LinkersWriter implements AutoCloseable {

    private final JavaWriter javaWriter;

    public LinkersWriter(JavaWriter javaWriter) {
        this.javaWriter = javaWriter;
    }

    @Override
    public void close(){
        try {
            javaWriter.close();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    public void write(ClassName linkers, Set<ClassName> classes, String applicationName) throws IOException {
        javaWriter.setIndent("\t");
        JavaWriter writer = javaWriter
                .emitPackage(linkers.packageName())
                .emitImports(Generated.class, ServletContextEvent.class, ServletContextListener.class, WebListener.class, ContextPaths.class)
                .emitImports(transform(classes, ClassNameToLinkerName.TO_LINKER_NAME))
                .emitEmptyLine()
                .emitAnnotation(WebListener.class)
                .emitAnnotation(Generated.class, LinkerAnnotationProcessor.processorQualifiedName())
                .beginType(linkers.className(), "class", EnumSet.of(PUBLIC), null, "ServletContextListener")
                .emitEmptyLine()
                .emitField("String", "contextPath", immutableEnumSet(PRIVATE, STATIC), "\"\"")
                .emitEmptyLine()
                .emitField("String", "applicationName", immutableEnumSet(PRIVATE, STATIC), StringLiteral.forValue(applicationName).literal())
                .emitEmptyLine()
                .emitAnnotation(Override.class)
                .beginMethod("void", "contextInitialized", immutableEnumSet(PUBLIC), "ServletContextEvent", "sce")
                .emitStatement("contextPath = ContextPaths.contextPath(sce.getServletContext(), applicationName)")
                .endMethod()
                .emitEmptyLine()
                .emitAnnotation(Override.class)
                .beginMethod("void", "contextDestroyed", immutableEnumSet(PUBLIC), "ServletContextEvent", "sce")
                .endMethod();

        for (ClassName linker : classes) {
            String linkerName = linkerName(linker);
            writer.emitEmptyLine()
                    .beginMethod(linkerName, Introspector.decapitalize(linkerName), immutableEnumSet(PUBLIC, STATIC))
                    .emitStatement(String.format("return new %s(contextPath)", linkerName))
                    .endMethod();
        }

        writer.endType();
    }

    private static String linkerName(ClassName linker) {
        return linker.className() + LinkerAnnotationProcessor.GENERATED_CLASSNAME_SUFFIX;
    }
}
