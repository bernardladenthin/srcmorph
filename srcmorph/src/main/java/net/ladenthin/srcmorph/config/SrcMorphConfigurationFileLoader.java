// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ladenthin.srcmorph.support.SourceLines;
import org.jspecify.annotations.Nullable;

/**
 * Reads a srcmorph configuration file, the same way for the CLI and the Maven plugin.
 *
 * <ol>
 *   <li>The extension picks the parser: {@code .json} and {@code .js} JSON, {@code .yaml} and
 *       {@code .yml} YAML, in any case.</li>
 *   <li>A {@code ${name}} in a value is replaced by the property {@code name} when the caller knows one
 *       (the plugin: the Maven user, project and system properties, so {@code -Dai.gpuLayers=12}
 *       reaches the file); an unknown one stays as it is, except in a directory, the run record file or a
 *       subtree, where it fails the load instead of naming a directory {@code ${name}}. It is replaced
 *       after parsing, in the parsed
 *       values, so a value with backslashes or quotes (a Windows path) needs no escaping, and a number
 *       written as {@code ${name}} binds as the number it becomes. {@code $${name}} writes a literal
 *       {@code ${name}} that no property replaces (in a path it still fails the load).</li>
 *   <li>The values bind strictly: an unknown key fails, naming it.</li>
 *   <li>Relative directories resolve against the directory of the file, so the file names the same
 *       tree whatever directory it is run from: {@code baseDirectory} (unset, the caller's default),
 *       {@code outputDirectory} (unset, {@value SrcMorphConfiguration#DEFAULT_OUTPUT_DIRECTORY} below the
 *       base directory), {@code modelDirectory} and {@code profileDirectory} (unset, the file's
 *       directory, so a model's {@code modelPath}, a prompt's {@code templateFile} and a fact
 *       definition's {@code factsFile} resolve against it too), {@code runRecordFile} and
 *       {@code partsDirectory}. A path with a
 *       root of its own (absolute, or on Windows root- or drive-relative) is used as it is.</li>
 * </ol>
 */
public final class SrcMorphConfigurationFileLoader {

    /** Extension of a JavaScript-named JSON file, read as JSON. */
    public static final String FILE_EXTENSION_JS = ".js";

    /** Extension of a JSON file. */
    public static final String FILE_EXTENSION_JSON = ".json";

    /** Extension of a YAML file. */
    public static final String FILE_EXTENSION_YAML = ".yaml";

    /** Short extension of a YAML file. */
    public static final String FILE_EXTENSION_YML = ".yml";

    /** The key of the run configuration in the file. */
    private static final String SRC_MORPH_KEY = "srcMorph";

    /** The key of the base directory in the run configuration. */
    private static final String BASE_DIRECTORY_KEY = "baseDirectory";

    /** The key of the output directory in the run configuration. */
    private static final String OUTPUT_DIRECTORY_KEY = "outputDirectory";

    /** The key of the model directory in the run configuration. */
    private static final String MODEL_DIRECTORY_KEY = "modelDirectory";

    /** The key of the profile directory in the run configuration. */
    private static final String PROFILE_DIRECTORY_KEY = "profileDirectory";

    /** The key of the run record file in the run configuration. */
    private static final String RUN_RECORD_FILE_KEY = "runRecordFile";

    /** The key of the directory the map-reduce answers are kept in. */
    private static final String PARTS_DIRECTORY_KEY = "partsDirectory";

    /** The key of the subtrees in the run configuration. */
    private static final String SUBTREES_KEY = "subtrees";

    /** A property reference: {@code ${name}}. */
    private static final Pattern PROPERTY_REFERENCE = Pattern.compile("\\$\\{([^}]+)}");

    /**
     * A property reference, {@code ${name}}, or an escaped one, {@code $${name}}, which writes a literal
     * {@code ${name}}; group 1 is the name.
     */
    private static final Pattern ESCAPABLE_REFERENCE = Pattern.compile("\\$?\\$\\{([^}]+)}");

    /** How an escaped reference starts: a second dollar sign in front of the reference. */
    private static final String ESCAPED_OPENER = "$${";

    /** Utility class; not instantiable. */
    private SrcMorphConfigurationFileLoader() {
        // no-op
    }

    /**
     * Returns the strict JSON mapper the configuration is read and echoed with.
     *
     * @return a new mapper that fails on an unknown key and reads a key set to nothing as unset
     */
    public static ObjectMapper newJsonMapper() {
        return strict(new ObjectMapper());
    }

    /**
     * Returns the strict YAML mapper the configuration is read and echoed with.
     *
     * @return a new mapper that fails on an unknown key and reads a key set to nothing as unset
     */
    public static YAMLMapper newYamlMapper() {
        return strict(new YAMLMapper());
    }

