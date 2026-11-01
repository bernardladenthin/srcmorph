// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SrcMorphConfigurationFileLoaderTest {

    @TempDir
    Path tempDir;

    private static final Map<String, String> NO_PROPERTIES = Collections.emptyMap();

    private Path write(final String name, final String content) throws IOException {
        final Path file = tempDir.resolve(name);
        Files.createDirectories(file.getParent());
        return Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    private File defaultBase() {
        return tempDir.resolve("default-base").toFile();
    }

    // <editor-fold defaultstate="collapsed" desc="parsing">
    @Test
    public void load_yamlAndJson_bindEverySection() throws IOException {
        final String yaml = "command: All\nmodelFilter: grok\n"
                + "packageFieldGenerations:\n  - promptKey: package-body\n    aiDefinitionKey: m\n"
                + "projectFieldGenerations:\n  - promptKey: project-body\n    aiDefinitionKey: m\n"
                + "srcMorph:\n  generationProvider: mock\n";
        final String json = "{\"command\":\"All\",\"modelFilter\":\"grok\","
                + "\"packageFieldGenerations\":[{\"promptKey\":\"package-body\",\"aiDefinitionKey\":\"m\"}],"
                + "\"projectFieldGenerations\":[{\"promptKey\":\"project-body\",\"aiDefinitionKey\":\"m\"}],"
                + "\"srcMorph\":{\"generationProvider\":\"mock\"}}";

        for (final Path file :
                new Path[] {write("a.yaml", yaml), write("b.YML", yaml), write("c.json", json), write("d.js", json)}) {
            final SrcMorphConfigurationFile content =
                    SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES);

            assertThat(content.getCommand(), is(equalTo("All")));
            assertThat(content.getModelFilter(), is(equalTo("grok")));
            assertThat(content.getPackageFieldGenerations().get(0).getPromptKey(), is(equalTo("package-body")));
            assertThat(content.getProjectFieldGenerations().get(0).getPromptKey(), is(equalTo("project-body")));
            assertThat(content.getSrcMorph().getGenerationProvider(), is(equalTo("mock")));
        }
    }

    @Test
    public void load_anUnknownKey_failsNamingIt() throws IOException {
        final Path file = write("srcmorph.yaml", "srcMorph:\n  subtree: [\"src\"]\n");

        final IOException thrown = assertThrows(
                IOException.class, () -> SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES));

        assertThat(thrown.getMessage(), containsString("subtree"));
    }

    @Test
    public void load_anotherExtension_isRefused() throws IOException {
        final Path file = write("srcmorph.txt", "");

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES));

        assertThat(thrown.getMessage(), containsString("A configuration file ends with .yaml, .yml, .json or .js"));
    }

    @Test
    public void load_anEmptyFile_isEveryDefault() throws IOException {
        final SrcMorphConfigurationFile content =
                SrcMorphConfigurationFileLoader.load(write("srcmorph.yaml", ""), defaultBase(), NO_PROPERTIES);

        assertThat(content.getCommand(), is(nullValue()));
        assertThat(content.getSrcMorph().getBaseDirectory(), is(equalTo(defaultBase())));
    }

    @Test
    public void parse_neitherSubstitutesNorResolves() throws IOException {
        final SrcMorphConfigurationFile content = SrcMorphConfigurationFileLoader.parse(
                "srcMorph:\n  baseDirectory: \"rel\"\n  projectName: \"${x}\"\n", true);

        assertThat(content.getSrcMorph().getBaseDirectory(), is(equalTo(new File("rel"))));
        assertThat(content.getSrcMorph().getProjectName(), is(equalTo("${x}")));
        assertThat(
                SrcMorphConfigurationFileLoader.parse("{\"command\":\"Plan\"}", false)
                        .getCommand(),
                is(equalTo("Plan")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="properties">
    @Test
    public void substitute_aKnownProperty_isReplaced_anUnknownOneStays() {
        final Map<String, String> properties = new HashMap<>();
        properties.put("ai.model", "gpt-oss");
        properties.put("dollar", "a$1\\b");

        assertThat(
                SrcMorphConfigurationFileLoader.substitute("m: ${ai.model}, x: ${nope}, d: ${dollar}", properties),
                is(equalTo("m: gpt-oss, x: ${nope}, d: a$1\\b")));
    }

    @Test
    public void substitute_noProperties_isTheText() {
        assertThat(SrcMorphConfigurationFileLoader.substitute("m: ${ai.model}", NO_PROPERTIES), is("m: ${ai.model}"));
    }

    @Test
    public void load_aPropertyWithBackslashesAndQuotes_needsNoEscaping() throws IOException {
        final Path file = write(
                "srcmorph.yaml",
                "srcMorph:\n  projectName: \"${name}\"\n  subtrees: [\"${tree}\", \"plain\"]\n"
                        + "  aiDefinitions:\n    - key: \"${tree}\"\n      modelPath: m.gguf\n");
        final Map<String, String> properties = new HashMap<>();
        properties.put("name", "C:\\Users\\x \"quoted\"");
        properties.put("tree", "D:\\src");

        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), properties)
                .getSrcMorph();

        assertThat(srcMorph.getProjectName(), is(equalTo("C:\\Users\\x \"quoted\"")));
        assertThat(srcMorph.getSubtrees(), is(equalTo(Arrays.asList("D:\\src", "plain"))));
        assertThat(srcMorph.getAiDefinitions().get(0).getKey(), is(equalTo("D:\\src")));
    }

    @Test
    public void load_propertiesFillTheFile() throws IOException {
        final Path file = write("srcmorph.yaml", "srcMorph:\n  maxFileSizeBytes: ${size}\n");

        final SrcMorphConfigurationFile content =
                SrcMorphConfigurationFileLoader.load(file, defaultBase(), Collections.singletonMap("size", "4096"));

        assertThat(content.getSrcMorph().getMaxFileSizeBytes(), is(4096L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="paths">
    @Test
    public void load_relativeDirectories_resolveAgainstTheFilesDirectory() throws IOException {
        final Path file = write(
                "project/config/srcmorph.yaml",
                "srcMorph:\n  baseDirectory: \"..\"\n  outputDirectory: \"out\"\n"
                        + "  modelDirectory: \"models\"\n  profileDirectory: \"../profiles\"\n"
                        + "  runRecordFile: \"records/run.json\"\n");
        final Path directory = file.getParent().toAbsolutePath();

        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        assertThat(srcMorph.getBaseDirectory(), is(equalTo(directory.getParent().toFile())));
        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(directory.resolve("out").toFile())));
        assertThat(
                srcMorph.getModelDirectory(),
                is(equalTo(directory.resolve("models").toFile())));
        assertThat(
                srcMorph.getProfileDirectory(),
                is(equalTo(directory.getParent().resolve("profiles").toFile())));
        assertThat(
                srcMorph.getRunRecordFile(),
                is(equalTo(directory.resolve("records/run.json").toFile())));
    }

    @Test
    public void load_noBaseAndNoOutput_theDefaultBaseAndItsSrcSiteAi() throws IOException {
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(
                        write("srcmorph.yaml", "srcMorph:\n  force: true\n"), defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        assertThat(srcMorph.getBaseDirectory(), is(equalTo(defaultBase())));
        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(defaultBase().toPath().resolve("src/site/ai").toFile())));
        // unset, the model and profile directories are the file's own, never the working directory
        assertThat(
                srcMorph.getModelDirectory(),
                is(equalTo(tempDir.toAbsolutePath().toFile())));
        assertThat(
                srcMorph.getProfileDirectory(),
                is(equalTo(tempDir.toAbsolutePath().toFile())));
        assertThat(srcMorph.getRunRecordFile(), is(nullValue()));
    }

    @Test
    public void load_aRelativeModelPath_resolvesAgainstTheFilesDirectory() throws IOException {
        // arrange
        final Path file = write(
                "conf/srcmorph.yaml",
                "srcMorph:\n  aiDefinitions:\n    - key: \"m\"\n      modelPath: \"models/m.gguf\"\n");

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();
        final AiModelDefinitionSupport models =
                new AiModelDefinitionSupport(srcMorph.getAiDefinitions(), srcMorph.getModelDirectory());

        // assert
        assertThat(
                models.getConfig("m").getModelPath(),
                is(equalTo(file.getParent()
                        .toAbsolutePath()
                        .resolve("models/m.gguf")
                        .toString())));
    }

    @Test
    public void load_aBaseButNoOutput_theOutputBelowThatBase() throws IOException {
        final Path file = write("srcmorph.yaml", "srcMorph:\n  baseDirectory: \"repo\"\n");

        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(tempDir.toAbsolutePath().resolve("repo/src/site/ai").toFile())));
    }

    @Test
    public void load_keysSetToNothing_countAsUnset() throws IOException {
        final Path file = write("srcmorph.yaml", "srcMorph:\n  baseDirectory:\n  outputDirectory:\n");

        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        assertThat(srcMorph.getBaseDirectory(), is(equalTo(defaultBase())));
        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(defaultBase().toPath().resolve("src/site/ai").toFile())));
    }

    @Test
    public void load_anAbsoluteDirectory_isUsedAsItIs() throws IOException {
        final File elsewhere = tempDir.resolve("elsewhere").toAbsolutePath().toFile();
        final Path file = write(
                "srcmorph.json",
                "{\"srcMorph\":{\"outputDirectory\":\"" + elsewhere.getPath().replace("\\", "/") + "\"}}");

        assertThat(
                SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                        .getSrcMorph()
                        .getOutputDirectory()
                        .toPath(),
                is(equalTo(elsewhere.toPath())));
    }
    // </editor-fold>
}
