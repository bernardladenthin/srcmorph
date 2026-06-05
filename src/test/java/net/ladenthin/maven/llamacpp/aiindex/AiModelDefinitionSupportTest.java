// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AiModelDefinitionSupportTest {

    // <editor-fold defaultstate="collapsed" desc="getConfig">
    @Test
    public void getConfig_knownKey_returnsConfig() {
        // arrange
        final String key = "my-model";
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(key);
        definition.setModelPath("/path/to/model.gguf");
        final AiModelDefinitionSupport support = new AiModelDefinitionSupport(Arrays.asList(definition));

        // act
        final AiGenerationConfig config = support.getConfig(key);

        // assert
        assertThat(config, is(notNullValue()));
        assertThat(config.getModelPath(), is(equalTo("/path/to/model.gguf")));
    }

    @Test
    public void getConfig_knownKey_propagatesAllFields() {
        // arrange
        final String key = "full-model";
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(key);
        definition.setModelPath("/models/test.gguf");
        definition.setContextSize(16384);
        definition.setMaxOutputTokens(512);
        definition.setTemperature(0.8f);
        definition.setThreads(4);
        definition.setCharsPerToken(3);
        definition.setWarnOnTrim(false);
        definition.setMaxRetries(5);
        definition.setRetryTemperatureIncrement(0.2f);
        definition.setChatTemplateEnableThinking(false);
        final AiModelDefinitionSupport support = new AiModelDefinitionSupport(Arrays.asList(definition));

        // act
        final AiGenerationConfig config = support.getConfig(key);

        // assert
        assertThat(config.getModelPath(), is(equalTo("/models/test.gguf")));
        assertThat(config.getContextSize(), is(equalTo(16384)));
        assertThat(config.getMaxOutputTokens(), is(equalTo(512)));
        assertThat(config.getTemperature(), is(equalTo(0.8f)));
        assertThat(config.getThreads(), is(equalTo(4)));
        assertThat(config.getCharsPerToken(), is(equalTo(3)));
        assertThat(config.isWarnOnTrim(), is(false));
        assertThat(config.getMaxRetries(), is(equalTo(5)));
        assertThat(config.getRetryTemperatureIncrement(), is(equalTo(0.2f)));
        assertThat(config.isChatTemplateEnableThinking(), is(false));
    }

    @Test
    public void getConfig_defaultValues_matchAiGenerationConfigDefaults() {
        // arrange
        final String key = "defaults-model";
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(key);
        final AiModelDefinitionSupport support = new AiModelDefinitionSupport(Arrays.asList(definition));

        // act
        final AiGenerationConfig config = support.getConfig(key);

        // assert
        assertThat(config.getContextSize(), is(equalTo(AiGenerationConfig.DEFAULT_CONTEXT_SIZE)));
        assertThat(config.getMaxOutputTokens(), is(equalTo(AiGenerationConfig.DEFAULT_MAX_OUTPUT_TOKENS)));
        assertThat(config.getTemperature(), is(equalTo(AiGenerationConfig.DEFAULT_TEMPERATURE)));
        assertThat(config.getThreads(), is(equalTo(AiGenerationConfig.DEFAULT_THREADS)));
        assertThat(config.getCharsPerToken(), is(equalTo(AiGenerationConfig.DEFAULT_CHARS_PER_TOKEN)));
        assertThat(config.getMaxInputChars(), is(equalTo(AiGenerationConfig.DEFAULT_MAX_INPUT_CHARS)));
        assertThat(config.isWarnOnTrim(), is(AiGenerationConfig.DEFAULT_WARN_ON_TRIM));
        assertThat(config.getMaxRetries(), is(equalTo(AiGenerationConfig.DEFAULT_MAX_RETRIES)));
        assertThat(
                config.getRetryTemperatureIncrement(),
                is(equalTo(AiGenerationConfig.DEFAULT_RETRY_TEMPERATURE_INCREMENT)));
        assertThat(config.isChatTemplateEnableThinking(), is(AiGenerationConfig.DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING));
    }

    @Test
    public void getConfig_unknownKey_throwsException() {
        // arrange
        final AiModelDefinitionSupport support =
                new AiModelDefinitionSupport(Collections.<AiModelDefinition>emptyList());

        // act / assert
        assertThrows(IllegalArgumentException.class, () -> support.getConfig("unknown-key"));
    }

    @Test
    public void getConfig_unknownKey_exceptionMessageContainsKey() {
        // arrange
        final String missingKey = "missing-model-key";
        final AiModelDefinitionSupport support =
                new AiModelDefinitionSupport(Collections.<AiModelDefinition>emptyList());

        try {
            // act
            support.getConfig(missingKey);
        } catch (IllegalArgumentException e) {
            // assert
            assertThat(e.getMessage(), containsString(missingKey));
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException was not thrown");
    }

    @Test
    public void getConfig_multipleDefinitions_returnsCorrectOne() {
        // arrange
        final AiModelDefinition defA = new AiModelDefinition();
        defA.setKey("model-a");
        defA.setMaxRetries(1);

        final AiModelDefinition defB = new AiModelDefinition();
        defB.setKey("model-b");
        defB.setMaxRetries(7);

        final AiModelDefinitionSupport support = new AiModelDefinitionSupport(Arrays.asList(defA, defB));

        // act
        final AiGenerationConfig configA = support.getConfig("model-a");
        final AiGenerationConfig configB = support.getConfig("model-b");

        // assert
        assertThat(configA.getMaxRetries(), is(equalTo(1)));
        assertThat(configB.getMaxRetries(), is(equalTo(7)));
    }

    @Test
    public void constructor_definitionWithNullKey_throwsWithIndexAndBadEntry() {
        // arrange — first entry well-formed, second missing the required key
        final AiModelDefinition validDef = new AiModelDefinition();
        validDef.setKey("valid");
        final AiModelDefinition nullKeyDef = new AiModelDefinition();
        // key not set — remains null

        // act + assert — construction now fails fast at the bad entry rather than
        // silently dropping it (which would surface as "Missing AI model definition"
        // deeper in the goal). Message must name the list index and dump the bad
        // entry so the user can locate the misconfiguration in their POM.
        final NullPointerException npe = assertThrows(
                NullPointerException.class, () -> new AiModelDefinitionSupport(Arrays.asList(validDef, nullKeyDef)));
        assertThat(npe.getMessage(), containsString("aiDefinitions[1].key"));
        assertThat(npe.getMessage(), containsString("AiModelDefinition"));
    }

    @Test
    public void getConfig_nullDefinitionsList_throwsForAnyKey() {
        // arrange
        final AiModelDefinitionSupport support = new AiModelDefinitionSupport(null);

        try {
            // act
            support.getConfig("any-key");
        } catch (IllegalArgumentException e) {
            // assert
            assertThat(e.getMessage(), containsString("any-key"));
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException was not thrown");
    }
    // </editor-fold>
}