    /**
     * Makes a mapper strict: an unknown key fails, and a key set to nothing ({@code srcMorph:} with no
     * value, {@code null}) keeps the field's default instead of putting {@code null} into it.
     *
     * @param mapper the mapper
     * @param <M>    the mapper type
     * @return the same mapper
     */
    private static <M extends ObjectMapper> M strict(final M mapper) {
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // a key set to nothing keeps its default; an empty list item ("- " in YAML) is refused with the list's
        // name instead of reaching the run as a null rule or path
        mapper.setDefaultSetterInfo(JsonSetter.Value.construct(Nulls.SKIP, Nulls.FAIL));
        return mapper;
    }

    /**
     * Reads a configuration file (steps 1 to 3 of the class description).
     *
     * @param file                 the configuration file
     * @param defaultBaseDirectory the base directory when the file sets none (the CLI: the file's
     *                             directory; the plugin: the project's base directory)
     * @param properties           the properties a {@code ${name}} is replaced by; empty for none
     * @return the content, its paths resolved
     * @throws IOException              if the file cannot be read, or its content does not bind (an
     *                                  unknown key, a wrong type), naming the place
     * @throws IllegalArgumentException if the file's extension is none of the four, or a path names a
     *                                  {@code ${name}} that no property sets
     */
    public static SrcMorphConfigurationFile load(
            final Path file, final File defaultBaseDirectory, final Map<String, String> properties) throws IOException {
        final ObjectMapper mapper = mapperFor(file);
        // a byte order mark (Windows PowerShell 5.1 writes one) is not part of the text
        final JsonNode parsed = mapper.readTree(SourceLines.decode(Files.readAllBytes(file)));
        final JsonNode tree = parsed == null ? null : substituted(parsed, properties);
        // an empty file is a configuration with every default
        final SrcMorphConfigurationFile bound =
                tree == null ? null : mapper.treeToValue(tree, SrcMorphConfigurationFile.class);
        final SrcMorphConfigurationFile content = bound != null ? bound : new SrcMorphConfigurationFile();
        requireReplaced(content.getSrcMorph());
        final Path parent = file.toAbsolutePath().normalize().getParent();
        final File directory = parent != null ? parent.toFile() : defaultBaseDirectory;
        final JsonNode srcMorph = tree == null ? null : tree.get(SRC_MORPH_KEY);
        resolve(
                content.getSrcMorph(),
                directory,
                defaultBaseDirectory,
                srcMorph != null && srcMorph.hasNonNull(BASE_DIRECTORY_KEY),
                srcMorph != null && srcMorph.hasNonNull(OUTPUT_DIRECTORY_KEY));
        return content;
    }

    /**
     * Parses a configuration text without a file: no property is replaced and no path is resolved.
     *
     * @param text the JSON or YAML text
     * @param yaml whether it is YAML
     * @return the content
     * @throws IOException if the text does not bind
     */
    public static SrcMorphConfigurationFile parse(final String text, final boolean yaml) throws IOException {
        return (yaml ? newYamlMapper() : newJsonMapper()).readValue(text, SrcMorphConfigurationFile.class);
    }

    /**
     * Returns the tree with every {@code ${name}} of its text values replaced ({@link #substitute}).
     *
     * @param node       the parsed tree
     * @param properties the properties
     * @return the tree, changed in place
     */
    private static JsonNode substituted(final JsonNode node, final Map<String, String> properties) {
        if (node.isTextual()) {
            return new TextNode(substitute(node.asText(), properties));
        }
        if (node instanceof ObjectNode) {
            final ObjectNode object = (ObjectNode) node;
            final List<String> names = new ArrayList<>();
            object.fieldNames().forEachRemaining(names::add);
            for (final String name : names) {
                final JsonNode value = object.get(name);
                if (value != null) {
                    object.set(name, substituted(value, properties));
                }
            }
        } else if (node instanceof ArrayNode) {
            final ArrayNode array = (ArrayNode) node;
            for (int i = 0; i < array.size(); i++) {
                array.set(i, substituted(array.get(i), properties));
            }
        }
        return node;
    }

