package net.biville.florent.jax_rs_linker.errors;

public enum Messages {

    NO_PATH_FOUND(
        "\n\tNo path could be found. " +
        "\n\tPlease make sure JAX-RS @Path is set on the method, its class or superclasses." +
        "\n\tGiven method: <%s>"
    ),
    ANNOTATION_MISUSE(
        "\n\tMethod should be annotated with exactly one annotation: @Self or @SubResource." +
        "\n\tGiven method: <%s>"
    ),
    NOT_A_METHOD(
        "\n\tElement should be a method, directly wrapped in a class." +
        "\n\tGiven method: <%s>"
    ),
    NO_HTTP_ANNOTATION_FOUND(
        "\n\tNo JAX-RS HTTP verb annotation found (e.g. @GET, @POST...)." +
        "\n\tGiven method: <%s>"
    );

    private final String errorMessage;

    Messages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String text() {
        return errorMessage;
    }

    public String format(Object... args) {
        return String.format(errorMessage, args);
    }
}
