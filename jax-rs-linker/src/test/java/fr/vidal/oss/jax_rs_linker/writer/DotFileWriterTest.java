package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fr.vidal.oss.jax_rs_linker.model.*;
import org.junit.After;
import org.junit.Test;

import java.io.StringWriter;

import static fr.vidal.oss.jax_rs_linker.model.ApiLink.SUB_RESOURCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DotFileWriterTest {

    private final StringWriter string = new StringWriter();
    private DotFileWriter writer = new DotFileWriter(string);

    @Test
    public void writes_dot_file() {
        writer.write(mappings());

        assertThat(string.toString()).isEqualTo(
            "digraph resources {\n" +
            "\tcom_acme_Foo -> com_acme_Bar [label=\"/foo/{id}/bar\"];\n" +
            "}"
        );
    }

    @After
    public void cleanUp() {
        writer.close();
    }

    private Multimap<ClassName, Mapping> mappings() {
        HashMultimap<ClassName, Mapping> multimap = HashMultimap.<ClassName, Mapping>create();
        multimap.put(
                ClassName.valueOf("com.acme.Foo"),
                new Mapping(
                        new JavaLocation(ClassName.valueOf("com.acme.Foo"), "doIt"),
                        new Api(
                                HttpVerb.GET,
                                SUB_RESOURCE(ClassName.valueOf("com.acme.Bar")),
                                new ApiPath("/foo/{id}/bar", Lists.<PathParameter>newArrayList())
                        )
                )
        );
        return multimap;
    }
}