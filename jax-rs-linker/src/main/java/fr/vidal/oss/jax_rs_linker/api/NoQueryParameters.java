package fr.vidal.oss.jax_rs_linker.api;

public enum NoQueryParameters implements QueryParameters {
    ;

    @Override
    public String value() {
        throw new UnsupportedOperationException();
    }
}
