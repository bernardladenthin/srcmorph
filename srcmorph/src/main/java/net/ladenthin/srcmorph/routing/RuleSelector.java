// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.routing;

import lombok.ToString;
import net.ladenthin.srcmorph.config.OversizeStrategy;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.RuleCondition;
import net.ladenthin.srcmorph.facts.FactExtractor;
import org.jspecify.annotations.Nullable;

/**
 * Selects the routing rule ({@link RoutingRule}) that applies to a file.
 *
 * <p>Each rule carries an {@link RuleCondition} (a composable and/or/not tree of leaf matchers) plus a
 * {@link RoutingRule#getPriority() priority}, and is a normal <em>route</em> rule
 * (prompt + model), the explicit {@link RoutingRule#isFallback() fallback}, or a
 * {@link RoutingRule#isSkip() skip} rule.</p>
 *
 * <p>Selection: among all non-fallback rules whose condition matches, the highest priority wins (ties
 * by declaration order); if the winner is a skip the file is excluded; if no non-fallback rule matches,
 * the explicit fallback applies; if nothing matches and there is no fallback, {@link #select} returns
 * {@code null} so the caller fails loudly.</p>
 */
@ToString
public final class RuleSelector {

    private final ConditionEvaluator conditionEvaluator = new ConditionEvaluator();

    /** Creates a new {@link RuleSelector}. */
    public RuleSelector() {
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
    public @Nullable RoutingRule select(
            final Iterable<? extends @Nullable RoutingRule> configs, final FileContext context) {
        RoutingRule fallback = null;
        RoutingRule best = null;
        for (final @Nullable RoutingRule config : configs) {
            if (config == null) {
                continue;
            }
            if (config.isFallback()) {
                if (fallback == null) {
                    fallback = config;
                }
                continue;
            }
            final RuleCondition condition = config.getCondition();
            if (condition != null
                    && conditionEvaluator.matches(condition, context)
                    && (best == null || config.getPriority() > best.getPriority())) {
                best = config;
            }
        }
        return best != null ? best : fallback;
    }

    /**
     * Validates what one rule says on its own, for a file rule and the package and project phases' rule
     * alike: its {@code onOversize} and {@code onIncomplete} tokens, a {@code maxChunks} that would be
     * applied ({@code sampleChunks} needs one, every other strategy none), a map or reduce prompt only
     * where chunks are read ({@code mapReduce}, {@code sampleChunks}), and its facts by the rules a facts
     * file is held to.
     *
     * @param config the rule
     * @throws IllegalArgumentException if the rule is invalid, naming it
     */
    public void validateRule(final RoutingRule config) {
        // an unknown onOversize token throws IllegalArgumentException naming the value
        final boolean sampleChunks = config.oversizeStrategy() == OversizeStrategy.SAMPLE_CHUNKS;
        if (sampleChunks != config.getMaxChunks() > 0) {
            throw new IllegalArgumentException(
                    sampleChunks
                            ? "onOversize: sampleChunks needs a maxChunks above 0 (how many chunks to read): " + config
                            : "maxChunks applies only to onOversize: sampleChunks (mapReduce reads every chunk): "
                                    + config);
        }
        final boolean chunked = sampleChunks || config.oversizeStrategy() == OversizeStrategy.MAP_REDUCE;
        if (!chunked && (config.getMapPromptKey() != null || config.getReducePromptKey() != null)) {
            throw new IllegalArgumentException(
                    "mapPromptKey and reducePromptKey apply only to onOversize: mapReduce and sampleChunks: " + config);
        }
        config.incompleteStrategy();
        final String rule = config.getId() != null ? config.getId() : String.valueOf(config.getPromptKey());
        FactExtractor.validate("the facts of rule '" + rule + "'", config.getFacts());
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
    public void validate(final Iterable<? extends @Nullable RoutingRule> configs) {
        int fallbackCount = 0;
        for (final @Nullable RoutingRule config : configs) {
            if (config == null) {
                continue;
            }
            validateRule(config);
            if (config.isFallback()) {
                fallbackCount++;
                if (config.isSkip()) {
                    throw new IllegalArgumentException("A fallback rule cannot also be a skip rule: " + config);
                }
                if (config.getCondition() != null) {
                    throw new IllegalArgumentException(
                            "A fallback rule must not have a condition (it catches everything else): " + config);
                }
                requireRouteKeys(config);
            } else {
                final RuleCondition condition = config.getCondition();
                if (condition == null) {
                    throw new IllegalArgumentException("A non-fallback rule must have a condition: " + config);
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
    private void requireRouteKeys(final RoutingRule config) {
        if (config.getPromptKey() == null || config.getAiDefinitionKey() == null) {
            throw new IllegalArgumentException(
                    "A route/fallback rule must have a promptKey and an aiDefinitionKey: " + config);
        }
    }
}
