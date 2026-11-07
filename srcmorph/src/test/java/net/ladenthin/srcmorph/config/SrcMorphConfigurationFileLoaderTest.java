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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        // arrange
        final String yaml = "command: All\nmodelFilter: grok\n"
                + "packageRules:\n  - promptKey: package-body\n    aiDefinitionKey: m\n"
                + "projectRules:\n  - promptKey: project-body\n    aiDefinitionKey: m\n"
                + "srcMorph:\n  generationProvider: mock\n";
        final String json = "{\"command\":\"All\",\"modelFilter\":\"grok\","
                + "\"packageRules\":[{\"promptKey\":\"package-body\",\"aiDefinitionKey\":\"m\"}],"
                + "\"projectRules\":[{\"promptKey\":\"project-body\",\"aiDefinitionKey\":\"m\"}],"
                + "\"srcMorph\":{\"generationProvider\":\"mock\"}}";

        // act, assert
        for (final Path file :
                new Path[] {write("a.yaml", yaml), write("b.YML", yaml), write("c.json", json), write("d.js", json)}) {
            final SrcMorphConfigurationFile content =
                    SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES);

            assertThat(content.getCommand(), is(equalTo("All")));
            assertThat(content.getModelFilter(), is(equalTo("grok")));
            assertThat(content.getPackageRules().get(0).getPromptKey(), is(equalTo("package-body")));
            assertThat(content.getProjectRules().get(0).getPromptKey(), is(equalTo("project-body")));
            assertThat(content.getSrcMorph().getGenerationProvider(), is(equalTo("mock")));
        }
    }

    @Test
    public void load_anUnknownKey_failsNamingIt() throws IOException {
        // arrange
        final Path file = write("srcmorph.yaml", "srcMorph:\n  subtree: [\"src\"]\n");
        final File base = defaultBase();

        // act
        final IOException thrown =
                assertThrows(IOException.class, () -> SrcMorphConfigurationFileLoader.load(file, base, NO_PROPERTIES));

        // assert
        assertThat(thrown.getMessage(), containsString("subtree"));
    }

    @Test
    public void load_anotherExtension_isRefused() throws IOException {
        // arrange
        final Path file = write("srcmorph.txt", "");
        final File base = defaultBase();

        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> SrcMorphConfigurationFileLoader.load(file, base, NO_PROPERTIES));

        // assert
        assertThat(thrown.getMessage(), containsString("A configuration file ends with .yaml, .yml, .json or .js"));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"srcmorph.json", "srcmorph.yaml"})
    public void load_aByteOrderMark_isNotPartOfTheText(final String name) throws IOException {
        // arrange: what Windows PowerShell 5.1's Out-File -Encoding utf8 writes. Only the JSON case guards the
        // loader's own handling of the mark: SnakeYAML skips a leading U+FEFF itself, so the YAML case passed
        // before it too; it stays as the statement that both formats read such a file
        final String text = name.endsWith(".json") ? "{\"srcMorph\": {\"force\": true}}" : "srcMorph:\n  force: true\n";
        final byte[] body = text.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[body.length + 3];
        bytes[0] = (byte) 0xEF;
        bytes[1] = (byte) 0xBB;
        bytes[2] = (byte) 0xBF;
        System.arraycopy(body, 0, bytes, 3, body.length);
        final Path file = Files.write(tempDir.resolve(name), bytes);

        // act
        final SrcMorphConfigurationFile content =
                SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES);

        // assert
        assertThat(content.getSrcMorph().isForce(), is(true));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"srcmorph.json", "srcmorph.yaml"})
    public void load_pruneOrphans_isBound(final String name) throws IOException {
        // arrange
        final String text = name.endsWith(".json")
                ? "{\"srcMorph\": {\"pruneOrphans\": true}}"
                : "srcMorph:\n  pruneOrphans: true\n";
        final Path file = write(name, text);

        // act
        final SrcMorphConfigurationFile content =
                SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES);

        // assert
        assertThat(content.getSrcMorph().isPruneOrphans(), is(true));
    }

    @Test
    public void load_anEmptyFile_isEveryDefault() throws IOException {
        // act
        final SrcMorphConfigurationFile content =
                SrcMorphConfigurationFileLoader.load(write("srcmorph.yaml", ""), defaultBase(), NO_PROPERTIES);

        // assert
        assertThat(content.getCommand(), is(nullValue()));
        assertThat(content.getSrcMorph().getBaseDirectory(), is(equalTo(defaultBase())));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="parse">
    @Test
    public void parse_aReferenceAndARelativePath_neitherSubstitutesNorResolves() throws IOException {
        // act
        final SrcMorphConfigurationFile content = SrcMorphConfigurationFileLoader.parse(
                "srcMorph:\n  baseDirectory: \"rel\"\n  projectName: \"${x}\"\n", true);

        // assert
        assertThat(content.getSrcMorph().getBaseDirectory(), is(equalTo(new File("rel"))));
        assertThat(content.getSrcMorph().getProjectName(), is(equalTo("${x}")));
        assertThat(
                SrcMorphConfigurationFileLoader.parse("{\"command\":\"Plan\"}", false)
                        .getCommand(),
                is(equalTo("Plan")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="substitute">
    @Test
    public void substitute_aKnownAndAnUnknownProperty_theKnownIsReplacedTheUnknownStays() {
        // arrange
        final Map<String, String> properties = new HashMap<>();
        properties.put("ai.model", "gpt-oss");
        properties.put("dollar", "a$1\\b");

        // act, assert
        assertThat(
                SrcMorphConfigurationFileLoader.substitute("m: ${ai.model}, x: ${nope}, d: ${dollar}", properties),
                is(equalTo("m: gpt-oss, x: ${nope}, d: a$1\\b")));
    }

    @Test
    public void substitute_noProperties_isTheText() {
        // act, assert
        assertThat(SrcMorphConfigurationFileLoader.substitute("m: ${ai.model}", NO_PROPERTIES), is("m: ${ai.model}"));
    }

    /** $${name} writes a literal ${name}, also where a property of that name exists. */
    @Test
    public void substitute_anEscapedReference_isTheLiteralReference() {
        // arrange
        final Map<String, String> properties = new HashMap<>();
        properties.put("user.home", "/home/me");

        // act, assert
        assertThat(
                SrcMorphConfigurationFileLoader.substitute("echo $${user.home} in ${user.home}", properties),
                is(equalTo("echo ${user.home} in /home/me")));
        assertThat(
                SrcMorphConfigurationFileLoader.substitute("echo $${HOME} costs $5", NO_PROPERTIES),
                is(equalTo("echo ${HOME} costs $5")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="properties">
    @Test
    public void load_aPropertyWithBackslashesAndQuotes_needsNoEscaping() throws IOException {
        // arrange
        final Path file = write(
                "srcmorph.yaml",
                "srcMorph:\n  projectName: \"${name}\"\n  subtrees: [\"${tree}\", \"plain\"]\n"
                        + "  aiDefinitions:\n    - key: \"${tree}\"\n      modelPath: m.gguf\n");
        final Map<String, String> properties = new HashMap<>();
        properties.put("name", "C:\\Users\\x \"quoted\"");
        properties.put("tree", "D:\\src");

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), properties)
                .getSrcMorph();

        // assert
        assertThat(srcMorph.getProjectName(), is(equalTo("C:\\Users\\x \"quoted\"")));
        assertThat(srcMorph.getSubtrees(), is(equalTo(Arrays.asList("D:\\src", "plain"))));
        assertThat(srcMorph.getAiDefinitions().get(0).getKey(), is(equalTo("D:\\src")));
    }

    @Test
    public void load_aPropertyReference_propertiesFillTheFile() throws IOException {
        // arrange
        final Path file = write("srcmorph.yaml", "srcMorph:\n  maxFileSizeBytes: ${size}\n");

        // act
        final SrcMorphConfigurationFile content =
                SrcMorphConfigurationFileLoader.load(file, defaultBase(), Collections.singletonMap("size", "4096"));

        // assert
        assertThat(content.getSrcMorph().getMaxFileSizeBytes(), is(4096L));
    }

    @Test
    public void load_fileNamesAndTheNewConditionLeaves_bind() throws IOException {
        // arrange
        final Path file = write(
                "srcmorph.yaml",
                "srcMorph:\n  fileNames: [\"Dockerfile\", \"Jenkinsfile\"]\n  rules:\n"
                        + "    - promptKey: file-body-ops\n      condition:\n        or:\n          conditions:\n"
                        + "            - fileNames: [\"Dockerfile.*\"]\n"
                        + "            - contentMatches: \"(?m)^apiVersion:\"\n"
                        + "            - pathGlob: \"**/{k8s,helm}/**\"\n");

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        // assert
        assertThat(srcMorph.getFileNames(), is(Arrays.asList("Dockerfile", "Jenkinsfile")));
        final List<RuleCondition> leaves =
                srcMorph.getRules().get(0).getCondition().getOr().getConditions();
        assertThat(leaves.get(0).getFileNames(), is(Arrays.asList("Dockerfile.*")));
        assertThat(leaves.get(1).getContentMatches(), is("(?m)^apiVersion:"));
        assertThat(leaves.get(2).getPathGlob(), is("**/{k8s,helm}/**"));
    }

    @Test
    public void load_fileInterpretersAndContentHeadChars_bindFromTheFile() throws IOException {
        // arrange
        final Path file =
                write("scripts.yaml", "srcMorph:\n  fileInterpreters: [bash, perl]\n  contentHeadChars: 65536\n");

        // act
        final SrcMorphConfiguration loaded = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        // assert
        assertThat(loaded.getFileInterpreters(), is(Arrays.asList("bash", "perl")));
        assertThat(loaded.getContentHeadChars(), is(65_536));
    }

    @Test
    public void load_regenerateOn_aSwitchTheFileSetsAndTheOthersOn() throws IOException {
        // arrange
        final Path unset = write("unset.yaml", "srcMorph:\n  force: false\n");
        final Path empty = write("empty.yaml", "srcMorph:\n  regenerateOn:\n");
        final Path off = write("off.yaml", "srcMorph:\n  regenerateOn:\n    prompt: false\n    children: false\n");

        // act
        final RegenerateOn unsetOn = SrcMorphConfigurationFileLoader.load(unset, defaultBase(), NO_PROPERTIES)
                .getSrcMorph()
                .getRegenerateOn();
        final RegenerateOn emptyOn = SrcMorphConfigurationFileLoader.load(empty, defaultBase(), NO_PROPERTIES)
                .getSrcMorph()
                .getRegenerateOn();
        final RegenerateOn offOn = SrcMorphConfigurationFileLoader.load(off, defaultBase(), NO_PROPERTIES)
                .getSrcMorph()
                .getRegenerateOn();

        // assert
        assertThat(unsetOn.isPrompt(), is(true));
        assertThat(emptyOn.isPrompt(), is(true));
        assertThat(offOn.isPrompt(), is(false));
        assertThat(offOn.isChildren(), is(false));
        assertThat(offOn.isSource(), is(true));
        assertThat(offOn.isFacts(), is(true));
        assertThat(offOn.isModel(), is(true));
    }

    @Test
    public void load_anUnknownRegenerateOnSwitch_fails() throws IOException {
        // arrange
        final Path file = write("typo.yaml", "srcMorph:\n  regenerateOn:\n    prompts: false\n");
        final File base = defaultBase();

        // act, assert
        assertThrows(IOException.class, () -> SrcMorphConfigurationFileLoader.load(file, base, NO_PROPERTIES));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "baseDirectory: \"${nope}/x\"",
                "outputDirectory: \"${nope}/x\"",
                "modelDirectory: \"${nope}/x\"",
                "profileDirectory: \"${nope}/x\"",
                "runRecordFile: \"${nope}/run.json\"",
                "partsDirectory: \"${nope}/parts\"",
                "subtrees: [\"src\", \"${nope}/x\"]"
            })
    public void load_aPathNamingAPropertyNobodySets_failsNamingIt(final String line) throws IOException {
        // arrange
        // a reference no property replaced would become a directory named ${nope}
        final Path file = write("srcmorph.yaml", "srcMorph:\n  " + line + "\n");
        final String key = line.substring(0, line.indexOf(':'));
        final File base = defaultBase();

        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> SrcMorphConfigurationFileLoader.load(file, base, NO_PROPERTIES));

        // assert
        assertThat(thrown.getMessage(), containsString("srcMorph." + key + " names ${nope}, which no property sets"));
        assertThat(thrown.getMessage(), containsString("-Dnope=..."));
    }

    @Test
    public void load_aPathNamingASetProperty_isThatPath() throws IOException {
        // arrange
        final Path file =
                write("srcmorph.yaml", "srcMorph:\n  outputDirectory: \"${out}/x\"\n  subtrees: [\"${out}\"]\n");

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(
                        file, defaultBase(), Collections.singletonMap("out", "gen"))
                .getSrcMorph();

        // assert
        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(tempDir.resolve("gen/x").toFile())));
        assertThat(srcMorph.getSubtrees(), is(equalTo(Collections.singletonList("gen"))));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="paths">
    @Test
    public void load_relativeDirectories_resolveAgainstTheFilesDirectory() throws IOException {
        // arrange
        final Path file = write(
                "project/config/srcmorph.yaml",
                "srcMorph:\n  baseDirectory: \"..\"\n  outputDirectory: \"out\"\n"
                        + "  modelDirectory: \"models\"\n  profileDirectory: \"../profiles\"\n"
                        + "  runRecordFile: \"records/run.json\"\n  partsDirectory: \"parts\"\n");
        final Path directory = file.getParent().toAbsolutePath();

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        // assert
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
        assertThat(
                srcMorph.getPartsDirectory(),
                is(equalTo(directory.resolve("parts").toFile())));
    }

    @Test
    public void load_noBaseAndNoOutput_theDefaultBaseAndItsSrcSiteAi() throws IOException {
        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(
                        write("srcmorph.yaml", "srcMorph:\n  force: true\n"), defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        // assert
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
        assertThat(srcMorph.getPartsDirectory(), is(nullValue()));
    }

    @Test
    public void load_aSectionOrKeySetToNothing_keepsItsDefault() throws IOException {
        // act
        final SrcMorphConfigurationFile empty =
                SrcMorphConfigurationFileLoader.load(write("empty.yaml", "srcMorph:\n"), defaultBase(), NO_PROPERTIES);
        final SrcMorphConfiguration nothing = SrcMorphConfigurationFileLoader.load(
                        write("nothing.yaml", "srcMorph:\n  generatorVersion:\n  aiVersion: ~\n"),
                        defaultBase(),
                        NO_PROPERTIES)
                .getSrcMorph();

        // assert
        assertThat(empty.getSrcMorph().getBaseDirectory(), is(equalTo(defaultBase())));
        assertThat(nothing.getGeneratorVersion(), is(equalTo(new SrcMorphConfiguration().getGeneratorVersion())));
        assertThat(nothing.getAiVersion(), is(equalTo(new SrcMorphConfiguration().getAiVersion())));
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
        final ModelRegistry models = new ModelRegistry(srcMorph.getAiDefinitions(), srcMorph.getModelDirectory());

        // assert
        assertThat(
                models.definition("m").getModelPath(),
                is(equalTo(file.getParent()
                        .toAbsolutePath()
                        .resolve("models/m.gguf")
                        .toString())));
    }

    @Test
    public void load_aBaseButNoOutput_theOutputBelowThatBase() throws IOException {
        // arrange
        final Path file = write("srcmorph.yaml", "srcMorph:\n  baseDirectory: \"repo\"\n");

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        // assert
        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(tempDir.toAbsolutePath().resolve("repo/src/site/ai").toFile())));
    }

    @Test
    public void load_keysSetToNothing_countAsUnset() throws IOException {
        // arrange
        final Path file = write("srcmorph.yaml", "srcMorph:\n  baseDirectory:\n  outputDirectory:\n");

        // act
        final SrcMorphConfiguration srcMorph = SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                .getSrcMorph();

        // assert
        assertThat(srcMorph.getBaseDirectory(), is(equalTo(defaultBase())));
        assertThat(
                srcMorph.getOutputDirectory(),
                is(equalTo(defaultBase().toPath().resolve("src/site/ai").toFile())));
    }

    @Test
    public void load_anAbsoluteDirectory_isUsedAsItIs() throws IOException {
        // arrange
        final File elsewhere = tempDir.resolve("elsewhere").toAbsolutePath().toFile();
        final Path file = write(
                "srcmorph.json",
                "{\"srcMorph\":{\"outputDirectory\":\"" + elsewhere.getPath().replace("\\", "/") + "\"}}");

        // act, assert
        assertThat(
                SrcMorphConfigurationFileLoader.load(file, defaultBase(), NO_PROPERTIES)
                        .getSrcMorph()
                        .getOutputDirectory()
                        .toPath(),
                is(equalTo(elsewhere.toPath())));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="echo">
    @Test
    public void echo_carriesTheStrategyTokensOnly_soItReadsBack() throws IOException {
        // arrange
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey("p");
        rule.setOnOversize("mapReduce");
        final SrcMorphConfiguration srcMorph = new SrcMorphConfiguration();
        srcMorph.setRules(Collections.singletonList(rule));

        // act
        final String json = SrcMorphConfigurationFileLoader.newJsonMapper().writeValueAsString(srcMorph);

        // assert
        assertThat(json.contains("oversizeStrategy"), is(false));
        assertThat(json.contains("\"onOversize\":\"mapReduce\""), is(true));
        SrcMorphConfigurationFileLoader.newJsonMapper().readValue(json, SrcMorphConfiguration.class);
    }
    // </editor-fold>
}
