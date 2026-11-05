// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Argument validation and JSON/YAML parsing behaviour of {@link Main}, all against the {@code mock}
 * provider (never loads a real model), plus one dedicated ad-hoc test fixture pair (kept private to
 * this module's {@code src/test/resources} — the public, documented {@code examples/} fixture set is
 * a later migration step).
 */
public class MainTest {

    @TempDir
    Path tempDir;

    private static final Path MINIMAL_GENERATE_JSON = Path.of("src/test/resources/test-fixtures/minimal-generate.json");
    private static final Path MINIMAL_GENERATE_YAML = Path.of("src/test/resources/test-fixtures/minimal-generate.yaml");

    private static final String MINIMAL_JSON_STRING = "{\"command\":\"Plan\"}";
    private static final String MINIMAL_YAML_STRING = "command: Plan\n";

    @Test
    public void main_noArgumentGiven_failsWithTheUsage() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> Main.main(new String[0]));

        assertThat(thrown.getMessage(), containsString("Expected one argument, got 0."));
        assertThat(thrown.getMessage(), containsString("usage: java -jar srcmorph.jar"));
    }

    @Test
    public void main_tooManyArgumentsGiven_failsWithTheUsage() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> Main.main(new String[] {"one", "two"}));

        assertThat(thrown.getMessage(), containsString("Expected one argument, got 2."));
    }

    @Test
    public void main_help_printsTheUsageAndRunsNothing() {
        final PrintStream original = System.out;
        final ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(printed, true));
            assertDoesNotThrow(() -> Main.main(new String[] {"--help"}));
            assertDoesNotThrow(() -> Main.main(new String[] {"-h"}));
        } finally {
            System.setOut(original);
        }

        final String usage = new String(printed.toByteArray(), StandardCharsets.UTF_8);
        assertThat(usage, containsString("usage: java -jar srcmorph.jar <configuration.json|.js|.yaml|.yml>"));
        // every command with its summary, twice (once per call)
        for (final CCommand command : CCommand.values()) {
            assertThat(usage, containsString(command.name()));
            assertThat(usage, containsString(command.summary()));
        }
        assertThat(usage.indexOf("usage:") != usage.lastIndexOf("usage:"), is(true));
    }

    @Test
    public void everyCommand_hasASummaryOfItsOwn() {
        for (final CCommand command : CCommand.values()) {
            assertThat(command.name() + " has a summary", command.summary().length() > 20, is(true));
        }
        assertThat(CCommand.Check.summary(), containsString("no model loaded"));
    }

    @Test
    public void usage_listsEveryCommandOnItsOwnLine() {
        final String usage = Main.usage();

        for (final CCommand command : CCommand.values()) {
            assertThat(usage, containsString("  " + command.name() + " "));
        }
        assertThat(
                usage,
                containsString("Relative paths in the configuration resolve against the directory of its file."));
    }

    @Test
    public void fromYaml_unknownCommand_namesTheCommands() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> Main.fromYaml("command: Plann\n"));

        assertThat(thrown.getMessage(), containsString("Unknown command 'Plann'; one of [Plan, Check,"));
    }

    @Test
    public void fromYaml_noCommand_isPlan() throws IOException {
        assertThat(Main.fromYaml("modelFilter: grok\n").command, is(CCommand.Plan));
        assertThat(Main.fromYaml("modelFilter: grok\n").modelFilter, is("grok"));
    }

    @Test
    public void loadConfiguration_relativePaths_resolveAgainstTheFilesDirectory() throws IOException {
        final Path directory = Files.createDirectories(tempDir.resolve("project/config"));
        final Path file = directory.resolve("srcmorph.yaml");
        Files.write(
                file,
                ("srcMorph:\n  outputDirectory: \"out\"\n  profileDirectory: \"../profiles\"\n"
                                + "  modelDirectory: \"models\"\n")
                        .getBytes(StandardCharsets.UTF_8));

        final CConfiguration configuration = Main.loadConfiguration(file);

        // no baseDirectory: the file's directory
        assertThat(
                configuration.srcMorph.getBaseDirectory(),
                is(directory.toAbsolutePath().toFile()));
        assertThat(
                configuration.srcMorph.getOutputDirectory(),
                is(directory.resolve("out").toAbsolutePath().toFile()));
        assertThat(
                configuration.srcMorph.getProfileDirectory(),
                is(tempDir.resolve("project/profiles").toAbsolutePath().toFile()));
        assertThat(
                configuration.srcMorph.getModelDirectory(),
                is(directory.resolve("models").toAbsolutePath().toFile()));
    }

    @Test
    public void loadConfiguration_systemPropertiesFillTheFile() throws IOException {
        final Path file = tempDir.resolve("srcmorph.yaml");
        Files.write(file, "modelFilter: \"${srcmorph.test.filter}\"\n".getBytes(StandardCharsets.UTF_8));
        System.setProperty("srcmorph.test.filter", "granite");
        try {
            assertThat(Main.loadConfiguration(file).modelFilter, is("granite"));
        } finally {
            System.clearProperty("srcmorph.test.filter");
        }
    }

    @Test
    public void main_unknownExtensionPath_throwsIllegalArgumentException() throws IOException {
        final Path configFile = tempDir.resolve("config.txt");
        Files.write(configFile, MINIMAL_JSON_STRING.getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> Main.main(new String[] {configFile.toString()}));
    }

    @Test
    public void loadConfiguration_jsonExtension_parsesFixture() throws IOException {
        final CConfiguration configuration = Main.loadConfiguration(MINIMAL_GENERATE_JSON);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.command, is(equalTo(CCommand.GenerateFileIndex)));
        assertThat(configuration.srcMorph.getGenerationProvider(), is(equalTo("mock")));
    }

    @Test
    public void loadConfiguration_yamlExtension_parsesFixture() throws IOException {
        final CConfiguration configuration = Main.loadConfiguration(MINIMAL_GENERATE_YAML);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.command, is(equalTo(CCommand.GenerateFileIndex)));
        assertThat(configuration.srcMorph.getGenerationProvider(), is(equalTo("mock")));
    }

    @Test
    public void fromJson_validJsonString_returnsExpectedConfiguration() throws IOException {
        final CConfiguration configuration = Main.fromJson(MINIMAL_JSON_STRING);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.command, is(equalTo(CCommand.Plan)));
    }

    @Test
    public void fromYaml_validYamlString_returnsExpectedConfiguration() throws IOException {
        final CConfiguration configuration = Main.fromYaml(MINIMAL_YAML_STRING);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.command, is(equalTo(CCommand.Plan)));
    }

    @Test
    public void fromJson_unknownTopLevelProperty_throwsIOException() {
        assertThrows(IOException.class, () -> Main.fromJson("{\"thisFieldDoesNotExist\":true}"));
    }

    @Test
    public void fromYaml_unknownTopLevelProperty_throwsIOException() {
        assertThrows(IOException.class, () -> Main.fromYaml("thisFieldDoesNotExist: true\n"));
    }

    @Test
    public void configurationToJsonThenFromJson_roundTripsCommand() throws IOException {
        final CConfiguration original = new CConfiguration();
        original.command = CCommand.AggregateProject;

        final String json = Main.configurationToJson(original);
        final CConfiguration roundTripped = Main.fromJson(json);

        assertThat(roundTripped.command, is(equalTo(CCommand.AggregateProject)));
    }

    @Test
    public void configurationToYAMLThenFromYaml_roundTripsCommand() throws IOException {
        final CConfiguration original = new CConfiguration();
        original.command = CCommand.Calibrate;

        final String yaml = Main.configurationToYaml(original);
        final CConfiguration roundTripped = Main.fromYaml(yaml);

        assertThat(roundTripped.command, is(equalTo(CCommand.Calibrate)));
    }

    @Test
    public void configurationEcho_aBaseUrlWithUserInfo_isEchoedMaskedInBothForms() throws Exception {
        // arrange
        final CConfiguration configuration = new CConfiguration();
        final AiModelDefinition model = new AiModelDefinition();
        model.setKey("remote");
        model.setBaseUrl("https://user:secret@example.org/v1");
        configuration.srcMorph.setAiDefinitions(List.of(model));

        // act
        final String echo = Main.configurationEcho(configuration);

        // assert
        assertThat(echo.contains("secret"), is(false));
        assertThat(echo, containsString("\"baseUrl\" : \"https://***@example.org/v1\""));
        assertThat(echo, containsString("baseUrl: \"https://***@example.org/v1\""));
    }

    @Test
    public void logConfigurationTransformation_defaultConfiguration_doesNotThrow() {
        final Main main = new Main(new CConfiguration());

        assertDoesNotThrow(main::logConfigurationEcho);
    }

    @Test
    public void run_planCommandWithNoFieldGenerations_throwsIllegalStateException() {
        final CConfiguration configuration = new CConfiguration();
        configuration.command = CCommand.Plan;
        configuration.srcMorph.setBaseDirectory(tempDir.toFile());
        configuration.srcMorph.setOutputDirectory(tempDir.resolve("out").toFile());
        // No fieldGenerations configured -> GenerateEngine.execute() throws SrcMorphException,
        // which Main.run() wraps as an unchecked IllegalStateException (no System.exit anywhere).
        final Main main = new Main(configuration);

        assertThrows(IllegalStateException.class, main::run);
    }

    // <editor-fold defaultstate="collapsed" desc="extension aliases and the safe default">

    /**
     * The {@code .js} alias is documented in {@code loadConfiguration}'s Javadoc as a JSON extension,
     * but no shipped {@code examples/} fixture uses it, so nothing else can reach that branch. If the
     * {@code ||} were reordered or the constant dropped, half the documented extensions would start
     * failing with "Unknown file ending" and no test would notice.
     *
     * @throws IOException if the fixture cannot be written or read
     */
    @Test
    public void loadConfiguration_jsExtension_parsesAsJson() throws IOException {
        // arrange
        final Path configFile = tempDir.resolve("config.js");
        Files.write(configFile, MINIMAL_JSON_STRING.getBytes(StandardCharsets.UTF_8));

        // act
        final CConfiguration configuration = Main.loadConfiguration(configFile);

        // assert
        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.command, is(equalTo(CCommand.Plan)));
    }

    /**
     * The {@code .yml} alias, for the same reason as {@link #loadConfiguration_jsExtension_parsesAsJson}.
     *
     * @throws IOException if the fixture cannot be written or read
     */
    @Test
    public void loadConfiguration_ymlExtension_parsesAsYaml() throws IOException {
        // arrange
        final Path configFile = tempDir.resolve("config.yml");
        Files.write(configFile, MINIMAL_YAML_STRING.getBytes(StandardCharsets.UTF_8));

        // act
        final CConfiguration configuration = Main.loadConfiguration(configFile);

        // assert
        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.command, is(equalTo(CCommand.Plan)));
    }

    /**
     * Extension matching is case-insensitive by construction ({@code toLowerCase(Locale.ROOT)}); a
     * mutant dropping that call would break an upper-cased path with no other test noticing.
     *
     * @throws IOException if the fixture cannot be written or read
     */
    @Test
    public void loadConfiguration_upperCaseExtension_stillParses() throws IOException {
        // arrange
        final Path configFile = tempDir.resolve("config.JSON");
        Files.write(configFile, MINIMAL_JSON_STRING.getBytes(StandardCharsets.UTF_8));

        // act
        final CConfiguration configuration = Main.loadConfiguration(configFile);

        // assert
        assertThat(configuration, is(notNullValue()));
    }

    /**
     * Pins the documented safety default: a configuration that names no command must plan, never run.
     * {@code Plan} loads no model and writes nothing, so it is the only safe value to fall back to --
     * the contract is documented on {@code CConfiguration.command} but was not asserted anywhere.
     *
     * @throws IOException if the empty document cannot be deserialised
     */
    @Test
    public void fromJson_documentWithoutCommand_defaultsToPlan() throws IOException {
        // act
        final CConfiguration configuration = Main.fromJson("{}");

        // assert
        assertThat(configuration.command, is(equalTo(CCommand.Plan)));
    }

    // </editor-fold>
}
