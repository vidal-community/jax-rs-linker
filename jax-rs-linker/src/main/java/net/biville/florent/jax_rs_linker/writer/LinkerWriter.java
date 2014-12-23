package net.biville.florent.jax_rs_linker.writer;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.squareup.javawriter.JavaWriter;
import net.biville.florent.jax_rs_linker.LinkerAnnotationProcessor;
import net.biville.florent.jax_rs_linker.functions.ClassToName;
import net.biville.florent.jax_rs_linker.model.*;
import net.biville.florent.jax_rs_linker.predicates.MappingByApiLinkTargetPredicate;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Sets.immutableEnumSet;
import static com.squareup.javawriter.JavaWriter.stringLiteral;
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
            .emitImports(Optional.class, Generated.class, Arrays.class, HashMap.class, Map.class, ApiPath.class, ClassName.class, PathParameter.class, TemplatedPath.class, ClassToName.class)
            .emitEmptyLine()
            .emitAnnotation(Generated.class, processorQualifiedName())
            .beginType(generatedClass.getName(), "class", EnumSet.of(Modifier.PUBLIC))
            .emitEmptyLine()
            .emitField("Map<ClassName, ApiPath>", "relatedMappings", immutableEnumSet(Modifier.PRIVATE, Modifier.FINAL), "new HashMap<>()")
            .emitEmptyLine()
            .beginConstructor(immutableEnumSet(Modifier.PUBLIC));

        for (Mapping mapping : linked(mappings)) {
            Api api = mapping.getApi();
            ClassName target = api.getApiLink().getTarget().get();
            String statementTemplate = "relatedMappings.put(%nClassName.valueOf(%s),%nnew ApiPath(%s, Arrays.asList(%s)))";
            ApiPath apiPath = api.getApiPath();
            writer = writer.emitStatement(
                statementTemplate,
                stringLiteral(target.getName()),
                stringLiteral(apiPath.getPath()),
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
        writer.endConstructor()
            .emitEmptyLine()
            .beginMethod("TemplatedPath", "self", immutableEnumSet(Modifier.PUBLIC))
            .emitStatement("return new TemplatedPath(%s, Arrays.asList(%s))", stringLiteral(apiPath.getPath()), parameters(apiPath.getPathParameters()))
            .endMethod()
            .emitEmptyLine()
            .beginMethod("Optional<TemplatedPath>", "related", immutableEnumSet(Modifier.PUBLIC), "Class<?>", "resourceClass")
            .emitStatement("ApiPath path = relatedMappings.get(ClassToName.INSTANCE.apply(resourceClass))")
            .beginControlFlow("if (path == null)")
            .emitStatement("return Optional.<TemplatedPath>absent()")
            .endControlFlow()
            .emitStatement("return Optional.of(new TemplatedPath(path.getPath(), path.getPathParameters()))")
            .endMethod()
            .endType();

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
                    return String.format(
                        "new PathParameter(ClassName.valueOf(%s), %s)",
                        stringLiteral(input.getType().getName()),
                        stringLiteral(input.getName())
                    );
                }
            })
            .join(Joiner.on(", "));
    }

    private ImmutableMap<String, String> processorQualifiedName() {
        return ImmutableMap.of("value", format("%s", stringLiteral(LinkerAnnotationProcessor.class.getName())));
    }

}
