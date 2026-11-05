// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * One routing rule of the configuration file's {@code fieldGenerations}: a {@link #condition} that
 * selects files, a {@link #priority} to break ties when several rules match, and an action — route the
 * matched files to a {@code (promptKey, aiDefinitionKey)}, mark them {@link #skip skipped}, or act as
 * the single explicit {@link #fallback}. The package and project phases take one rule of the same
 * shape from {@code packageFieldGenerations} / {@code projectFieldGenerations}.
 *
 * <p><strong>Note:</strong> this class must remain a mutable JavaBean with setters because the
 * configuration file is bound through them.</p>
 *
 * @see net.ladenthin.srcmorph.routing.RuleSelector
 * @see RuleCondition
 */
@ToString
public class RoutingRule {

    /** Creates a new {@link RoutingRule}. */
    public RoutingRule() {
        // no-op
    }

    /**
     * Optional human-readable id for this routing rule (e.g. {@code java-small}, {@code skip-generated}).
     * Purely a label: it is shown in the plan tree so you can see <em>which</em> rule routed each file,
     * and it appears in validation error messages. {@code null} when not set.
     */
    private @Nullable String id;

    /**
     * Key that references the {@link PromptDefinition} of the rule's prompt; {@code null} on a skip rule,
     * which has none.
     */
    private @Nullable String promptKey;

    /**
     * Key that references an {@link ModelDefinition} registered in the {@code aiDefinitions} list;
     * that definition supplies all AI generation parameters (model path, context size, sampling, …).
     */
    private @Nullable String aiDefinitionKey;

    /**
     * The file-matching condition (a composable and/or/not tree of leaves — extension, size, lines,
     * modified-after/before, path glob). Required for route and skip rules; the {@link #fallback} has
     * none (it catches everything else).
     *
     * @see RuleCondition
     * @see net.ladenthin.srcmorph.routing.ConditionEvaluator
     */
    private @Nullable RuleCondition condition;

    /**
     * Selection priority. When several rules match the same file, the highest priority wins; ties are
     * broken by declaration order (the earlier rule wins). Default {@code 0}. Lets a specific rule
     * (e.g. a high-priority {@link #skip}) override a more general one regardless of the order in the
     * configuration file.
     */
    private int priority;

    /**
     * Marks this rule as the explicit fallback: it applies to any file no other rule matched. At most
     * one fallback may be configured. A fallback routes (needs {@link #promptKey} and
     * {@link #aiDefinitionKey}), has no {@link #condition}, and cannot be a {@link #skip}. When no rule
     * matches a file and no fallback is configured, indexing fails rather than silently skipping it.
     */
    private boolean fallback;

    /**
     * Marks this rule as a skip (ignore) rule: matching files are excluded from indexing entirely. A
     * skip competes by {@link #priority} like any matching rule, so a high-priority skip excludes files
     * a route rule or the fallback would otherwise pick up. Skip rules need a {@link #condition} but no
     * prompt/model.
     */
    private boolean skip;

    /**
     * What to do when a matched file is larger than its routed model's context window. One of
     * {@code fail} (default — abort the build), {@code sample} (trim to the window, summarize the head),
     * {@code mapReduce} (chunk + summarize every chunk + combine), {@code sampleChunks} (the same over
     * {@link #maxChunks} representative chunks), {@code deterministic} (model-free metadata body). Parsed by {@link OversizeStrategy#fromConfig(String)}; {@code null}/blank = {@code fail} for a file rule, while the package
     * and project phases' rule defaults to {@code mapReduce} (their input is the summaries below them).
     */
    private @Nullable String onOversize;

    /**
     * What to do when the model's answer is no summary (empty, cut off at the output budget, or no answer
     * at all): {@code fail} (default; the file is counted as failed and the run fails at its end),
     * {@code retryOnce} (ask once more first) or {@code keep} (write an answer cut off at the output budget
     * as it is, when it has text). Any other such answer is never written. Parsed by
     * {@link IncompleteStrategy#fromConfig(String)}.
     */
    private @Nullable String onIncomplete;

    /**
     * For {@link OversizeStrategy#SAMPLE_CHUNKS} only, and required there: how many representative
     * chunks (the first, the last, evenly spaced between) are summarized. {@code 0} (default) for every
     * other strategy; a positive value with another one fails the rule set.
     */
    private int maxChunks;

    /**
     * For {@link OversizeStrategy#MAP_REDUCE} and {@link OversizeStrategy#SAMPLE_CHUNKS} only: the key of
     * the prompt appended to {@link #promptKey} in every map call, which says that the input is one chunk
     * of the source ({@code Chunk i of n} in the user message). {@code null} = the rule's prompt alone.
     */
    private @Nullable String mapPromptKey;

    /**
     * For {@link OversizeStrategy#MAP_REDUCE} and {@link OversizeStrategy#SAMPLE_CHUNKS} only: the key of
     * the prompt appended to {@link #promptKey} in every reduce call, which says that the input is the part
     * summaries of one source, to be merged into one. {@code null} = the rule's prompt alone.
     */
    private @Nullable String reducePromptKey;

    /**
     * Optional deterministic "fact" counters ({@code facts}). When set, each counter's
     * {@code label: <match count over the whole file>} is prepended to the generated body of <em>every</em>
     * file this rule matches (oversize or not) — exact, language-agnostic structural counts (e.g. SQL
     * {@code INSERT} rows / tables / views, Java types / {@code boolean} fields) that give downstream
     * agents authoritative numbers a sampled AI summary cannot reliably produce. {@code null}/empty = no
     * facts block. See {@link net.ladenthin.srcmorph.facts.FactExtractor}.
     */
    private @Nullable List<FactCounter> facts;

    /**
     * Optional reference to a shared {@code factDefinitions} group by its key ({@code factsKey}), or to
     * the file {@code <profileDirectory>/<key>.tsv}, instead of repeating an inline {@link #facts} list.
     * Resolved before indexing by {@link net.ladenthin.srcmorph.facts.FactGroups#resolveFactsKeys(Iterable)}, which copies
     * the referenced counters onto {@link #facts}; a rule that sets both fails. {@code null} = use the
     * inline {@code facts} (or none).
     */
    private @Nullable String factsKey;

    /**
     * Returns the optional rule id (label), or {@code null} when not set.
     *
     * @return the rule id, or {@code null}
     */
    public @Nullable String getId() {
        return id;
    }

    /**
     * Sets the optional rule id (label).
     *
     * @param id the rule id
     */
    public void setId(final @Nullable String id) {
        this.id = id;
    }

    /**
     * Returns the prompt template key.
     *
     * @return the key that identifies the prompt template, or {@code null} (a skip rule has none)
     * @see #requirePromptKey()
     */
    public @Nullable String getPromptKey() {
        return promptKey;
    }

    /**
     * Returns the prompt template key of a rule that routes: a route rule or the fallback, which
     * {@link net.ladenthin.srcmorph.routing.RuleSelector#validate validation} gave one.
     *
     * @return the key that identifies the prompt template
     * @throws IllegalStateException if the rule has none (a skip rule)
     */
    public String requirePromptKey() {
        final String key = promptKey;
        if (key == null) {
            throw new IllegalStateException("The rule has no promptKey: " + this);
        }
        return key;
    }

    /**
     * Sets the prompt template key.
     *
     * @param promptKey key that references an {@link net.ladenthin.srcmorph.config.PromptDefinition}
     */
    public void setPromptKey(final @Nullable String promptKey) {
        this.promptKey = promptKey;
    }

    /**
     * Returns the AI model definition key.
     *
     * @return the key that references the {@link ModelDefinition}, or {@code null} (a skip rule has none)
     * @see #requireAiDefinitionKey()
     */
    public @Nullable String getAiDefinitionKey() {
        return aiDefinitionKey;
    }

    /**
     * Returns the AI model definition key of a rule that routes: a route rule or the fallback, which
     * {@link net.ladenthin.srcmorph.routing.RuleSelector#validate validation} gave one.
     *
     * @return the key that references the {@link ModelDefinition}
     * @throws IllegalStateException if the rule has none (a skip rule)
     */
    public String requireAiDefinitionKey() {
        final String key = aiDefinitionKey;
        if (key == null) {
            throw new IllegalStateException("The rule has no aiDefinitionKey: " + this);
        }
        return key;
    }

    /**
     * Sets the AI model definition key.
     *
     * @param aiDefinitionKey key that references an {@link ModelDefinition}
     */
    public void setAiDefinitionKey(final @Nullable String aiDefinitionKey) {
        this.aiDefinitionKey = aiDefinitionKey;
    }

    /**
     * Returns the file-matching condition, or {@code null} (the fallback has none).
     *
     * @return the condition, or {@code null}
     */
    public @Nullable RuleCondition getCondition() {
        return condition;
    }

    /**
     * Sets the file-matching condition.
     *
     * @param condition the condition tree
     */
    public void setCondition(final @Nullable RuleCondition condition) {
        this.condition = condition;
    }

    /**
     * Returns the selection priority ({@code 0} = default).
     *
     * @return the selection priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the selection priority (higher wins when several rules match).
     *
     * @param priority the selection priority
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Returns whether this rule is the explicit fallback.
     *
     * @return {@code true} if this rule is the fallback
     */
    public boolean isFallback() {
        return fallback;
    }

    /**
     * Sets whether this rule is the explicit fallback.
     *
     * @param fallback {@code true} to mark this rule as the fallback
     */
    public void setFallback(final boolean fallback) {
        this.fallback = fallback;
    }

    /**
     * Returns whether this rule is a skip (ignore) rule.
     *
     * @return {@code true} if matching files should be skipped
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * Sets whether this rule is a skip (ignore) rule.
     *
     * @param skip {@code true} to skip files this rule matches
     */
    public void setSkip(final boolean skip) {
        this.skip = skip;
    }

    /**
     * Returns the raw {@code onOversize} config token, or {@code null} when not set.
     *
     * @return the oversize-strategy token, or {@code null}
     */
    public @Nullable String getOnOversize() {
        return onOversize;
    }

    /**
     * Sets the {@code onOversize} config token (one of {@code fail}/{@code sample}/{@code mapReduce}/
     * {@code sampleChunks}/{@code deterministic}).
     *
     * @param onOversize the oversize-strategy token
     */
    public void setOnOversize(final @Nullable String onOversize) {
        this.onOversize = onOversize;
    }

    /**
     * Returns the parsed oversize strategy ({@link OversizeStrategy#FAIL} when unset/blank). Not a bean
     * getter, so a serialized configuration carries only the token.
     *
     * @return the oversize strategy
     * @throws IllegalArgumentException if {@code onOversize} is non-blank and matches no strategy
     */
    public OversizeStrategy oversizeStrategy() {
        return OversizeStrategy.fromConfig(onOversize);
    }

    /**
     * Returns the raw {@code onIncomplete} config token, or {@code null} when not set.
     *
     * @return the incomplete-answer strategy token, or {@code null}
     */
    public @Nullable String getOnIncomplete() {
        return onIncomplete;
    }

    /**
     * Sets the {@code onIncomplete} config token (one of {@code fail}/{@code retryOnce}/{@code keep}).
     *
     * @param onIncomplete the incomplete-answer strategy token
     */
    public void setOnIncomplete(final @Nullable String onIncomplete) {
        this.onIncomplete = onIncomplete;
    }

    /**
     * Returns the parsed incomplete-answer strategy ({@link IncompleteStrategy#FAIL} when unset/blank).
     * Not a bean getter, so a serialized configuration carries only the token.
     *
     * @return the incomplete-answer strategy
     * @throws IllegalArgumentException if {@code onIncomplete} is non-blank and matches no strategy
     */
    public IncompleteStrategy incompleteStrategy() {
        return IncompleteStrategy.fromConfig(onIncomplete);
    }

    /**
     * Returns how many chunks {@code sampleChunks} reads. Above {@code 0} for {@code sampleChunks}, which
     * requires it; {@code 0} (unset) for every other strategy ({@code mapReduce} reads every chunk).
     *
     * @return the number of chunks {@code sampleChunks} reads, or {@code 0}
     */
    public int getMaxChunks() {
        return maxChunks;
    }

    /**
     * Sets how many chunks {@code sampleChunks} reads. Required above {@code 0} with {@code sampleChunks};
     * a positive value with another strategy fails the rule set.
     *
     * @param maxChunks the number of chunks {@code sampleChunks} reads, or {@code 0}
     */
    public void setMaxChunks(final int maxChunks) {
        this.maxChunks = maxChunks;
    }

    /**
     * Returns the optional deterministic fact counters, or {@code null} when none are configured.
     *
     * @return the fact counters, or {@code null}
     */
    public @Nullable List<FactCounter> getFacts() {
        return facts;
    }

    /**
     * Sets the deterministic fact counters.
     *
     * @param facts the fact counters
     */
    public void setFacts(final @Nullable List<FactCounter> facts) {
        this.facts = facts;
    }

    /**
     * Returns the key of the prompt appended to the rule's prompt in a map call, or {@code null}.
     *
     * @return the map prompt key, or {@code null}
     */
    public @Nullable String getMapPromptKey() {
        return mapPromptKey;
    }

    /**
     * Sets the key of the prompt appended to the rule's prompt in a map call.
     *
     * @param mapPromptKey the map prompt key
     */
    public void setMapPromptKey(final @Nullable String mapPromptKey) {
        // blank is unset, as composedKey reads it, so validation and loading see the same
        this.mapPromptKey = mapPromptKey == null || mapPromptKey.trim().isEmpty() ? null : mapPromptKey;
    }

    /**
     * Returns the key of the prompt appended to the rule's prompt in a reduce call, or {@code null}.
     *
     * @return the reduce prompt key, or {@code null}
     */
    public @Nullable String getReducePromptKey() {
        return reducePromptKey;
    }

    /**
     * Sets the key of the prompt appended to the rule's prompt in a reduce call.
     *
     * @param reducePromptKey the reduce prompt key
     */
    public void setReducePromptKey(final @Nullable String reducePromptKey) {
        // blank is unset, as composedKey reads it, so validation and loading see the same
        this.reducePromptKey = reducePromptKey == null || reducePromptKey.trim().isEmpty() ? null : reducePromptKey;
    }

    /**
     * Returns the shared fact-definition reference key, or {@code null} when not set.
     *
     * @return the facts key, or {@code null}
     */
    public @Nullable String getFactsKey() {
        return factsKey;
    }

    /**
     * Sets the shared fact-definition reference key.
     *
     * @param factsKey the facts key
     */
    public void setFactsKey(final @Nullable String factsKey) {
        this.factsKey = factsKey;
    }

    /**
     * Returns an independent copy of this rule with every field; the {@link #facts} list is copied, its
     * counters and the {@link #condition} tree are shared. An indexer keeps its rule as a copy, so the
     * caller's bean can change after construction without changing a run.
     *
     * @return the copy
     */
    public RoutingRule copy() {
        final RoutingRule copy = new RoutingRule();
        copy.id = id;
        copy.promptKey = promptKey;
        copy.aiDefinitionKey = aiDefinitionKey;
        copy.condition = condition;
        copy.priority = priority;
        copy.fallback = fallback;
        copy.skip = skip;
        copy.onOversize = onOversize;
        copy.onIncomplete = onIncomplete;
        copy.maxChunks = maxChunks;
        copy.mapPromptKey = mapPromptKey;
        copy.reducePromptKey = reducePromptKey;
        final List<FactCounter> ownFacts = facts;
        copy.facts = ownFacts == null ? null : new ArrayList<>(ownFacts);
        copy.factsKey = factsKey;
        return copy;
    }
}
