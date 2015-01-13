package com.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import com.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import com.vidal.oss.jax_rs_linker.api.NoPathParameters;
import com.vidal.oss.jax_rs_linker.model.Api;
import com.vidal.oss.jax_rs_linker.model.ApiLinkType;
import com.vidal.oss.jax_rs_linker.model.ClassName;
import com.vidal.oss.jax_rs_linker.model.Mapping;
import com.vidal.oss.jax_rs_linker.model.PathParameter;
import com.vidal.oss.jax_rs_linker.predicates.MappingByApiLinkTargetPredicate;
import com.vidal.oss.jax_rs_linker.functions.ClassToName;
import com.vidal.oss.jax_rs_linker.model.ApiPath;
import com.vidal.oss.jax_rs_linker.model.TemplatedPath;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Sets.immutableEnumSet;
import static java.lang.String.format;

public class LinkerWriter implements AutoCloseable {

    private final JavaWriter javaWriter;

    public LinkerWriter(JavaWriter javaWriter) {
        this.javaWriter = javaWriter;
    }

    public void write(ClassName generatedClass, Collection<Mapping> mappings) throws IOException {
        javaWriter.setIndent("\t");
        JavaWriter writer = javaWriter
            .emitPackage(generatedClass.packageName())
            .emitImports(Optional.class, Generated.class, Arrays.class, HashMap.class, Map.class, ApiPath.class, ClassName.class, PathParameter.class, TemplatedPath.class, ClassToName.class, NoPathParameters.class)
            .emitEmptyLine()
            .emitAnnotation(Generated.class, LinkerAnnotationProcessor.processorQualifiedName())
            .beginType(generatedClass.fullyQualifiedName(), "class", EnumSet.of(Modifier.PUBLIC))
            .emitEmptyLine()
            .emitField("Map<ClassName, ApiPath>", "relatedMappings", immutableEnumSet(Modifier.PRIVATE, Modifier.FINAL), "new HashMap<>()")
            .emitField("String", "contextPath", immutableEnumSet(Modifier.PRIVATE, Modifier.FINAL))
            .emitEmptyLine()
            .beginConstructor(immutableEnumSet(Modifier.PUBLIC))
            .emitStatement("this(\"\")")
            .endConstructor()
            .emitEmptyLine()
            .beginConstructor(immutableEnumSet(Modifier.PUBLIC), "String", "contextPath")
            .emitStatement("this.contextPath = contextPath");

        for (Mapping mapping : linked(mappings)) {
            Api api = mapping.getApi();
            ClassName target = api.getApiLink().getTarget().get();
            String statementTemplate = "relatedMappings.put(%nClassName.valueOf(%s),%nnew ApiPath(%s, Arrays.<PathParameter>asList(%s)))";
            ApiPath apiPath = api.getApiPath();
            writer = writer.emitStatement(
                statementTemplate,
                StringLiteral.forValue(target.fullyQualifiedName()).literal(),
                StringLiteral.forValue(apiPath.getPath()).literal(),
                parameters(apiPath.getPathParameters())
            );
        }
        Optional<Mapping> mapping = FluentIterable.from(mappings)
            .firstMatch(new Predicate<Mapping>() {
                @Override
                public boolean apply(@Nullable Mapping input) {
                    return input.getApi().getApiLink().getApiLinkType() == ApiLinkType.SELF;
                }
            });

        ApiPath apiPath = mapping.get().getApi().getApiPath();
        String parameterizedTemplatedPath = parameterizedTemplatedPath(apiPath, generatedClass.className());
        writer.endConstructor()
            .emitEmptyLine()
            .beginMethod(parameterizedTemplatedPath, "self", immutableEnumSet(Modifier.PUBLIC))
            .emitStatement("return new %s(contextPath + %s, Arrays.<PathParameter>asList(%s))", parameterizedTemplatedPath, StringLiteral.forValue(apiPath.getPath()).literal(), parameters(apiPath.getPathParameters()))
            .endMethod()
            .emitEmptyLine()
            .beginMethod(format("Optional<%s>", parameterizedTemplatedPath), "related", immutableEnumSet(Modifier.PUBLIC), "Class<?>", "resourceClass")
            .emitStatement("ApiPath path = relatedMappings.get(ClassName.valueOf(ClassToName.INSTANCE.apply(resourceClass)))")
            .beginControlFlow("if (path == null)")
            .emitStatement("return Optional.<%s>absent()", parameterizedTemplatedPath)
            .endControlFlow()
            .emitStatement("return Optional.of(new %s(contextPath + path.getPath(), path.getPathParameters()))", parameterizedTemplatedPath)
            .endMethod()
            .endType();

    }

    private String parameterizedTemplatedPath(ApiPath apiPath, String generatedClass) {
        if (apiPath.getPathParameters().isEmpty()) {
            return format("TemplatedPath<%s>", NoPathParameters.class.getSimpleName());
        }
        return format("TemplatedPath<%s>", generatedClass.replace("Linker", "PathParameters"));
    }

    private Iterable<Mapping> linked(Collection<Mapping> mappings) {
        return FluentIterable.from(mappings)
            .filter(MappingByApiLinkTargetPredicate.BY_API_LINK_TARGET_PRESENCE)
            .toList();
    }

    @Override
    public void close() {
        try {
            javaWriter.close();
        } catch (IOException e) {
            throw propagate(e);
        }
    }


    private String parameters(Collection<PathParameter> pathParameters) {
        return FluentIterable.from(pathParameters)
            .transform(new Function<PathParameter, String>() {
                @Nullable
                @Override
                public String apply(PathParameter input) {
                    return format(
                            "new PathParameter(ClassName.valueOf(%s), %s)",
                            StringLiteral.forValue(input.getType().fullyQualifiedName()).literal(),
                            StringLiteral.forValue(input.getName()).literal()
                    );
                }
            })
            .join(Joiner.on(", "));
    }

}
