package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import fr.vidal.oss.jax_rs_linker.api.NoPathParameters;
import fr.vidal.oss.jax_rs_linker.model.Api;
import fr.vidal.oss.jax_rs_linker.model.ApiLinkType;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.predicates.MappingByApiLinkTargetPredicate;
import fr.vidal.oss.jax_rs_linker.functions.ClassToName;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.TemplatedPath;

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
            .emitImports(Generated.class, Arrays.class, ApiPath.class, ClassName.class, PathParameter.class, TemplatedPath.class, NoPathParameters.class)
            .emitEmptyLine()
            .emitAnnotation(Generated.class, LinkerAnnotationProcessor.processorQualifiedName())
            .beginType(generatedClass.fullyQualifiedName(), "class", EnumSet.of(Modifier.PUBLIC))
            .emitEmptyLine()
            .emitField("String", "contextPath", immutableEnumSet(Modifier.PRIVATE, Modifier.FINAL))
            .emitEmptyLine()
            .beginConstructor(immutableEnumSet(Modifier.PUBLIC))
            .emitStatement("this(\"\")")
            .endConstructor()
            .emitEmptyLine()
            .beginConstructor(immutableEnumSet(Modifier.PUBLIC), "String", "contextPath")
            .emitStatement("this.contextPath = contextPath");

        Optional<Mapping> selfMapping = FluentIterable.from(mappings)
                .firstMatch(new Predicate<Mapping>() {
                    @Override
                    public boolean apply(@Nullable Mapping input) {
                        return input.getApi().getApiLink().getApiLinkType() == ApiLinkType.SELF;
                    }
                });

        ApiPath selfApiPath = selfMapping.get().getApi().getApiPath();
        String parameterizedTemplatedPath = parameterizedTemplatedPath(selfMapping.get().getApi().getApiPath(), generatedClass.className());
        writer = writer.endConstructor()
                .emitEmptyLine()
                .beginMethod(parameterizedTemplatedPath, "self", immutableEnumSet(Modifier.PUBLIC))
                .emitStatement("return new %s(contextPath + %s, Arrays.<PathParameter>asList(%s))", parameterizedTemplatedPath, StringLiteral.forValue(selfApiPath.getPath()).literal(), parameters(selfApiPath.getPathParameters()))
                .endMethod()
                .emitEmptyLine();

        for (Mapping mapping : linked(mappings)) {
            Api api = mapping.getApi();
            ClassName target = api.getApiLink().getTarget().get();
            writer = writer.beginMethod(parameterizedTemplatedPath, format("related%s", target.className()), immutableEnumSet(Modifier.PUBLIC))
                    .emitStatement("ApiPath path = new ApiPath(%s, Arrays.<PathParameter>asList(%s))",
                            StringLiteral.forValue(api.getApiPath().getPath()).literal(),
                            parameters(api.getApiPath().getPathParameters()))
                    .emitStatement("return new %s(contextPath + path.getPath(), path.getPathParameters())", parameterizedTemplatedPath)
                    .endMethod()
                    .emitEmptyLine();
        }
        writer.endType();

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
