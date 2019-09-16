package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.collect.Lists;
import fr.vidal.oss.jax_rs_linker.model.Api;
import fr.vidal.oss.jax_rs_linker.model.ApiLink;
import fr.vidal.oss.jax_rs_linker.model.ApiPath;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.ClassNameGeneration;
import fr.vidal.oss.jax_rs_linker.model.HttpVerb;
import fr.vidal.oss.jax_rs_linker.model.JavaLocation;
import fr.vidal.oss.jax_rs_linker.model.Mapping;
import fr.vidal.oss.jax_rs_linker.model.SubResourceTarget;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.lang.model.element.TypeElement;
import java.util.Map;

import static fr.vidal.oss.jax_rs_linker.functions.MappingToDot.TO_DOT_STATEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MappingToDotTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    Map.Entry<ClassNameGeneration, Mapping> entry;

    @Test
    public void maps_subresource_mapping_to_dot_statement() {
        ClassNameGeneration bar = new ClassNameGeneration(ClassName.valueOf("com.acme.Bar"), mock(TypeElement.class));
        when(entry.getKey()).thenReturn(bar);
        when(entry.getValue()).thenReturn(new Mapping(
                new JavaLocation(bar, "dealWithIt"),
                new Api(
                        HttpVerb.GET,
                        ApiLink.SUB_RESOURCE(new SubResourceTarget(ClassName.valueOf("com.acme.Foo"), "")),
                        new ApiPath("/{id}", Lists.newArrayList()),
                        null)
        ));

        assertThat(TO_DOT_STATEMENT.apply(entry))
                .isEqualTo("\tcom_acme_Bar -> com_acme_Foo [label=\"/{id}\"];");
    }

    @Test
    public void maps_self_mapping_to_null() {
        ClassNameGeneration bar = new ClassNameGeneration(ClassName.valueOf("com.acme.Bar"), mock(TypeElement.class));
        when(entry.getValue()).thenReturn(new Mapping(
                new JavaLocation(bar, "dealWithIt"),
                new Api(
                        HttpVerb.GET,
                        ApiLink.SELF(),
                        new ApiPath("/{id}", Lists.newArrayList()),
                        null)
        ));

        assertThat(TO_DOT_STATEMENT.apply(entry)).isNull();
    }
}
