// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Selects the routing rule ({@link AiFieldGenerationConfig}) that applies to a file.
 *
 * <p>Each rule carries an {@link AiCondition} (a composable and/or/not tree of leaf matchers) plus a
 * {@link AiFieldGenerationConfig#getPriority() priority}, and is a normal <em>route</em> rule
 * (prompt + model), the explicit {@link AiFieldGenerationConfig#isFallback() fallback}, or a
 * {@link AiFieldGenerationConfig#isSkip() skip} rule.</p>
 *
 * <p>Selection: among all non-fallback rules whose condition matches, the highest priority wins (ties
 * by declaration order); if the winner is a skip the file is excluded; if no non-fallback rule matches,
 * the explicit fallback applies; if nothing matches and there is no fallback, {@link #select} returns
 * {@code null} so the caller fails loudly.</p>
 */
@ToString
public final class AiFieldGenerationSelector {

    private final AiConditionEvaluator conditionEvaluator = new AiConditionEvaluator();

    /** Creates a new {@link AiFieldGenerationSelector}. */
    public AiFieldGenerationSelector() {
        // no-op
    }

    /**
     * Returns the rule that applies to {@code context}, resolving ties by priority then declaration
     * order.
     *
     * @param configs the configured rules, in declaration order; {@code null} entries are skipped
     * @param context the file facts the conditions are evaluated against
     * @return the winning rule (which may be a skip or the fallback), or {@code null} when nothing
     *         matches and no fallback is configured
     */
    public @Nullable AiFieldGenerationConfig select(
            final Iterable<AiFieldGenerationConfig> configs, final AiFileContext context) {
        AiFieldGenerationConfig fallback = null;
        AiFieldGenerationConfig best = null;
        for (final AiFieldGenerationConfig config : configs) {
            if (config == null) {
                continue;
            }
            if (config.isFallback()) {
                if (fallback == null) {
                    fallback = config;
                }
                continue;
            }
            final AiCondition condition = config.getCondition();
            if (condition != null
                    && conditionEvaluator.matches(condition, context)
                    && (best == null || config.getPriority() > best.getPriority())) {
                best = config;
            }
        }
        return best != null ? best : fallback;
    }

    /**
     * Validates a rule set, throwing {@link IllegalArgumentException} on a misconfiguration so the build
     * fails fast.
     *
     * <p>Checks: at most one fallback; the fallback must have no condition, must not be a skip, and must
     * have a prompt + model; every non-fallback rule must have a (valid) condition; route rules must
     * have a prompt + model (skip rules need neither).</p>
     *
     * @param configs the configured rules
     * @throws IllegalArgumentException if the rule set is invalid
     */
    public void validate(final Iterable<AiFieldGenerationConfig> configs) {
        int fallbackCount = 0;
        for (final AiFieldGenerationConfig config : configs) {
            if (config == null) {
                continue;
            }
            // Fail fast on an unknown onOversize token (throws IllegalArgumentException naming the value).
            config.getOversizeStrategy();
            // Fail fast on a malformed <facts> counter (missing label/pattern or an invalid regex).
            AiFactExtractor.validate(config.getFacts());
            if (config.isFallback()) {
                fallbackCount++;
                if (config.isSkip()) {
                    throw new IllegalArgumentException("A fallback rule cannot also be a skip rule: " + config);
                }
                if (config.getCondition() != null) {
                    throw new IllegalArgumentException(
                            "A fallback rule must not have a <condition> (it catches everything else): " + config);
                }
                requireRouteKeys(config);
            } else {
                final AiCondition condition = config.getCondition();
                if (condition == null) {
                    throw new IllegalArgumentException("A non-fallback rule must have a <condition>: " + config);
                }
                conditionEvaluator.validate(condition);
                if (!config.isSkip()) {
                    requireRouteKeys(config);
                }
            }
        }
        if (fallbackCount > 1) {
            throw new IllegalArgumentException("At most one fallback rule may be configured, found " + fallbackCount);
        }
    }

    /**
     * Throws when a route/fallback rule is missing the prompt key or AI definition key.
     *
     * @param config the rule
     */
    private void requireRouteKeys(final AiFieldGenerationConfig config) {
        if (config.getPromptKey() == null || config.getAiDefinitionKey() == null) {
            throw new IllegalArgumentException(
                    "A route/fallback rule must have a promptKey and an aiDefinitionKey: " + config);
        }
    }
}
