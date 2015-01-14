package fr.vidal.oss.jax_rs_linker.parser;

import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import javax.annotation.Generated;

@Generated("fr.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor")
public enum PersonResourcePathParameters
    implements PathParameters {
	FIRST_NAME("firstName"),
	ID("id");

	private final String placeholder;

	PersonResourcePathParameters(String placeholder) {
		this.placeholder = placeholder;
	}

    @Override
    public String placeholder() {
        return this.placeholder;
    }
}
