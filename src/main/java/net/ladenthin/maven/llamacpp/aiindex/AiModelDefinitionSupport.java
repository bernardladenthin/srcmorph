// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;

/**
 * Resolves {@link AiModelDefinition} entries by their key, returning the corresponding
 * {@link AiGenerationConfig}.
 *
 * <p>Built from the {@code <aiDefinitions>} list in the plugin configuration, this
 * support class acts as a lookup table that converts each {@link AiModelDefinition} into
 * a ready-to-use {@link AiGenerationConfig} object. Field-generation entries and mojos
 * reference definitions by their string key rather than embedding the full parameter
 * set inline.</p>
 *
 * <p>Definitions with a {@code null} key are silently ignored during construction.
 * Lookups for missing keys throw {@link IllegalArgumentException} so that
 * configuration errors are detected eagerly at runtime.</p>
 *
 * @see AiModelDefinition
 * @see AiFieldGenerationConfig
 */
@ToString
public class AiModelDefinitionSupport {

    /**
     * Error message prefix used when a requested key is not found in the definition map.
     * The missing key is appended after this prefix to produce the full exception message.
     */
    private static final String MISSING_DEFINITION_MESSAGE_PREFIX = "Missing AI model definition for key: ";

    private final Map<String, AiGenerationConfig> configs = new HashMap<>();

    /**
     * Builds a new {@code AiModelDefinitionSupport} from the supplied definitions list.
     *
     * <p>Each definition with a non-{@code null} key is converted to an
     * {@link AiGenerationConfig} and stored internally. Definitions with a {@code null}
     * key are silently skipped.</p>
     *
     * @param definitions list of AI model definitions; may be {@code null} or empty
     */
    public AiModelDefinitionSupport(final List<AiModelDefinition> definitions) {
        if (definitions != null) {
            for (AiModelDefinition definition : definitions) {
                if (definition.getKey() != null) {
                    configs.put(definition.getKey(), toConfig(definition));
                }
            }
        }
    }

    /**
     * Returns the {@link AiGenerationConfig} associated with the given key.
     *
     * @param key the definition key to look up; must not be {@code null}
     * @return the corresponding generation config
     * @throws IllegalArgumentException if no definition is registered for {@code key}
     */
    public AiGenerationConfig getConfig(final String key) {
        final AiGenerationConfig config = configs.get(key);
        if (config == null) {
            throw new IllegalArgumentException(MISSING_DEFINITION_MESSAGE_PREFIX + key);
        }
        return config;
    }

    /**
     * Converts an {@link AiModelDefinition} into an {@link AiGenerationConfig} by copying
     * all field values.
     *
     * @param definition the definition to convert; must not be {@code null}
     * @return a fully populated {@link AiGenerationConfig}
     */
    private static AiGenerationConfig toConfig(final AiModelDefinition definition) {
        final AiGenerationConfig config = new AiGenerationConfig();
        config.setModelPath(definition.getModelPath());
        config.setContextSize(definition.getContextSize());
        config.setMaxOutputTokens(definition.getMaxOutputTokens());
        config.setTemperature(definition.getTemperature());
        config.setThreads(definition.getThreads());
        config.setCharsPerToken(definition.getCharsPerToken());
        config.setWarnOnTrim(definition.isWarnOnTrim());
        config.setMaxRetries(definition.getMaxRetries());
        config.setRetryTemperatureIncrement(definition.getRetryTemperatureIncrement());
        config.setTopP(definition.getTopP());
        config.setTopK(definition.getTopK());
        config.setRepeatPenalty(definition.getRepeatPenalty());
        config.setStopStrings(definition.getStopStrings());
        config.setChatTemplateEnableThinking(definition.isChatTemplateEnableThinking());
        return config;
    }
}
