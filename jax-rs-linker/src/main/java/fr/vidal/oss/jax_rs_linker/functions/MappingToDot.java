package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import fr.vidal.oss.jax_rs_linker.model.*;

import javax.annotation.Nullable;
import java.util.Map;

import static java.lang.String.format;

public enum MappingToDot implements Function<Map.Entry<ClassName, Mapping>, String> {

    TO_DOT_STATEMENT;

    @Nullable
    @Override
    public String apply(Map.Entry<ClassName, Mapping> input) {
        Api api = input.getValue().getApi();
        ApiLink apiLink = api.getApiLink();
        if (apiLink.getApiLinkType() != ApiLinkType.SUB_RESOURCE) {
            return null;
        }
        return format(
                "\t%s -> %s [label=\"%s\"];",
                escape(input.getKey()),
                escape(apiLink.getTarget().get()),
                api.getApiPath().getPath()
        );
    }

    private String escape(ClassName name) {
        return name.fullyQualifiedName().replace(".", "_");
    }
}
