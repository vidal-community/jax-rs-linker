package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import fr.vidal.oss.jax_rs_linker.functions.VariableElementToPathParameter;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.predicates.ElementHasAnnotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.String.format;

class PathVisitor {

    private final Types types;

    public PathVisitor(Types types) {
        this.types = types;
    }

    public Optional<String> visitPath(ExecutableElement element) {
        String aggregatedPath = emptyToNull(
            aggregate(
                currentPath(element).or(""),
                element.getEnclosingElement()
            )
        );
        return fromNullable(aggregatedPath);
    }

    public Collection<PathParameter> visitParameters(ExecutableElement element) {
        return FluentIterable.from(element.getParameters())
            .filter(ElementHasAnnotation.BY_ANNOTATION(PathParam.class))
            .transform(VariableElementToPathParameter.INTO_PATH_PARAMETER)
            .toList();
    }

    private String aggregate(String acc, Element element) {
        checkArgument(element.getKind() == ElementKind.CLASS, format("Expecting only class at this point. Given <%s>", element));

        String path = prepend(currentPath(element), acc);
        TypeMirror superclass = ((TypeElement) element).getSuperclass();
        if (superclass.getKind() == TypeKind.NONE) {
            return path;
        }
        return aggregate(path, types.asElement(superclass));
    }

    private String prepend(Optional<String> maybePath, String accumulator) {
        if (!maybePath.isPresent()) {
            return accumulator;
        }
        String path = maybePath.get();
        if (accumulator.isEmpty()) {
            return path;
        }

        if (path.endsWith("/") || accumulator.startsWith("/")) {
            return format("%s%s", path, accumulator);
        }
        return format("%s/%s", path, accumulator);
    }

    private Optional<String> currentPath(Element element) {
        Path annotation = element.getAnnotation(Path.class);
        if (annotation == null) {
            return absent();
        }
        return Optional.of(annotation.value());
    }
}
