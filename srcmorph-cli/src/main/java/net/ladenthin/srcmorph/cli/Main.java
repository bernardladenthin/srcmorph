// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import lombok.ToString;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.AggregatePackagesEngine;
import net.ladenthin.srcmorph.engine.AggregateProjectEngine;
import net.ladenthin.srcmorph.engine.CalibrateEngine;
import net.ladenthin.srcmorph.engine.CalibrationReport;
import net.ladenthin.srcmorph.engine.GenerateEngine;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLI entry point: loads a single JSON/YAML {@link CConfiguration} file (the program's sole
 * argument) and dispatches to the configured {@link CCommand}.
 *
 * <p>Mirrors the BitcoinAddressFinder {@code cli.Main} pattern: extension-dispatched
 * deserialization ({@code .json}/{@code .js} via Jackson's {@code ObjectMapper},
 * {@code .yaml}/{@code .yml} via {@code YAMLMapper}), an echo-back of the parsed configuration for
 * review before anything runs, and no {@link System#exit(int)} anywhere — a failure propagates as
 * an unchecked exception out of {@link #main(String[])} so the JVM's own uncaught-exception
 * handling reports it (a non-zero process exit without bypassing normal shutdown).</p>
 */
@ToString
public class Main {

    /**
     * File extension for JavaScript configuration files; treated identically to {@link #FILE_EXTENSION_JSON}.
     */
    static final String FILE_EXTENSION_JS = ".js";

    /**
     * Standard file extension for JSON configuration files.
     *
     * @see #FILE_EXTENSION_JS
     */
    static final String FILE_EXTENSION_JSON = ".json";

    /**
     * Standard long-form file extension for YAML configuration files.
     *
     * @see #FILE_EXTENSION_YML
     */
    static final String FILE_EXTENSION_YAML = ".yaml";

    /** Short-form file extension for YAML configuration files; treated identically to {@link #FILE_EXTENSION_YAML}. */
    static final String FILE_EXTENSION_YML = ".yml";

    /** SLF4J logger for the CLI entry point. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final CConfiguration configuration;

    /**
     * Creates a new main instance for the given, already-loaded configuration.
     *
     * @param configuration the loaded configuration
     */
    public Main(final CConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Reads the entire contents of {@code path} as a UTF-8 string.
     *
     * @param path the file to read
     * @return the file contents
     * @throws IOException if the file cannot be read
     */
    public static String readString(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    /**
     * Parses a JSON configuration string.
     *
     * @param configurationString the JSON document
     * @return the parsed {@link CConfiguration}
     * @throws IOException if the JSON cannot be deserialised, including an unknown property (the
     *                      mapper is strict — see {@link #newJsonMapper()})
     */
    public static CConfiguration fromJson(final String configurationString) throws IOException {
        final ObjectMapper mapper = newJsonMapper();
        return mapper.readValue(configurationString, CConfiguration.class);
    }

    /**
     * Parses a YAML configuration string.
     *
     * @param configurationString the YAML document
     * @return the parsed {@link CConfiguration}
     * @throws IOException if the YAML cannot be deserialised, including an unknown property (the
     *                      mapper is strict — see {@link #newYamlMapper()})
     */
    public static CConfiguration fromYaml(final String configurationString) throws IOException {
        final YAMLMapper mapper = newYamlMapper();
        return mapper.readValue(configurationString, CConfiguration.class);
    }

    /**
     * Serialises a {@link CConfiguration} as indented JSON.
     *
     * @param configuration the configuration to serialise
     * @return the JSON representation
     * @throws IOException if the configuration cannot be serialised
     */
    public static String configurationToJson(final CConfiguration configuration) throws IOException {
        final ObjectMapper mapper = newJsonMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(configuration);
    }

    /**
     * Serialises a {@link CConfiguration} as YAML.
     *
     * @param configuration the configuration to serialise
     * @return the YAML representation
     * @throws IOException if the configuration cannot be serialised
     */
    public static String configurationToYAML(final CConfiguration configuration) throws IOException {
        final YAMLMapper mapper = newYamlMapper();
        return mapper.writeValueAsString(configuration);
    }

    /**
     * Loads a configuration file from disk, picking the parser based on the file extension.
     *
     * <p>Supported extensions (case-insensitive):
     * <ul>
     *   <li>{@link #FILE_EXTENSION_JSON} / {@link #FILE_EXTENSION_JS} &#x2192; parsed as JSON via {@link #fromJson(String)}</li>
     *   <li>{@link #FILE_EXTENSION_YAML} / {@link #FILE_EXTENSION_YML} &#x2192; parsed as YAML via {@link #fromYaml(String)}</li>
     * </ul>
     *
     * @param configurationPath the configuration file
     * @return the parsed configuration
     * @throws IOException if the file cannot be read or its content cannot be deserialised
     * @throws IllegalArgumentException if {@code configurationPath} does not end with a supported extension
     */
    public static CConfiguration loadConfiguration(final Path configurationPath) throws IOException {
        final String configurationAsString = readString(configurationPath);
        final String lowerPath = configurationPath.toString().toLowerCase(Locale.ROOT);
        if (lowerPath.endsWith(FILE_EXTENSION_JS) || lowerPath.endsWith(FILE_EXTENSION_JSON)) {
            return fromJson(configurationAsString);
        } else if (lowerPath.endsWith(FILE_EXTENSION_YAML) || lowerPath.endsWith(FILE_EXTENSION_YML)) {
            return fromYaml(configurationAsString);
        } else {
            throw new IllegalArgumentException("Unknown file ending for: " + configurationPath);
        }
    }

    /**
     * Java entry point. Loads the configuration file passed as the first argument, logs the parsed
     * configuration back for review, and runs the configured command.
     *
     * @param args the program arguments; expects a single path to a JSON or YAML configuration file
     * @throws IOException if the configuration file cannot be read or deserialised
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            LOGGER.error("Invalid arguments. Pass path to configuration as first argument.");
            return;
        }
        final Path configurationPath = Paths.get(args[0]);
        final CConfiguration configuration = loadConfiguration(configurationPath);
        final Main main = new Main(configuration);
        main.logConfigurationTransformation();
        main.run();
    }

    /**
     * Logs the JSON and YAML representations of the loaded configuration for review, so a user can
     * confirm exactly what was parsed before any model loads or file is written.
     *
     * @throws IOException if the configuration cannot be serialised
     */
    public void logConfigurationTransformation() throws IOException {
        final String json = configurationToJson(configuration);
        final String yaml = configurationToYAML(configuration);
        LOGGER.info("Please review the transformed configuration to ensure it aligns with your expectations "
                + "and requirements before proceeding.:\n"
                + "########## BEGIN transformed JSON configuration ##########\n"
                + json
                + "\n"
                + "########## END   transformed JSON configuration ##########\n"
                + "\n"
                + "########## BEGIN transformed YAML configuration ##########\n"
                + yaml
                + "\n"
                + "########## END   transformed YAML configuration ##########\n");
    }

    /**
     * Dispatches to the engine(s) selected by {@link CConfiguration#command} and runs them.
     *
     * <p>No {@link System#exit(int)} is called anywhere: a failure is wrapped as an unchecked
     * {@link IllegalStateException} and rethrown, so it propagates out of {@link #main(String[])}
     * as an ordinary uncaught exception.</p>
     */
    public void run() {
        LOGGER.info(configuration.command.name());
        try {
            switch (configuration.command) {
                case Plan: {
                    new GenerateEngine(copyWithPlanOnlyForced(configuration.srcMorph)).execute();
                    break;
                }
                case GenerateFileIndex: {
                    new GenerateEngine(configuration.srcMorph).execute();
                    break;
                }
                case AggregatePackages: {
                    new AggregatePackagesEngine(configuration.srcMorph).execute();
                    break;
                }
                case AggregateProject: {
                    new AggregateProjectEngine(configuration.srcMorph).execute();
                    break;
                }
                case All: {
                    new GenerateEngine(configuration.srcMorph).execute();
                    new AggregatePackagesEngine(configuration.srcMorph).execute();
                    new AggregateProjectEngine(configuration.srcMorph).execute();
                    break;
                }
                case Calibrate: {
                    final CalibrationReport report = new CalibrateEngine(configuration.srcMorph).execute();
                    // Deliberate System.out, not SLF4J: this is a paste-ready <calibration> XML
                    // snippet meant for the user to copy into their POM's <aiDefinition>, not a log
                    // line, so it must not carry a logger's timestamp/level prefix.
                    System.out.println(report.renderXml()); // NOPMD - intentional stdout, see above
                    break;
                }
                default:
                    throw new UnsupportedOperationException(
                            "Command: " + configuration.command.name() + " currently not supported.");
            }
            LOGGER.info("Main#run end.");
        } catch (final SrcMorphException | IOException e) {
            LOGGER.error("Fatal error during Main.run.", e);
            throw new IllegalStateException(
                    "Main.run() failed on thread " + Thread.currentThread().getName(), e);
        }
    }

    /**
     * Builds a deep copy of {@code original} with {@link SrcMorphConfiguration#setPlanOnly(boolean)}
     * forced to {@code true}, regardless of what the configuration file said. Used by the
     * {@link CCommand#Plan} command so it never accidentally runs a real (model-loading) generation
     * even if the file's own {@code planOnly} was left {@code false} — and never mutates the shared
     * {@link CConfiguration#srcMorph} instance the caller already logged.
     *
     * <p>The copy is a round-trip through a JSON mapper, which keeps this in lockstep with every
     * field {@link SrcMorphConfiguration} gains in the future with no manual field-by-field copy to
     * maintain. Deliberately uses a <em>lenient</em> mapper (unlike {@link #newJsonMapper()}, which
     * loads user-authored config files and must fail fast on a typo): the source object also carries
     * derived, read-only, setter-less getters that a strict mapper's serialize-then-deserialize
     * round-trip would otherwise reject as "unknown properties" (e.g.
     * {@code AiFieldGenerationConfig#getOversizeStrategy()}, computed from the raw
     * {@code onOversize} string field). This copy step never touches user input — {@code original}
     * was already accepted by the strict mapper when the configuration file was first loaded — so
     * leniency here does not weaken the config-file-typo-fails-fast contract.</p>
     *
     * @param original the configuration to copy
     * @return a deep copy of {@code original} with {@code planOnly} forced {@code true}
     */
    private static SrcMorphConfiguration copyWithPlanOnlyForced(final SrcMorphConfiguration original) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            final SrcMorphConfiguration copy =
                    mapper.readValue(mapper.writeValueAsString(original), SrcMorphConfiguration.class);
            copy.setPlanOnly(true);
            return copy;
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to copy the configuration for the Plan command.", e);
        }
    }

    /**
     * Creates the JSON mapper used for both the CLI's own {@link CConfiguration} parsing and the
     * internal {@link #copyWithPlanOnlyForced(SrcMorphConfiguration)} round-trip.
     *
     * @return a new {@link ObjectMapper} with {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
     *         explicitly enabled (already Jackson's own default; enabled explicitly here so the strict
     *         intent — a typo in the config file fails fast rather than silently no-op'ing, mirroring
     *         what plexus does for the Maven XML side — is documented in code, not left implicit)
     */
    private static ObjectMapper newJsonMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    /**
     * Creates the YAML mapper used for {@link CConfiguration} parsing.
     *
     * @return a new {@link YAMLMapper} with {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
     *         explicitly enabled (see {@link #newJsonMapper()} for the rationale)
     */
    private static YAMLMapper newYamlMapper() {
        final YAMLMapper mapper = new YAMLMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
