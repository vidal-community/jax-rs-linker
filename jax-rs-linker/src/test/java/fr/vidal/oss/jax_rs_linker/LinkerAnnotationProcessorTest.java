package fr.vidal.oss.jax_rs_linker;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.lang.System.lineSeparator;

import javax.tools.JavaFileObject;
import org.junit.Rule;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.CompilationRule;

public class LinkerAnnotationProcessorTest {

    @Rule
    public CompilationRule compilation = new CompilationRule();

    private LinkerAnnotationProcessor processor = new LinkerAnnotationProcessor();
    private ExposedApplicationAnnotationProcessor applicationNameProcessor = new ExposedApplicationAnnotationProcessor();

    @Test
    public void generates_graph_of_linkers() {
        assert_().about(javaSources())
            .that(ImmutableList.of(
                forResource("Configuration.java"),
                forResource("ProductResource.java"),
                forResource("BrandResource.java"),
                forResource("PersonResource.java")
            ))
            .processedWith(processor, applicationNameProcessor)
            .compilesWithoutError()
            .and()
            .generatesSources(
                    forResource("ProductResourceLinker.java"),
                    forResource("PersonResourceLinker.java"),
                    forResource("BrandResourceLinker.java"),
                    forResource("ProductResourcePathParameters.java"),
                    forResource("BrandResourcePathParameters.java"),
                    forResource("PersonResourcePathParameters.java"),
                    forResource("PersonResourceQueryParameters.java"),
                    forResource("linkers/ContextPathHolder.java")
            );
    }

    @Test
    public void generates_linker_without_path_parameters() throws Exception {
        assert_().about(javaSources())
                .that(ImmutableList.of(
                        forResource("Configuration.java"),
                        forResource("DevNullResource.java")
                ))
                .processedWith(processor, applicationNameProcessor)
                .compilesWithoutError()
                .and()
                .generatesSources(
                        forResource("DevNullResourceLinker.java")
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
                    "\n  \tGiven method: <fr.vidal.oss.jax_rs_linker.parser.ProductResource#getBrandByProductId>"
            );
    }

    @Test
    public void fails_to_generate_linkers_if_too_many_self_annotations_on_1_resource() {
        assert_().about(javaSource())
                .that(forResource("SelfObsessedResource.java"))
                .processedWith(processor, applicationNameProcessor)
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
                .processedWith(processor, applicationNameProcessor)
                .failsToCompile()
                .withErrorContaining(
                        "\n  \tThe enclosing class already defined one @Self-annotated method. Only one method should be annotated so." +
                        "\n  \tGiven method: <SelfObsessedResource#getMoreSelf>"
                );
    }

    @Test
    public void fails_to_compile_ambiguous_configuration_class() {
        JavaFileObject configuration = forResource("Misconfiguration.java");

        assert_().about(javaSource())
            .that(configuration)
            .processedWith(processor, applicationNameProcessor)
            .failsToCompile()
            .withErrorContaining(
                "\n  \tEither annotate your configuration class with @ApplicationPath or provide a servletName to @ExposedApplication (not both)." +
                "\n  \tGiven class: <Misconfiguration>"
            )
            .in(configuration);
    }

    @Test
    public void generate_linkers_with_exposed_application_on_package() {
        assert_().about(javaSources())
            .that(ImmutableList.of(
                forResource("package-info.java"),
                forResource("BrandResource.java")
            ))
            .processedWith(processor, applicationNameProcessor)
            .compilesWithoutError()
            .and()
            .generatesSources(
                forResource("BrandResourceLinker.java"),
                forResource("linkers/ContextPathHolder.java"),
                forResource("linkers/ApplicationNamePackageInfo.java")
            );
    }

    @Test
    public void fails_to_compile_without_jaxrs_configuration_and_servletName() {
        JavaFileObject configuration = forResource("mispackage/package-info.java");

        assert_().about(javaSource())
            .that(configuration)
            .processedWith(processor, applicationNameProcessor)
            .failsToCompile()
            .withErrorContaining(
                "\n  \t@ExposedApplication servletName must not be empty when used on a package." +
                "\n  \tGiven package: <mispackage>"
            )
            .in(configuration);
    }

    @Test
    public void detects_all_query_parameters_even_when_none_on_self_method() {
        JavaFileObject resource = forResource("query_parameters_misdetection/PeopleResource.java");

        assert_().about(javaSources())
            .that(ImmutableList.of(
                forResource("Configuration.java"),
                resource
            ))
            .processedWith(processor, applicationNameProcessor)
            .compilesWithoutError()
            .and()
            .generatesSources(
                forResource("query_parameters_misdetection/PeopleResourceQueryParameters.java"),
                forResource("query_parameters_misdetection/PeopleResourceLinker.java")
            );
    }

    @Test
    public void does_not_compile_when_no_self_defined() {
        JavaFileObject resource = forResource("subresource_without_self/SelflessResource.java");

        assert_().about(javaSources())
            .that(ImmutableList.of(
                forResource("Configuration.java"),
                resource
            ))
            .processedWith(processor, applicationNameProcessor)
            .failsToCompile()
            .withErrorContaining("The following classes need exactly 1 method annotated with @Self annotation:" + lineSeparator() +
                "  \t - subresource_without_self.SelflessResource");
    }
}
