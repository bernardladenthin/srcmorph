// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Selects the routing rule ({@link AiFieldGenerationConfig}) that applies to a given source file.
 *
 * <p>Each rule carries optional filters — {@link AiFieldGenerationConfig#getFileExtensions() file
 * extensions}, {@link AiFieldGenerationConfig#getMinFileSizeBytes() file size} and
 * {@link AiFieldGenerationConfig#getMinLines() line count} — plus a
 * {@link AiFieldGenerationConfig#getPriority() priority}, and is one of three kinds: a normal
 * <em>route</em> rule (prompt + model), an explicit {@link AiFieldGenerationConfig#isFallback()
 * fallback}, or a {@link AiFieldGenerationConfig#isSkip() skip} rule.</p>
 *
 * <p>Selection rule:</p>
 * <ol>
 *   <li>Among all non-fallback rules whose <em>every</em> declared filter matches the file, the one
 *       with the highest priority wins (ties broken by declaration order — the earlier rule wins).</li>
 *   <li>If the winner is a skip rule, the file is excluded from indexing.</li>
 *   <li>If no non-fallback rule matches, the explicit fallback applies.</li>
 *   <li>If nothing matches and no fallback is configured, {@link #select} returns {@code null} so the
 *       caller can fail loudly rather than silently skip the file.</li>
 * </ol>
 *
 * <p>Within one rule an unset bound is ignored. Size and line bounds use an <em>exclusive</em> lower
 * bound and an <em>inclusive</em> upper bound, so adjacent bands are written as
 * {@code band2.min == band1.max} (a file exactly on the boundary belongs to the lower band).</p>
 */
@ToString
public final class AiFieldGenerationSelector {

    /** Creates a new {@link AiFieldGenerationSelector}. */
    public AiFieldGenerationSelector() {
        // no-op
    }

    /**
     * Returns the rule that applies to a file, matching on extension, size and line count and resolving
     * ties by priority then declaration order.
     *
     * @param configs       the configured rules, in declaration order; {@code null} entries are skipped
     * @param fileName      the source file name (e.g. {@code Foo.java})
     * @param fileSizeBytes the source file size in bytes (used for the size filter)
     * @param lineCount     the source line count (used for the line filter)
     * @return the winning rule (which may be a skip or the fallback), or {@code null} when nothing
     *         matches and no fallback is configured
     */
    public @Nullable AiFieldGenerationConfig select(
            final Iterable<AiFieldGenerationConfig> configs,
            final String fileName,
            final long fileSizeBytes,
            final int lineCount) {
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
            if (matchesExtension(config, fileName)
                    && matchesSize(config, fileSizeBytes)
                    && matchesLines(config, lineCount)
                    && (best == null || config.getPriority() > best.getPriority())) {
                best = config;
            }
        }
        return best != null ? best : fallback;
    }

    /**
     * Validates a rule set, throwing {@link IllegalArgumentException} on a misconfiguration so the build
     * fails fast with a clear message rather than mis-routing or silently skipping files.
     *
     * <p>Checks: at most one fallback; a non-fallback rule must declare at least one filter; a fallback
     * must not also be a skip; route rules (and the fallback) must have a prompt key and an AI
     * definition key.</p>
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
            if (config.isFallback()) {
                fallbackCount++;
                if (config.isSkip()) {
                    throw new IllegalArgumentException("A fallback rule cannot also be a skip rule: " + config);
                }
                requireRouteKeys(config);
            } else {
                if (!hasFilter(config)) {
                    throw new IllegalArgumentException(
                            "A non-fallback rule must declare at least one filter (extension/size/lines): " + config);
                }
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
     * Returns {@code true} when the rule declares at least one filter (extension, size or line bound).
     *
     * @param config the rule
     * @return {@code true} if any filter is set
     */
    private boolean hasFilter(final AiFieldGenerationConfig config) {
        final List<String> extensions = config.getFileExtensions();
        final boolean hasExtensions = extensions != null && !extensions.isEmpty();
        return hasExtensions
                || config.getMinFileSizeBytes() > 0
                || config.getMaxFileSizeBytes() > 0
                || config.getMinLines() > 0
                || config.getMaxLines() > 0;
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

    /**
     * Returns {@code true} when the rule's extension filter matches the file name (or is unset).
     *
     * @param config   the rule
     * @param fileName the source file name
     * @return {@code true} if the extension filter is unset or one of its extensions matches
     */
    private boolean matchesExtension(final AiFieldGenerationConfig config, final String fileName) {
        final List<String> extensions = config.getFileExtensions();
        if (extensions == null || extensions.isEmpty()) {
            return true;
        }
        for (final String extension : extensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} when the file size is within the rule's bounds (exclusive lower, inclusive
     * upper; each disabled when {@code <= 0}).
     *
     * @param config        the rule
     * @param fileSizeBytes the source file size in bytes
     * @return {@code true} if the size is within the configured band
     */
    private boolean matchesSize(final AiFieldGenerationConfig config, final long fileSizeBytes) {
        final long min = config.getMinFileSizeBytes();
        final long max = config.getMaxFileSizeBytes();
        if (min > 0 && fileSizeBytes <= min) {
            return false;
        }
        if (max > 0 && fileSizeBytes > max) {
            return false;
        }
        return true;
    }

    /**
     * Returns {@code true} when the line count is within the rule's bounds (exclusive lower, inclusive
     * upper; each disabled when {@code <= 0}).
     *
     * @param config    the rule
     * @param lineCount the source line count
     * @return {@code true} if the line count is within the configured band
     */
    private boolean matchesLines(final AiFieldGenerationConfig config, final int lineCount) {
        final int min = config.getMinLines();
        final int max = config.getMaxLines();
        if (min > 0 && lineCount <= min) {
            return false;
        }
        if (max > 0 && lineCount > max) {
            return false;
        }
        return true;
    }
}
