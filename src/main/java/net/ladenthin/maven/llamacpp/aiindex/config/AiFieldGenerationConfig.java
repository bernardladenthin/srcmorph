// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Maven plugin configuration POJO that associates a prompt template (by key) with an
 * AI model definition (also by key) for a single field-generation step.
 *
 * <p>Instances are declared inside the {@code <fieldGenerations>} list in the plugin
 * configuration. Each entry causes one AI generation call per indexed file or package:
 * the prompt identified by {@link #promptKey} is prepared and sent to the AI provider
 * configured by the {@link AiModelDefinition} identified by {@link #aiDefinitionKey}.</p>
 *
 * <p>Example POM fragment:</p>
 * <pre>{@code
 * <fieldGeneration>
 *     <promptKey>file-body</promptKey>
 *     <aiDefinitionKey>codestral-32k</aiDefinitionKey>
 * </fieldGeneration>
 * }</pre>
 *
 * <p><strong>Note:</strong> This class must remain a mutable JavaBean with setters because
 * Maven's plugin framework instantiates configuration objects via reflection and injects
 * values through setters.</p>
 *
 * @see AiModelDefinition
 * @see AiModelDefinitionSupport
 * @see net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition
 */
@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})
@ToString
public class AiFieldGenerationConfig {

    /** Creates a new {@link AiFieldGenerationConfig}. */
    public AiFieldGenerationConfig() {
        // no-op
    }

    /**
     * Optional human-readable id for this routing rule (e.g. {@code java-small}, {@code skip-generated}).
     * Purely a label: it is shown in the plan tree so you can see <em>which</em> rule routed each file
     * (handy when several rules share a prompt), and it appears in validation error messages. {@code null}
     * when not set.
     */
    private String id;

    private String promptKey;

    /**
     * Key that references an {@link AiModelDefinition} registered in the
     * {@code <aiDefinitions>} list.
     *
     * <p>The referenced definition supplies all AI generation parameters (model path,
     * context size, temperature, retry policy, input trimming limits, etc.) for this
     * field-generation step.</p>
     */
    private String aiDefinitionKey;

    /**
     * Optional source file extensions (e.g. {@code .java}, {@code .sql}) that select this field
     * generation for a file. When non-empty, this entry applies only to files whose name ends with
     * one of the listed extensions. When {@code null} or empty, this entry is the fallback applied to
     * any file that no extension-specific entry matched.
     *
     * @see AiFieldGenerationSelector
     */
    private @Nullable List<String> fileExtensions;

    /**
     * Optional exclusive lower file-size bound in bytes. When {@code > 0}, this entry applies only to
     * files whose size is strictly greater than this. {@code 0} (default) disables the lower bound.
     * Combined with {@link #maxFileSizeBytes} this lets the prompt be chosen by file size within a
     * single run (e.g. a terse prompt for small files, a detailed one for large).
     *
     * @see AiFieldGenerationSelector
     */
    private long minFileSizeBytes;

    /**
     * Optional inclusive upper file-size bound in bytes. When {@code > 0}, this entry applies only to
     * files whose size is less than or equal to this. {@code 0} (default) disables the upper bound.
     *
     * @see AiFieldGenerationSelector
     */
    private long maxFileSizeBytes;

    /**
     * Optional exclusive lower line-count bound. When {@code > 0}, this entry applies only to files
     * with strictly more than this many lines. {@code 0} (default) disables the lower bound.
     *
     * @see AiFieldGenerationSelector
     */
    private int minLines;

    /**
     * Optional inclusive upper line-count bound. When {@code > 0}, this entry applies only to files
     * with at most this many lines. {@code 0} (default) disables the upper bound.
     *
     * @see AiFieldGenerationSelector
     */
    private int maxLines;

    /**
     * Selection priority. When several rules match the same file, the one with the highest priority
     * wins; ties are broken by declaration order (the earlier rule wins). Default {@code 0}. Lets a
     * specific rule (e.g. a high-priority {@link #skip}) override a more general one regardless of XML
     * order.
     *
     * @see AiFieldGenerationSelector
     */
    private int priority;

