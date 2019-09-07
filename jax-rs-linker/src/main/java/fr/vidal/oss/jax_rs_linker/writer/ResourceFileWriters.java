package fr.vidal.oss.jax_rs_linker.writer;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

public class ResourceFileWriters {

    private final Filer filer;

    public ResourceFileWriters(Filer filer) {
        this.filer = filer;
    }

    public Writer writer(String fileName) {
        try {
            return filer
                .createResource(CLASS_OUTPUT, "", fileName)
                .openWriter();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
