package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.collect.Lists;
import fr.vidal.oss.jax_rs_linker.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static fr.vidal.oss.jax_rs_linker.functions.MappingToDot.TO_DOT_STATEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MappingToDotTest {

    @Mock
    Map.Entry<ClassName, Mapping> entry;

    @Test
    public void maps_subresource_mapping_to_dot_statement() {
        ClassName bar = ClassName.valueOf("com.acme.Bar");
        when(entry.getKey()).thenReturn(bar);
        when(entry.getValue()).thenReturn(new Mapping(
                new JavaLocation(bar, "dealWithIt"),
                new Api(
                        HttpVerb.GET,
                        ApiLink.SUB_RESOURCE(new SubResourceTarget(ClassName.valueOf("com.acme.Foo"), "")),
                        new ApiPath("/{id}", Lists.<PathParameter>newArrayList()),
                        null)
        ));

        assertThat(TO_DOT_STATEMENT.apply(entry))
                .isEqualTo("\tcom_acme_Bar -> com_acme_Foo [label=\"/{id}\"];");
    }

    @Test
    public void maps_self_mapping_to_null() {
        ClassName bar = ClassName.valueOf("com.acme.Bar");
        when(entry.getKey()).thenReturn(bar);
        when(entry.getValue()).thenReturn(new Mapping(
                new JavaLocation(bar, "dealWithIt"),
                new Api(
                        HttpVerb.GET,
                        ApiLink.SELF(),
                        new ApiPath("/{id}", Lists.<PathParameter>newArrayList()),
                        null)
        ));

        assertThat(TO_DOT_STATEMENT.apply(entry)).isNull();
    }
}
