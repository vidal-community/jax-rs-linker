package net.biville.florent.jax_rs_linker.writer;

import com.google.common.base.Throwables;
import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import net.biville.florent.jax_rs_linker.model.ClassName;

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
import static net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor.GENERATED_CLASSNAME_SUFFIX;
import static net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor.processorQualifiedName;
import static net.biville.florent.jax_rs_linker.functions.ClassNameToLinkerName.TO_LINKER_NAME;

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
                .emitImports(Generated.class, ServletContextEvent.class, ServletContextListener.class, WebListener.class)
                .emitImports(transform(classes, TO_LINKER_NAME))
                .emitEmptyLine()
                .emitAnnotation(WebListener.class)
                .emitAnnotation(Generated.class, processorQualifiedName())
                .beginType(linkers.className(), "class", EnumSet.of(PUBLIC), null, "ServletContextListener")
                .emitEmptyLine()
                .emitField("String", "contextPath", immutableEnumSet(PRIVATE, STATIC), "\"\"")
                .emitEmptyLine()
                .emitField("String", "applicationName", immutableEnumSet(PRIVATE, STATIC), StringLiteral.forValue(applicationName).literal())
                .emitAnnotation(Override.class)
                .beginMethod("void", "contextInitialized", immutableEnumSet(PUBLIC), "ServletContextEvent", "sce")
                .emitStatement("contextPath = sce.getServletContext().getContextPath() + sce.getServletContext().getServletRegistration(applicationName).getMappings().iterator().next()")
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
        return linker.className() + GENERATED_CLASSNAME_SUFFIX;
    }
}
