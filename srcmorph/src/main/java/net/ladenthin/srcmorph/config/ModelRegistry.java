// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import net.ladenthin.srcmorph.support.SourcePaths;
import org.jspecify.annotations.Nullable;

/**
 * Resolves {@link ModelDefinition} entries by their key, each a copy with its model path resolved.
 *
 * <p>Built from the {@code aiDefinitions} list of the configuration file, this
 * support class acts as a lookup table that holds a copy of each {@link ModelDefinition}, its
 * relative {@code modelPath} resolved against the model directory, so the caller's beans stay as
 * they are. Routing rules and engines
 * reference definitions by their string key rather than embedding the full parameter
 * set inline.</p>
 *
 * <p>Every entry in the supplied list must have a non-null {@code key}; a null key
 * throws {@link IllegalArgumentException} naming the list index and dumping the offending
 * entry. This is the contract enforcement boundary that makes a misconfigured
 * {@code aiDefinitions} list fail before the run starts rather than silently
 * dropping the entry and surfacing later as a "Missing AI model definition" failure
 * deeper in the run. The engines wrap that {@link IllegalArgumentException} in
 * {@code net.ladenthin.srcmorph.engine.SrcMorphException}, so the CLI and the plugin report
 * it as a configuration error rather than a bug.</p>
 *
 * <p>Lookups for missing keys throw {@link IllegalArgumentException} so that
 * configuration errors are detected eagerly at runtime.</p>
 *
 * @see ModelDefinition
 * @see RoutingRule
 */
@ToString
public final class ModelRegistry {

    /**
     * Error message prefix used when a requested key is not found in the definition map.
     * The missing key is appended after this prefix to produce the full exception message.
     */
    private static final String MISSING_DEFINITION_MESSAGE_PREFIX = "Missing AI model definition for key: ";

    private final Map<String, ModelDefinition> definitions;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Builds a new {@code ModelRegistry} from the supplied definitions list.
     *
     * <p>Every entry must have a non-null {@code key}; a null key throws
     * {@link IllegalArgumentException} naming the list index and the offending entry.
     * See the class Javadoc for the full contract.</p>
     *
     * @param definitions list of AI model definitions; may be {@code null} or empty
     *                    (treated as no definitions); individual entries must be
     *                    well-formed
     * @throws IllegalArgumentException if any entry has a {@code null} or a duplicate {@code key}
     */
    public ModelRegistry(final @Nullable List<ModelDefinition> definitions) {
        this(definitions, null);
    }

    /**
     * Builds a new {@code ModelRegistry} whose relative {@code modelPath} entries are
     * resolved against {@code modelDirectory} (see {@link #resolveModelPath(File, String)}).
     *
     * @param definitions    list of AI model definitions; may be {@code null} or empty
     * @param modelDirectory directory relative model paths are resolved against, or {@code null} to
     *                       keep them relative to the working directory
     * @throws IllegalArgumentException if any entry has a {@code null} or a duplicate {@code key}
     */
    public ModelRegistry(final @Nullable List<ModelDefinition> definitions, final @Nullable File modelDirectory) {
        if (definitions == null) {
            this.definitions = new HashMap<>(compatibilityHelper.hashMapCapacityFor(0));
            return;
        }
        final int count = definitions.size();
        // Presize so the loop's put() calls never trigger a rehash
        // (fb-contrib PSC_PRESIZE_COLLECTIONS).
        this.definitions = new HashMap<>(compatibilityHelper.hashMapCapacityFor(count));
        for (int i = 0; i < count; i++) {
            final ModelDefinition definition = definitions.get(i);
            final String key = definition.getKey();
            if (key == null) {
                throw new IllegalArgumentException(
                        "aiDefinitions[" + i + "].key is required (bad entry: " + definition + ")");
            }
            if (this.definitions.containsKey(key)) {
                throw new IllegalArgumentException("aiDefinitions[" + i + "].key '" + key + "' is defined twice");
            }
            this.definitions.put(key, resolved(definition, modelDirectory));
        }
    }

    /**
     * Returns the {@link ModelDefinition} associated with the given key.
     *
     * @param key the definition key to look up; must not be {@code null}
     * @return the resolved definition
     * @throws IllegalArgumentException if no definition is registered for {@code key}
     */
    public ModelDefinition definition(final String key) {
        final ModelDefinition config = definitions.get(key);
        if (config == null) {
            throw new IllegalArgumentException(MISSING_DEFINITION_MESSAGE_PREFIX + key);
        }
        return config;
    }

    /**
     * Resolves a configured model path: a relative path against {@code modelDirectory}, an absolute
     * path unchanged. So a configuration can name every model by its file name below one directory
     * and still point a single model anywhere else with an absolute path.
     *
     * <p>"Absolute" here means any path with a root, which on Windows also covers the root-relative
     * {@code /models/x.gguf} and the drive-relative {@code M:x.gguf}: both name a place of their own,
     * and joining them below the directory would produce a path nobody wrote. A path the platform
     * cannot parse is left unchanged too; the engines' model check reports it. The rule is
     * {@link SourcePaths#resolveAgainst}, which prompt template files share.</p>
     *
     * @param modelDirectory directory relative paths are resolved against, or {@code null} to leave
     *                       them relative to the working directory
     * @param modelPath      the configured path, or {@code null}
     * @return the path to load, or {@code null} when {@code modelPath} is {@code null}
     */
    static @Nullable String resolveModelPath(final @Nullable File modelDirectory, final @Nullable String modelPath) {
        return SourcePaths.resolveAgainst(modelDirectory, modelPath);
    }

    /**
     * Copies a definition with its model path resolved against {@code modelDirectory}.
     *
     * @param definition     the definition to copy; must not be {@code null}
     * @param modelDirectory directory a relative model path is resolved against, or {@code null}
     * @return the copy
     */
    private static ModelDefinition resolved(final ModelDefinition definition, final @Nullable File modelDirectory) {
        final ModelDefinition copy = definition.copy();
        final @Nullable String modelPath = definition.getModelPath();
        final String resolved = resolveModelPath(modelDirectory, modelPath);
        // null only when the definition names no model; passed on as before, for the engines'
        // fail-fast model path check to report.
        copy.setModelPath(resolved == null ? modelPath : resolved);
        return copy;
    }
}
