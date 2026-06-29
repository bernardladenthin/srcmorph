// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import net.ladenthin.maven.llamacpp.aiindex.support.AiSourceExcludeFilter;
import org.jspecify.annotations.Nullable;

/**
 * Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.
 *
 * <p>{@link #matches} walks the tree (and/or/not + leaves); {@link #validate} fails fast on a
 * misconfigured node (not exactly one branch set, empty combinator, unbounded range, unparseable
 * date, blank glob); {@link #usesLines} reports whether any node needs the line count, so the indexer
 * only reads file contents when a {@code lines} condition is actually used.</p>
 */
public final class AiConditionEvaluator {

    /** Message when a node does not set exactly one branch/leaf. */
    private static final String ERROR_ONE_BRANCH =
            "a condition node must set exactly one of and/or/not/extensions/size/lines/"
                    + "modifiedAfter/modifiedBefore/pathGlob: ";

    /** Creates a new {@link AiConditionEvaluator}. */
    public AiConditionEvaluator() {
        // no-op
    }

    /**
     * Returns whether {@code context} satisfies {@code condition}. Assumes the condition was validated
     * via {@link #validate}.
     *
     * @param condition the condition tree
     * @param context   the file facts
     * @return {@code true} if the file matches
     */
    public boolean matches(final AiCondition condition, final AiFileContext context) {
        final AiConditionGroup and = condition.getAnd();
        if (and != null) {
            final List<AiCondition> children = and.getConditions();
            if (children != null) {
                for (final AiCondition child : children) {
                    if (!matches(child, context)) {
                        return false;
                    }
                }
            }
            return true;
        }
        final AiConditionGroup or = condition.getOr();
        if (or != null) {
            final List<AiCondition> children = or.getConditions();
            if (children != null) {
                for (final AiCondition child : children) {
                    if (matches(child, context)) {
                        return true;
                    }
                }
            }
            return false;
        }
        final AiCondition not = condition.getNot();
        if (not != null) {
            return !matches(not, context);
        }
        final List<String> extensions = condition.getExtensions();
        if (extensions != null) {
            return matchesExtension(extensions, context.fileName());
        }
        final AiRangeCondition size = condition.getSize();
        if (size != null) {
            return size.contains(context.sizeBytes());
        }
        final AiRangeCondition lines = condition.getLines();
        if (lines != null) {
            return lines.contains(context.lineCount());
        }
        final String modifiedAfter = condition.getModifiedAfter();
        if (modifiedAfter != null) {
            return context.lastModifiedEpochMilli() > parseEpochMilli(modifiedAfter);
        }
        final String modifiedBefore = condition.getModifiedBefore();
        if (modifiedBefore != null) {
            return context.lastModifiedEpochMilli() < parseEpochMilli(modifiedBefore);
        }
        final String pathGlob = condition.getPathGlob();
        if (pathGlob != null) {
            return globMatches(pathGlob, context.relativePath());
        }
        throw new IllegalStateException(ERROR_ONE_BRANCH + condition);
    }

    /**
     * Validates a condition tree, throwing {@link IllegalArgumentException} on a misconfiguration.
     *
     * @param condition the condition tree
     * @throws IllegalArgumentException if any node is invalid
     */
    public void validate(final AiCondition condition) {
        if (branchCount(condition) != 1) {
            throw new IllegalArgumentException(ERROR_ONE_BRANCH + condition);
        }
        final AiConditionGroup and = condition.getAnd();
        final AiConditionGroup or = condition.getOr();
        final AiCondition not = condition.getNot();
        final List<String> extensions = condition.getExtensions();
        final AiRangeCondition size = condition.getSize();
        final AiRangeCondition lines = condition.getLines();
        final String modifiedAfter = condition.getModifiedAfter();
        final String modifiedBefore = condition.getModifiedBefore();
        final String pathGlob = condition.getPathGlob();
        if (and != null) {
            validateGroup(and, "<and>");
        } else if (or != null) {
            validateGroup(or, "<or>");
        } else if (not != null) {
            validate(not);
        } else if (extensions != null) {
            if (extensions.isEmpty()) {
                throw new IllegalArgumentException("an <extensions> condition must list at least one extension");
            }
        } else if (size != null) {
            if (!size.hasBound()) {
                throw new IllegalArgumentException("a <size> condition must set <min> or <max>");
            }
        } else if (lines != null) {
            if (!lines.hasBound()) {
                throw new IllegalArgumentException("a <lines> condition must set <min> or <max>");
            }
        } else if (modifiedAfter != null) {
            parseEpochMilli(modifiedAfter);
        } else if (modifiedBefore != null) {
            parseEpochMilli(modifiedBefore);
        } else if (pathGlob != null && pathGlob.trim().isEmpty()) {
            throw new IllegalArgumentException("a <pathGlob> condition must not be blank");
        }
    }

