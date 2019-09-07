package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import fr.vidal.oss.jax_rs_linker.model.ClassNameGeneration;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Objects;

import static fr.vidal.oss.jax_rs_linker.functions.MappingToDot.TO_DOT_STATEMENT;
import static java.util.stream.Collectors.toList;

public class DotFileWriter implements AutoCloseable {

    private final Writer writer;

    public DotFileWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(Multimap<ClassNameGeneration, Mapping> elements) {
        try {
            writer.append(Joiner.on("\n").join(graph(elements)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Iterable<String> graph(Multimap<ClassNameGeneration, Mapping> elements) {
        return ImmutableList.<String>builder()
            .add("dinetwork {")
            .addAll(mappings(elements))
            .add("}")
            .build();
    }

    private Iterable<String> mappings(Multimap<ClassNameGeneration, Mapping> elements) {
        return elements.entries().stream()
            .map(TO_DOT_STATEMENT)
            .filter(Objects::nonNull)
            .collect(toList());
    }

}
