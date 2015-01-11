package com.vidal.oss.jax_rs_linker.writer;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.squareup.javawriter.JavaWriter;
import com.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import com.vidal.oss.jax_rs_linker.functions.PathParameterToString;
import com.vidal.oss.jax_rs_linker.model.ClassName;
import com.vidal.oss.jax_rs_linker.model.Mapping;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Sets.immutableEnumSet;
import static com.vidal.oss.jax_rs_linker.functions.MappingToPathParameters.TO_PATH_PARAMETERS;
import static com.vidal.oss.jax_rs_linker.functions.PathParameterToString.TO_STRING;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public class PathParamsEnumWriter implements AutoCloseable {

    private final JavaWriter javaWriter;

    public PathParamsEnumWriter(JavaWriter javaWriter) {
        this.javaWriter = javaWriter;
    }

    public void write(ClassName generatedClass, Collection<Mapping> mappings) throws IOException {
        javaWriter.setIndent("\t");
        JavaWriter writer = javaWriter
            .emitPackage(generatedClass.packageName())
            .emitImports(Generated.class)
            .emitEmptyLine()
            .emitAnnotation(Generated.class, LinkerAnnotationProcessor.processorQualifiedName())
            .beginType(generatedClass.getName(), "enum", EnumSet.of(PUBLIC));

        writeEnumeration(mappings, writer);

        writer.emitEmptyLine()
            .emitField("String", "value", immutableEnumSet(PRIVATE, FINAL))
            .emitEmptyLine()
            .beginConstructor(Collections.<Modifier>emptySet(), "String", "value")
                .emitStatement("this.value = value")
            .endConstructor()
            .emitEmptyLine()
            .beginMethod("String", "value", Collections.<Modifier>emptySet())
            .emitStatement("return this.value")
            .endMethod()
            .endType();

    }

    private void writeEnumeration(Collection<Mapping> mappings, JavaWriter writer) throws IOException {
        Collection<String> apiPathsEnums = getApiPathsEnums(mappings);
        Iterator<String> it = apiPathsEnums.iterator();
        while(it.hasNext()) {
            writer.emitEnumValue(it.next(), !it.hasNext());
        }
    }

    private Collection<String> getApiPathsEnums(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
                .transformAndConcat(TO_PATH_PARAMETERS)
                .transform(TO_STRING)
                .toSortedSet(Ordering.natural());
    }

    @Override
    public void close() {
        try {
            javaWriter.close();
        } catch (IOException e) {
            throw propagate(e);
        }
    }

}