    /**
     * Returns whether any node in the tree is a {@code lines} condition, so the indexer knows whether it
     * must read file contents to count lines.
     *
     * @param condition the condition tree
     * @return {@code true} if a {@code lines} condition is present anywhere
     */
    public boolean usesLines(final AiCondition condition) {
        if (condition.getLines() != null) {
            return true;
        }
        final AiConditionGroup and = condition.getAnd();
        if (and != null) {
            return anyUsesLines(and.getConditions());
        }
        final AiConditionGroup or = condition.getOr();
        if (or != null) {
            return anyUsesLines(or.getConditions());
        }
        final AiCondition not = condition.getNot();
        if (not != null) {
            return usesLines(not);
        }
        return false;
    }

    private boolean anyUsesLines(final @Nullable List<AiCondition> children) {
        if (children == null) {
            return false;
        }
        for (final AiCondition child : children) {
            if (usesLines(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates an {@code <and>}/{@code <or>} group: it must hold at least one child, and each child is
     * validated recursively.
     *
     * @param group the combinator group
     * @param label the element label for error messages
     */
    private void validateGroup(final AiConditionGroup group, final String label) {
        final List<AiCondition> children = group.getConditions();
        if (children == null || children.isEmpty()) {
            throw new IllegalArgumentException("an " + label + " condition must have at least one child");
        }
        for (final AiCondition child : children) {
            validate(child);
        }
    }

    /**
     * Returns how many branch/leaf fields the node sets (must be exactly one).
     *
     * @param c the condition node
     * @return the number of non-null branch/leaf fields
     */
    private int branchCount(final AiCondition c) {
        int count = 0;
        if (c.getAnd() != null) {
            count++;
        }
        if (c.getOr() != null) {
            count++;
        }
        if (c.getNot() != null) {
            count++;
        }
        if (c.getExtensions() != null) {
            count++;
        }
        if (c.getSize() != null) {
            count++;
        }
        if (c.getLines() != null) {
            count++;
        }
        if (c.getModifiedAfter() != null) {
            count++;
        }
        if (c.getModifiedBefore() != null) {
            count++;
        }
        if (c.getPathGlob() != null) {
            count++;
        }
        return count;
    }

    private boolean matchesExtension(final List<String> extensions, final String fileName) {
        for (final @Nullable String extension : extensions) {
            if (extension != null && fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean globMatches(final String glob, final String relativePath) {
        return new AiSourceExcludeFilter(Collections.singletonList(glob)).isExcluded(relativePath);
    }

    /**
     * Parses an ISO-8601 instant (e.g. {@code 2026-01-01T00:00:00Z}) to epoch milliseconds.
     *
     * @param isoInstant the ISO-8601 instant
     * @return epoch milliseconds
     * @throws IllegalArgumentException if the value is not a valid ISO-8601 instant
     */
    private long parseEpochMilli(final String isoInstant) {
        try {
            return Instant.parse(isoInstant).toEpochMilli();
        } catch (final DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "invalid ISO-8601 instant (expected e.g. 2026-01-01T00:00:00Z): " + isoInstant, e);
        }
    }
}
