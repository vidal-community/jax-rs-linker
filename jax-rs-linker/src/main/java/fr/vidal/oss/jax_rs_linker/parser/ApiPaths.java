package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Maps.newHashMap;

public final class ApiPaths {

    public static String sanitize(String path) {
        int colonPosition = path.indexOf(':');
        if (colonPosition == -1) {
            return path;
        }

        int closingBracePosition = colonPosition + path.substring(colonPosition).indexOf('}');
        return path.substring(0, colonPosition).trim()
            + sanitize(path.substring(closingBracePosition));
    }

    public static Collection<PathParameter> decorate(Collection<PathParameter> pathParameters, String path) {
        final Map<String, String> parametersRegex = extractParametersRegex(path);

        return FluentIterable.from(pathParameters).transform(new Function<PathParameter, PathParameter>() {
            @Nullable
            @Override
            public PathParameter apply(PathParameter pathParameter) {
                if (parametersRegex.containsKey(pathParameter.getName())) {
                    return new PathParameter(pathParameter.getType(),
                            pathParameter.getName(),
                            parametersRegex.get(pathParameter.getName())
                    );
                }
                return pathParameter;
            }
        }).toList();
    }

    private static Map<String, String> extractParametersRegex(String path) {
        Map<String, String> parameterAndRegex = newHashMap();

        StringTokenizer st = new StringTokenizer(path, "/");
        while (st.hasMoreTokens()) {
            String pathParam = st.nextToken();
            if (pathParam.contains("{")) {
                int colonPosition = pathParam.indexOf(':');
                if (colonPosition != -1) {
                    String name = pathParam.substring(1, colonPosition).trim();
                    String regex = pathParam.substring(colonPosition + 1, pathParam.length() - 1).trim();
                    parameterAndRegex.put(name, regex);
                }
            }

        }
        return parameterAndRegex;
    }
}
