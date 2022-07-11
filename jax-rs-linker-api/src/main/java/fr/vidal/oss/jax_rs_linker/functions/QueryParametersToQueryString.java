package fr.vidal.oss.jax_rs_linker.functions;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public enum QueryParametersToQueryString implements Function<Map<String,Collection<String>>,String> {

    TO_QUERY_STRING;

    private static final String QUERY_OPERATOR = "?";

    @Override
    public String apply(Map<String, Collection<String>> input) {
        if (input.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(QUERY_OPERATOR);
        for (Map.Entry<String, Collection<String>> valuesPerKey : input.entrySet()) {
            String key = valuesPerKey.getKey();
            for (String value : valuesPerKey.getValue()) {
                builder.append(key).append("=").append(value).append("&");
            }
        }
        return builder.substring(0, builder.length() - 1);
    }
}
