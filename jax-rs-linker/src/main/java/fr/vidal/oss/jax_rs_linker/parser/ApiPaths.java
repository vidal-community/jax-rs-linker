package fr.vidal.oss.jax_rs_linker.parser;

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
}
