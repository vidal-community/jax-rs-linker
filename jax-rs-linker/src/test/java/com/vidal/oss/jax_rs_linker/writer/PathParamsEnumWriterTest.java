package com.vidal.oss.jax_rs_linker.writer;

import com.squareup.javawriter.JavaWriter;
import com.vidal.oss.jax_rs_linker.model.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class PathParamsEnumWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_generate_correct_enum() throws IOException {
        ClassName generatedEnum = ClassName.valueOf("com.vidal.oss.jax_rs_linker.parser.PersonResourcePathParameters");
        File generatedEnumFile = temporaryFolder.newFile(generatedEnum.getName());
        JavaWriter javaWriter = new JavaWriter(new BufferedWriter(new FileWriter(generatedEnumFile)));

        try (PathParamsEnumWriter writer = new PathParamsEnumWriter(javaWriter)) {
            writer.write(generatedEnum, mappings());
        }

        String generatedBrandResourceEnum = fileContent(generatedEnumFile, StandardCharsets.UTF_8);
        String brandResourceEnum = fileContent(new File(getClass().getClassLoader().getResource("PersonResourcePathParameters.java").getFile()), StandardCharsets.UTF_8);

        assertThat(generatedBrandResourceEnum).isEqualTo(brandResourceEnum.replaceAll("    ", "\t"));
    }

    private String fileContent(File generatedEnumFile, Charset charset) throws IOException {
        return new String(Files.readAllBytes(Paths.get(generatedEnumFile.getAbsolutePath())), charset);
    }

    private Collection<Mapping> mappings() {
        JavaLocation javaLocation = new JavaLocation(ClassName.valueOf("com.vidal.oss.jax_rs_linker.parser.PersonResourcePathParameters"), "getById");
        ApiPath apiPath = new ApiPath("/person/{id}", newArrayList(new PathParameter(ClassName.valueOf("int"), "id")));
        ApiLink apiLink = ApiLink.SELF();
        Api api = new Api(HttpVerb.GET, apiLink, apiPath);
        Mapping mapping = new Mapping(javaLocation, api);

        javaLocation = new JavaLocation(ClassName.valueOf("com.vidal.oss.jax_rs_linker.parser.PersonResourcePathParameters"), "getByFirstName");
        apiPath = new ApiPath("/person/name/{firstName}", newArrayList(new PathParameter(ClassName.valueOf("String"), "firstName")));
        apiLink = ApiLink.SELF();
        api = new Api(HttpVerb.GET, apiLink, apiPath);
        Mapping mapping2 = new Mapping(javaLocation, api);

        return newArrayList(mapping, mapping2);
    }
}