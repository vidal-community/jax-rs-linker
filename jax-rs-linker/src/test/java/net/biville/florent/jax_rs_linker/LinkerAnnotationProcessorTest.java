package net.biville.florent.jax_rs_linker;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.CompilationRule;
import org.junit.Rule;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;


public class LinkerAnnotationProcessorTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();

    private LinkerAnnotationProcessor processor = new LinkerAnnotationProcessor();

    @Test
    public void generates_graph_of_linkers() {
        assert_().about(javaSources())
            .that(ImmutableList.of(
                forResource("ProductResource.java"),
                forResource("BrandResource.java")
            ))
            .processedWith(processor)
            .compilesWithoutError()
            .and()
            .generatesSources(
                    forResource("ProductResourceLinker.java"),
                    forResource("BrandResourceLinker.java"),
                    forResource("linkers/Linkers.java")
            );
    }

    @Test
    public void fails_to_generate_linkers_if_not_all_resources_processed() {
        assert_().about(javaSources())
            .that(ImmutableList.of(
                forResource("ProductResource.java")
            ))
            .processedWith(new LinkerAnnotationProcessor())
            .failsToCompile()
            .withErrorContaining(
                "\n  \tThe following method links to an unreachable resource class via @SubResource." +
                    "\n  \tGiven method: <net.biville.florent.jax_rs_linker.parser.ProductResource#getBrandByProductId>"
            );
    }

    @Test
    public void fails_to_generate_linkers_if_too_many_self_annotations_on_1_resource() {
        assert_().about(javaSource())
                .that(forResource("SelfObsessedResource.java"))
                .processedWith(processor)
                .failsToCompile()
                .withErrorContaining(
                        "\n  \tThe enclosing class already defined one @Self-annotated method. Only one method should be annotated so." +
                        "\n  \tGiven method: <SelfObsessedResource#getMoreSelf>"
                );
    }
    
    @Test
    public void fails_to_generate_linkers_if_related_resource_has_too_many_self_annotations() {
        assert_().about(javaSources())
                .that(ImmutableList.of(
                        forResource("GalleryResource.java"),
                        forResource("SelfObsessedResource.java")
                ))
                .processedWith(processor)
                .failsToCompile()
                .withErrorContaining(
                        "\n  \tThe enclosing class already defined one @Self-annotated method. Only one method should be annotated so." +
                        "\n  \tGiven method: <SelfObsessedResource#getMoreSelf>"
                );
    }

}