package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static fr.vidal.oss.jax_rs_linker.functions.MappingToDot.TO_DOT_STATEMENT;

public class DotFileWriter implements AutoCloseable {

    private final Writer writer;

    public DotFileWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(Multimap<ClassName, Mapping> elements) {
        try {
            writer.append(Joiner.on("\n").join(graph(elements)));
        } catch (IOException e) {
            throw propagate(e);
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw propagate(e);
        }
    }

    private Iterable<String> graph(Multimap<ClassName, Mapping> elements) {
        return ImmutableList.<String>builder()
            .add("digraph resources {")
            .addAll(mappings(elements))
            .add("}")
            .build();
    }

    private Iterable<String> mappings(Multimap<ClassName, Mapping> elements) {
        return FluentIterable.from(elements.entries())
            .transform(TO_DOT_STATEMENT)
            .filter(notNull())
            .toList();
    }

}
