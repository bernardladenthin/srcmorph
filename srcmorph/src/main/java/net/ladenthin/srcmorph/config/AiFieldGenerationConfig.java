// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * One routing rule for the {@code generate} goal: a {@link #condition} that selects files, a
 * {@link #priority} to break ties when several rules match, and an action — route the matched files to
 * a {@code (promptKey, aiDefinitionKey)}, mark them {@link #skip skipped}, or act as the single explicit
 * {@link #fallback}.
 *
 * <p><strong>Note:</strong> this class must remain a mutable JavaBean with setters because the Maven
 * plugin framework populates it via reflection.</p>
 *
 * @see AiFieldGenerationSelector
 * @see AiCondition
 */
@ToString
@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})
public class AiFieldGenerationConfig {

    /** Creates a new {@link AiFieldGenerationConfig}. */
    public AiFieldGenerationConfig() {
        // no-op
    }

    /**
     * Optional human-readable id for this routing rule (e.g. {@code java-small}, {@code skip-generated}).
     * Purely a label: it is shown in the plan tree so you can see <em>which</em> rule routed each file,
     * and it appears in validation error messages. {@code null} when not set.
     */
    private @Nullable String id;

    private String promptKey;

    /**
     * Key that references an {@link AiModelDefinition} registered in the {@code <aiDefinitions>} list;
     * that definition supplies all AI generation parameters (model path, context size, sampling, …).
     */
    private String aiDefinitionKey;

    /**
     * The file-matching condition (a composable and/or/not tree of leaves — extension, size, lines,
     * modified-after/before, path glob). Required for route and skip rules; the {@link #fallback} has
     * none (it catches everything else).
     *
     * @see AiCondition
     * @see AiConditionEvaluator
     */
    private @Nullable AiCondition condition;

    /**
     * Selection priority. When several rules match the same file, the highest priority wins; ties are
     * broken by declaration order (the earlier rule wins). Default {@code 0}. Lets a specific rule
     * (e.g. a high-priority {@link #skip}) override a more general one regardless of XML order.
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
     * {@code mapReduce} (chunk + summarize each + combine), {@code deterministic} (model-free metadata
     * body). Parsed by {@link AiOversizeStrategy#fromConfig(String)}; {@code null}/blank = {@code fail}.
     */
    private @Nullable String onOversize;

    /**
     * For {@link AiOversizeStrategy#MAP_REDUCE}: the maximum number of chunks to summarize. {@code 0}
     * (default) = unbounded (process every chunk). A positive value bounds the run time by sampling that
     * many representative chunks (head + evenly spaced + tail) across the file.
     */
    private int maxChunks;

    /**
     * Optional deterministic "fact" counters ({@code <facts>}). When set, each counter's
     * {@code label: <match count over the whole file>} is prepended to the generated body of <em>every</em>
     * file this rule matches (oversize or not) — exact, language-agnostic structural counts (e.g. SQL
     * {@code INSERT} rows / tables / views, Java types / {@code boolean} fields) that give downstream
     * agents authoritative numbers a sampled AI summary cannot reliably produce. {@code null}/empty = no
     * facts block. See {@link AiFactExtractor}.
     */
    private @Nullable List<AiFactCounter> facts;

    /**
     * Optional reference to a shared {@code <factDefinitions>} group by its key ({@code <factsKey>}),
     * instead of repeating an inline {@link #facts} block. Resolved before indexing by
     * {@link AiFactDefinitionSupport#resolveFactsKeys(Iterable)}, which copies the referenced counters
     * onto {@link #facts} (overwriting any inline value). {@code null} = use the inline {@code <facts>}
     * (or none).
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
     * @return the key that identifies the prompt template ({@code null} only on an unconfigured skip rule)
     */
    public String getPromptKey() {
        return promptKey;
    }

    /**
     * Sets the prompt template key.
     *
     * @param promptKey key that references an {@link net.ladenthin.srcmorph.prompt.AiPromptDefinition}
     */
    public void setPromptKey(final String promptKey) {
        this.promptKey = promptKey;
    }

    /**
     * Returns the AI model definition key.
     *
     * @return the key that references the {@link AiModelDefinition} ({@code null} only on a skip rule)
     */
    public String getAiDefinitionKey() {
        return aiDefinitionKey;
    }

    /**
     * Sets the AI model definition key.
     *
     * @param aiDefinitionKey key that references an {@link AiModelDefinition}
     */
    public void setAiDefinitionKey(final String aiDefinitionKey) {
        this.aiDefinitionKey = aiDefinitionKey;
    }

    /**
     * Returns the file-matching condition, or {@code null} (the fallback has none).
     *
     * @return the condition, or {@code null}
     */
    public @Nullable AiCondition getCondition() {
        return condition;
    }

    /**
     * Sets the file-matching condition.
     *
     * @param condition the condition tree
     */
    public void setCondition(final @Nullable AiCondition condition) {
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
     * Sets the {@code onOversize} config token (one of {@code fail}/{@code sample}/{@code mapReduce}/{@code deterministic}).
     *
     * @param onOversize the oversize-strategy token
     */
    public void setOnOversize(final @Nullable String onOversize) {
        this.onOversize = onOversize;
    }

    /**
     * Returns the parsed oversize strategy ({@link AiOversizeStrategy#FAIL} when unset/blank).
     *
     * @return the oversize strategy
     * @throws IllegalArgumentException if {@code onOversize} is non-blank and matches no strategy
     */
    public AiOversizeStrategy getOversizeStrategy() {
        return AiOversizeStrategy.fromConfig(onOversize);
    }

    /**
     * Returns the map-reduce chunk cap ({@code 0} = unbounded).
     *
     * @return the maximum number of chunks
     */
    public int getMaxChunks() {
        return maxChunks;
    }

    /**
     * Sets the map-reduce chunk cap ({@code 0} = unbounded).
     *
     * @param maxChunks the maximum number of chunks
     */
    public void setMaxChunks(final int maxChunks) {
        this.maxChunks = maxChunks;
    }

    /**
     * Returns the optional deterministic fact counters, or {@code null} when none are configured.
     *
     * @return the fact counters, or {@code null}
     */
    public @Nullable List<AiFactCounter> getFacts() {
        return facts;
    }

    /**
     * Sets the deterministic fact counters.
     *
     * @param facts the fact counters
     */
    public void setFacts(final @Nullable List<AiFactCounter> facts) {
        this.facts = facts;
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
}