    /**
     * Marks this rule as the explicit fallback: it applies to any file that no other (non-fallback)
     * rule matched. At most one fallback may be configured. A fallback routes (needs
     * {@link #promptKey} and {@link #aiDefinitionKey}); it cannot also be a {@link #skip}. When no rule
     * matches a file and no fallback is configured, indexing fails with an error rather than silently
     * skipping the file.
     *
     * @see AiFieldGenerationSelector
     */
    private boolean fallback;

    /**
     * Marks this rule as a skip (ignore) rule: matching files are excluded from indexing entirely (no
     * model, no prompt). A skip rule competes by {@link #priority} like any matching rule, so giving it
     * a high priority lets it exclude files that a route rule or the fallback would otherwise pick up.
     * Skip rules need filters but no {@link #promptKey}/{@link #aiDefinitionKey}.
     *
     * @see AiFieldGenerationSelector
     */
    private boolean skip;

    /**
     * Returns the optional rule id (label), or {@code null} when not set.
     *
     * @return the rule id, or {@code null}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the optional rule id (label).
     *
     * @param id the rule id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the prompt template key.
     *
     * @return the key that identifies the prompt template to use for this field
     */
    public String getPromptKey() {
        return promptKey;
    }

    /**
     * Sets the prompt template key.
     *
     * @param promptKey key that references an {@link net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition}
     */
    public void setPromptKey(final String promptKey) {
        this.promptKey = promptKey;
    }

    /**
     * Returns the AI model definition key.
     *
     * @return the key that references the {@link AiModelDefinition} to use
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
     * Returns the source file extensions that select this entry, or {@code null} when this entry is
     * the extension-agnostic fallback.
     *
     * @return the selecting file extensions, or {@code null}
     */
    public @Nullable List<String> getFileExtensions() {
        return fileExtensions != null ? Collections.unmodifiableList(fileExtensions) : null;
    }

    /**
     * Sets the source file extensions that select this entry. The list is defensively copied.
     *
     * @param fileExtensions selecting file extensions (e.g. {@code .java}); {@code null} or empty
     *                       makes this entry the fallback
     */
    public void setFileExtensions(final @Nullable Collection<String> fileExtensions) {
        this.fileExtensions = fileExtensions != null ? new ArrayList<>(fileExtensions) : null;
    }

    /**
     * Returns the exclusive lower file-size bound in bytes ({@code 0} = no lower bound).
     *
     * @return exclusive lower file-size bound in bytes
     */
    public long getMinFileSizeBytes() {
        return minFileSizeBytes;
    }

    /**
     * Sets the exclusive lower file-size bound in bytes.
     *
     * @param minFileSizeBytes exclusive lower bound ({@code 0} disables it)
     */
    public void setMinFileSizeBytes(final long minFileSizeBytes) {
        this.minFileSizeBytes = minFileSizeBytes;
    }

    /**
     * Returns the inclusive upper file-size bound in bytes ({@code 0} = no upper bound).
     *
     * @return inclusive upper file-size bound in bytes
     */
    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    /**
     * Sets the inclusive upper file-size bound in bytes.
     *
     * @param maxFileSizeBytes inclusive upper bound ({@code 0} disables it)
     */
    public void setMaxFileSizeBytes(final long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    /**
     * Returns the exclusive lower line-count bound ({@code 0} = no lower bound).
     *
     * @return exclusive lower line-count bound
     */
    public int getMinLines() {
        return minLines;
    }

    /**
     * Sets the exclusive lower line-count bound.
     *
     * @param minLines exclusive lower bound ({@code 0} disables it)
     */
    public void setMinLines(final int minLines) {
        this.minLines = minLines;
    }

    /**
     * Returns the inclusive upper line-count bound ({@code 0} = no upper bound).
     *
     * @return inclusive upper line-count bound
     */
    public int getMaxLines() {
        return maxLines;
    }

    /**
     * Sets the inclusive upper line-count bound.
     *
     * @param maxLines inclusive upper bound ({@code 0} disables it)
     */
    public void setMaxLines(final int maxLines) {
        this.maxLines = maxLines;
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
}