    /**
     * Replaces every {@code ${name}} whose {@code name} is a known property by its value, and every
     * escaped {@code $${name}} by the literal {@code ${name}} (an inline prompt that shows a shell
     * variable, say), whether a property of that name exists or not.
     *
     * @param text       the text
     * @param properties the properties
     * @return the text with the known references replaced and the escaped ones unescaped
     */
    static String substitute(final String text, final Map<String, String> properties) {
        final Matcher matcher = ESCAPABLE_REFERENCE.matcher(text);
        final StringBuffer result = new StringBuffer(text.length());
        while (matcher.find()) {
            final boolean escaped = matcher.group().startsWith(ESCAPED_OPENER);
            final String reference = escaped ? matcher.group().substring(1) : matcher.group();
            final String name = matcher.group(1);
            final String value = escaped || name == null ? null : properties.get(name);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value : reference));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Fails when a path of the run configuration still holds a {@code ${name}} that no property replaced:
     * the run would create or read a directory of that name.
     *
     * @param srcMorph the run configuration, its properties replaced
     * @throws IllegalArgumentException naming the key and the reference
     */
    static void requireReplaced(final SrcMorphConfiguration srcMorph) {
        requireReplaced(BASE_DIRECTORY_KEY, srcMorph.getBaseDirectory());
        requireReplaced(OUTPUT_DIRECTORY_KEY, srcMorph.getOutputDirectory());
        requireReplaced(MODEL_DIRECTORY_KEY, srcMorph.getModelDirectory());
        requireReplaced(PROFILE_DIRECTORY_KEY, srcMorph.getProfileDirectory());
        requireReplaced(RUN_RECORD_FILE_KEY, srcMorph.getRunRecordFile());
        requireReplaced(PARTS_DIRECTORY_KEY, srcMorph.getPartsDirectory());
        final List<String> subtrees = srcMorph.getSubtrees();
        if (subtrees != null) {
            for (final String subtree : subtrees) {
                requireReplaced(SUBTREES_KEY, subtree);
            }
        }
    }

    private static void requireReplaced(final String key, final @Nullable Object value) {
        if (value == null) {
            return;
        }
        final Matcher matcher = PROPERTY_REFERENCE.matcher(value.toString());
        if (matcher.find()) {
            throw new IllegalArgumentException(SRC_MORPH_KEY + "." + key + " names " + matcher.group()
                    + ", which no property sets (" + value + "); set it (-D" + matcher.group(1)
                    + "=...) or write the path");
        }
    }

    private static ObjectMapper mapperFor(final Path file) {
        final String name = file.toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(FILE_EXTENSION_JSON) || name.endsWith(FILE_EXTENSION_JS)) {
            return newJsonMapper();
        }
        if (name.endsWith(FILE_EXTENSION_YAML) || name.endsWith(FILE_EXTENSION_YML)) {
            return newYamlMapper();
        }
        throw new IllegalArgumentException("A configuration file ends with " + FILE_EXTENSION_YAML + ", "
                + FILE_EXTENSION_YML + ", " + FILE_EXTENSION_JSON + " or " + FILE_EXTENSION_JS + ": " + file);
    }

    /**
     * Resolves the directories of a run configuration (step 3 of the class description).
     *
     * @param srcMorph             the run configuration, changed in place
     * @param directory            the directory of the configuration file
     * @param defaultBaseDirectory the base directory when none is set
     * @param baseSet              whether the file sets {@code baseDirectory}
     * @param outputSet            whether the file sets {@code outputDirectory}
     */
    static void resolve(
            final SrcMorphConfiguration srcMorph,
            final File directory,
            final File defaultBaseDirectory,
            final boolean baseSet,
            final boolean outputSet) {
        // a key set to nothing counts as unset
        final File configuredBase = srcMorph.getBaseDirectory();
        final File base =
                baseSet && configuredBase != null ? resolved(directory, configuredBase) : defaultBaseDirectory;
        srcMorph.setBaseDirectory(base);
        final File configuredOutput = srcMorph.getOutputDirectory();
        srcMorph.setOutputDirectory(
                outputSet && configuredOutput != null
                        ? resolved(directory, configuredOutput)
                        : resolved(base, new File(SrcMorphConfiguration.DEFAULT_OUTPUT_DIRECTORY)));
        // unset, they are the file's directory: a relative modelPath, templateFile or factsFile resolves
        // through them, so against the file's directory as well, never against the working directory
        final File modelDirectory = srcMorph.getModelDirectory();
        srcMorph.setModelDirectory(modelDirectory != null ? resolved(directory, modelDirectory) : directory);
        final File profileDirectory = srcMorph.getProfileDirectory();
        srcMorph.setProfileDirectory(profileDirectory != null ? resolved(directory, profileDirectory) : directory);
        final File runRecordFile = srcMorph.getRunRecordFile();
        if (runRecordFile != null) {
            srcMorph.setRunRecordFile(resolved(directory, runRecordFile));
        }
        final File partsDirectory = srcMorph.getPartsDirectory();
        if (partsDirectory != null) {
            srcMorph.setPartsDirectory(resolved(directory, partsDirectory));
        }
    }

    private static File resolved(final File directory, final File file) {
        final Path path = file.toPath();
        return path.getRoot() != null
                ? file
                : directory.toPath().resolve(path).normalize().toFile();
    }
}
