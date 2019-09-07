package fr.vidal.oss.jax_rs_linker.writer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fr.vidal.oss.jax_rs_linker.model.Api;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.ClassNameGeneration;
import fr.vidal.oss.jax_rs_linker.model.HttpVerb;
import fr.vidal.oss.jax_rs_linker.model.JavaLocation;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.SubResourceTarget;
import org.junit.After;
import org.junit.Test;

import javax.lang.model.element.TypeElement;
import java.io.StringWriter;

import static fr.vidal.oss.jax_rs_linker.model.ApiLink.SUB_RESOURCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DotFileWriterTest {

    private final StringWriter string = new StringWriter();
    private DotFileWriter writer = new DotFileWriter(string);

    @Test
    public void writes_dot_file() {
        writer.write(mappings());

        assertThat(string.toString()).isEqualTo(
            "dinetwork {\n" +
            "\tcom_acme_Foo -> com_acme_Bar [label=\"/foo/{id}/bar\"];\n" +
            "}"
        );
    }

    @After
    public void cleanUp() {
        writer.close();
    }

    private Multimap<ClassNameGeneration, Mapping> mappings() {
        HashMultimap<ClassNameGeneration, Mapping> multimap = HashMultimap.create();
        ClassNameGeneration foo = new ClassNameGeneration(ClassName.valueOf("com.acme.Foo"), mock(TypeElement.class));
        multimap.put(
                foo,
                new Mapping(
                        new JavaLocation(foo, "doIt"),
                        new Api(
                                HttpVerb.GET,
                                SUB_RESOURCE(new SubResourceTarget(ClassName.valueOf("com.acme.Bar"), "")),
                                new ApiPath("/foo/{id}/bar", Lists.newArrayList()),
                                null)
                )
        );
        return multimap;
    }
}
