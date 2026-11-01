// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFile;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFileLoader;
import net.ladenthin.srcmorph.engine.AggregatePackagesEngine;
import net.ladenthin.srcmorph.engine.AggregateProjectEngine;
import net.ladenthin.srcmorph.engine.CalibrateEngine;
import net.ladenthin.srcmorph.engine.CalibrationReport;
import net.ladenthin.srcmorph.engine.GenerateEngine;
import net.ladenthin.srcmorph.engine.ListModelsEngine;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.jspecify.annotations.Nullable;
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

    /** The argument that prints the usage. */
    static final String HELP_ARGUMENT = "--help";

    /** The short form of {@link #HELP_ARGUMENT}. */
    static final String HELP_ARGUMENT_SHORT = "-h";

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
     *                      mapper is strict — see {@link SrcMorphConfigurationFileLoader#newJsonMapper()})
     */
    public static CConfiguration fromJson(final String configurationString) throws IOException {
        return fromFile(SrcMorphConfigurationFileLoader.parse(configurationString, false));
    }

    /**
     * Parses a YAML configuration string.
     *
     * @param configurationString the YAML document
     * @return the parsed {@link CConfiguration}
     * @throws IOException if the YAML cannot be deserialised, including an unknown property (the
     *                      mapper is strict — see {@link SrcMorphConfigurationFileLoader#newYamlMapper()})
     */
    public static CConfiguration fromYaml(final String configurationString) throws IOException {
        return fromFile(SrcMorphConfigurationFileLoader.parse(configurationString, true));
    }

    /**
     * Converts the content of a configuration file into the CLI's configuration: its {@code command}
     * (unset, {@link CCommand#Plan}), {@code modelFilter}, {@code srcMorph} and the two phase rules.
     *
     * @param file the content
     * @return the CLI's configuration
     * @throws IllegalArgumentException if the command is none of {@link CCommand}, naming them
     */
    static CConfiguration fromFile(final SrcMorphConfigurationFile file) {
        final CConfiguration configuration = new CConfiguration();
        final String command = file.getCommand();
        if (command != null) {
            try {
                configuration.command = CCommand.valueOf(command);
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Unknown command '" + command + "'; one of " + Arrays.toString(CCommand.values()), e);
            }
        }
        configuration.modelFilter = file.getModelFilter();
        configuration.srcMorph = file.getSrcMorph();
        configuration.packageFieldGenerations = file.getPackageFieldGenerations();
        configuration.projectFieldGenerations = file.getProjectFieldGenerations();
        return configuration;
    }

    /**
     * Serialises a {@link CConfiguration} as indented JSON.
     *
     * @param configuration the configuration to serialise
     * @return the JSON representation
     * @throws IOException if the configuration cannot be serialised
     */
    public static String configurationToJson(final CConfiguration configuration) throws IOException {
        final ObjectMapper mapper = SrcMorphConfigurationFileLoader.newJsonMapper();
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
        final YAMLMapper mapper = SrcMorphConfigurationFileLoader.newYamlMapper();
        return mapper.writeValueAsString(configuration);
    }

    /**
     * Loads a configuration file the way the Maven plugin does ({@link SrcMorphConfigurationFileLoader}):
     * {@code ${name}} filled from the Java system properties, the parser picked by the extension
     * ({@code .json}, {@code .js}, {@code .yaml}, {@code .yml}), unknown keys refused, and relative
     * directories resolved against the directory of the file, which is also the base directory when
     * the file sets none.
     *
     * @param configurationPath the configuration file
     * @return the parsed configuration
     * @throws IOException if the file cannot be read or its content cannot be deserialised
     * @throws IllegalArgumentException if {@code configurationPath} does not end with a supported extension
     */
    public static CConfiguration loadConfiguration(final Path configurationPath) throws IOException {
        final Path parent = configurationPath.toAbsolutePath().normalize().getParent();
        final File directory = parent != null ? parent.toFile() : new File(".");
        return fromFile(SrcMorphConfigurationFileLoader.load(configurationPath, directory, systemProperties()));
    }

    private static Map<String, String> systemProperties() {
        final Map<String, String> properties = new HashMap<>();
        for (final String name : System.getProperties().stringPropertyNames()) {
            final String value = System.getProperty(name);
            if (value != null) {
                properties.put(name, value);
            }
        }
        return properties;
    }

    /**
     * Returns the usage {@code --help} prints and a wrong command line fails with: the two calls, and
     * every {@link CCommand} with its summary.
     *
     * @return the usage text
     */
    static String usage() {
        final StringBuilder usage = new StringBuilder()
                .append("usage: java -jar srcmorph.jar <configuration.json|.js|.yaml|.yml>\n")
                .append("       java -jar srcmorph.jar ")
                .append(HELP_ARGUMENT)
                .append("\n\nThe configuration's \"command\" selects what runs:\n");
        for (final CCommand command : CCommand.values()) {
            usage.append(String.format(Locale.ROOT, "  %-18s %s%n", command.name(), command.summary()));
        }
        return usage.append("Relative paths in the configuration resolve against the directory of its file.\n")
                .toString();
    }

    /**
     * Java entry point. Loads the configuration file passed as the first argument, logs the parsed
     * configuration back for review, and runs the configured command.
     *
     * @param args the program arguments: a single path to a JSON or YAML configuration file, or
     *             {@code --help} / {@code -h} for the usage
     * @throws IOException              if the configuration file cannot be read or deserialised
     * @throws IllegalArgumentException with the usage, unless there is exactly one argument
     */
    public static void main(final String[] args) throws IOException {
        final boolean help = args.length == 1 && (HELP_ARGUMENT.equals(args[0]) || HELP_ARGUMENT_SHORT.equals(args[0]));
        if (help || args.length != 1) {
            final String usage = usage();
            if (help) {
                // Deliberate System.out, as for Calibrate: the usage is for the reader, not a log line.
                System.out.print(usage); // NOPMD - intentional stdout
                return;
            }
            throw new IllegalArgumentException("Expected one argument, got " + args.length + ".\n" + usage);
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
                case Check: {
                    new GenerateEngine(configuration.srcMorph).check();
                    // the package phase only when configured: GenerateFileIndex alone needs no package rule
                    if (configuration.packageFieldGenerations != null) {
                        new AggregatePackagesEngine(forPhase(configuration.packageFieldGenerations)).check();
                    }
                    new AggregateProjectEngine(forPhase(configuration.projectFieldGenerations)).check();
                    break;
                }
                case GenerateFileIndex: {
                    new GenerateEngine(configuration.srcMorph).execute();
                    break;
                }
                case AggregatePackages: {
                    new AggregatePackagesEngine(forPhase(configuration.packageFieldGenerations)).execute();
                    break;
                }
                case AggregateProject: {
                    new AggregateProjectEngine(forPhase(configuration.projectFieldGenerations)).execute();
                    break;
                }
                case All: {
                    new GenerateEngine(configuration.srcMorph).execute();
                    new AggregatePackagesEngine(forPhase(configuration.packageFieldGenerations)).execute();
                    new AggregateProjectEngine(forPhase(configuration.projectFieldGenerations)).execute();
                    break;
                }
                case Calibrate: {
                    final CalibrationReport report = new CalibrateEngine(configuration.srcMorph).execute();
                    // Deliberate System.out, not SLF4J: this is a paste-ready YAML calibration: block
                    // for the model's entry under aiDefinitions of the configuration file, not a log
                    // line, so it must not carry a logger's timestamp/level prefix.
                    System.out.println(report.renderPasteBlock()); // NOPMD - intentional stdout, see above
                    break;
                }
                case ListModels: {
                    // stdout for the same reason as Calibrate: a paste-ready aiDefinitions block.
                    System.out.print( // NOPMD - intentional stdout, see Calibrate
                            new ListModelsEngine(configuration.srcMorph, configuration.modelFilter)
                                    .execute()
                                    .render());
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
     * The configuration one aggregation phase runs with: a copy of {@link CConfiguration#srcMorph} whose
     * {@code fieldGenerations} are the phase's own rule, as a Maven plugin execution overrides them. The
     * file rules of {@code srcMorph} never reach a phase: the package phase takes exactly one rule and
     * fails without {@link CConfiguration#packageFieldGenerations}, the project phase writes its index
     * without an overview when {@link CConfiguration#projectFieldGenerations} is unset. The shared
     * configuration is never modified, so the file phase of {@link CCommand#All} keeps its own rules.
     *
     * @param phaseFieldGenerations the phase's rules ({@link CConfiguration#packageFieldGenerations}
     *                              or {@link CConfiguration#projectFieldGenerations}), or {@code null}
     * @return the configuration to hand the phase's engine
     */
    private SrcMorphConfiguration forPhase(final @Nullable List<AiFieldGenerationConfig> phaseFieldGenerations) {
        final SrcMorphConfiguration copy = deepCopy(configuration.srcMorph);
        copy.setFieldGenerations(
                phaseFieldGenerations == null
                        ? Collections.<AiFieldGenerationConfig>emptyList()
                        : phaseFieldGenerations);
        return copy;
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
     * maintain. Deliberately uses a <em>lenient</em> mapper (unlike {@link SrcMorphConfigurationFileLoader#newJsonMapper()}, which
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
        final SrcMorphConfiguration copy = deepCopy(original);
        copy.setPlanOnly(true);
        return copy;
    }

    /**
     * Deep-copies a configuration by a round trip through a lenient JSON mapper, see
     * {@link #copyWithPlanOnlyForced(SrcMorphConfiguration)} for why lenient.
     *
     * @param original the configuration to copy
     * @return an independent copy of {@code original}
     */
    private static SrcMorphConfiguration deepCopy(final SrcMorphConfiguration original) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return mapper.readValue(mapper.writeValueAsString(original), SrcMorphConfiguration.class);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to copy the configuration.", e);
        }
    }
}
