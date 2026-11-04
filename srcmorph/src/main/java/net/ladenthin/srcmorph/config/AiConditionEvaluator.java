// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.ladenthin.srcmorph.support.AiGlob;
import net.ladenthin.srcmorph.support.AiPathSupport;
import net.ladenthin.srcmorph.support.AiShebang;
import net.ladenthin.srcmorph.support.AiSourceExcludeFilter;
import org.jspecify.annotations.Nullable;

/**
 * Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.
 *
 * <p>{@link #matches} walks the tree (and/or/not + leaves); {@link #validate} fails fast on a
 * misconfigured node (not exactly one branch set, empty combinator, unbounded range, unparseable
 * date, blank or unclosed glob, invalid regular expression); {@link #usesLines} and {@link #usesContent}
 * report whether any node needs the line count or the start of the text, so the indexer only reads
 * file contents when a {@code lines}, {@code contentMatches} or {@code interpreters} condition is
 * actually used.</p>
 */
public final class AiConditionEvaluator {

    /**
     * How many characters of a file a {@code contentMatches} condition reads unless the configuration's
     * {@code contentHeadChars} says otherwise: enough for the header of a Kubernetes manifest, an OpenAPI
     * document or a Camel route, and cheap for a large file.
     */
    public static final int CONTENT_HEAD_CHARS = 16_384;

    /** Message when a node does not set exactly one branch/leaf. */
    private static final String ERROR_ONE_BRANCH =
            "a condition node must set exactly one of and/or/not/extensions/size/lines/"
                    + "modifiedAfter/modifiedBefore/pathGlob/fileNames/contentMatches/interpreters: ";

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
        final List<String> fileNames = condition.getFileNames();
        if (fileNames != null) {
            return matchesFileName(fileNames, context.fileName());
        }
        final String contentMatches = condition.getContentMatches();
        if (contentMatches != null) {
            return Pattern.compile(contentMatches).matcher(context.head()).find();
        }
        final List<String> interpreters = condition.getInterpreters();
        if (interpreters != null) {
            final String interpreter = AiShebang.interpreter(context.head());
            return interpreter != null && AiShebang.matches(interpreter, interpreters);
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
        final List<String> fileNames = condition.getFileNames();
        final String contentMatches = condition.getContentMatches();
        final List<String> interpreters = condition.getInterpreters();
        if (and != null) {
            validateGroup(and, "and");
        } else if (or != null) {
            validateGroup(or, "or");
        } else if (not != null) {
            validate(not);
        } else if (extensions != null) {
            if (extensions.isEmpty()) {
                throw new IllegalArgumentException("an extensions condition must list at least one extension");
            }
        } else if (size != null) {
            if (!size.hasBound()) {
                throw new IllegalArgumentException("a size condition must set min or max");
            }
        } else if (lines != null) {
            if (!lines.hasBound()) {
                throw new IllegalArgumentException("a lines condition must set min or max");
            }
        } else if (modifiedAfter != null) {
            parseEpochMilli(modifiedAfter);
        } else if (modifiedBefore != null) {
            parseEpochMilli(modifiedBefore);
        } else if (pathGlob != null) {
            if (pathGlob.trim().isEmpty()) {
                throw new IllegalArgumentException("a pathGlob condition must not be blank");
            }
            AiGlob.compile(pathGlob.trim(), false);
        } else if (fileNames != null) {
            validateFileNames(fileNames);
        } else if (contentMatches != null) {
            validateContentMatches(contentMatches);
        } else if (interpreters != null) {
            validateInterpreters(interpreters);
        }
    }

    /**
     * Validates the names of an {@code interpreters} condition: at least one, none blank, each a name
     * without its directory.
     *
     * @param interpreters the names
     * @throws IllegalArgumentException if a name is blank or a path
     */
    public static void validateInterpreters(final List<String> interpreters) {
        if (interpreters.isEmpty()) {
            throw new IllegalArgumentException("an interpreters condition must list at least one interpreter");
        }
        for (final @Nullable String name : interpreters) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("an interpreters condition must not list a blank interpreter");
            }
            if (name.indexOf('/') >= 0) {
                throw new IllegalArgumentException(
                        "an interpreter is named without its directory (bash, not /bin/bash): '" + name + "'");
            }
        }
    }

    private static void validateFileNames(final List<String> fileNames) {
        if (fileNames.isEmpty()) {
            throw new IllegalArgumentException("a fileNames condition must list at least one name");
        }
        for (final @Nullable String name : fileNames) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("a fileNames condition must not list a blank name");
            }
            if (name.indexOf('/') >= 0) {
                throw new IllegalArgumentException("a fileNames condition matches the name without its directory, not '"
                        + name + "'; use pathGlob");
            }
            AiGlob.compile(name.trim(), true);
        }
    }

    private static void validateContentMatches(final String contentMatches) {
        if (contentMatches.isEmpty()) {
            throw new IllegalArgumentException("a contentMatches condition must not be empty");
        }
        try {
            Pattern.compile(contentMatches);
        } catch (final PatternSyntaxException e) {
            throw new IllegalArgumentException(
                    "a contentMatches condition is no Java regular expression: " + e.getMessage(), e);
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
        return uses(condition, node -> node.getLines() != null);
    }

    /**
     * Returns whether any node in the tree is a {@code contentMatches} or {@code interpreters} condition,
     * so the indexer knows whether it must read the start of each file.
     *
     * @param condition the condition tree
     * @return {@code true} if a {@code contentMatches} or {@code interpreters} condition is present
     *     anywhere
     */
    public boolean usesContent(final AiCondition condition) {
        return uses(condition, node -> node.getContentMatches() != null || node.getInterpreters() != null);
    }

    private boolean uses(final AiCondition condition, final Predicate<AiCondition> leaf) {
        if (leaf.test(condition)) {
            return true;
        }
        final AiConditionGroup and = condition.getAnd();
        if (and != null) {
            return anyUses(and.getConditions(), leaf);
        }
        final AiConditionGroup or = condition.getOr();
        if (or != null) {
            return anyUses(or.getConditions(), leaf);
        }
        final AiCondition not = condition.getNot();
        if (not != null) {
            return uses(not, leaf);
        }
        return false;
    }

    private boolean anyUses(final @Nullable List<AiCondition> children, final Predicate<AiCondition> leaf) {
        if (children == null) {
            return false;
        }
        for (final AiCondition child : children) {
            if (uses(child, leaf)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates an {@code and}/{@code or} group: it must hold at least one child, and each child is
     * validated recursively.
     *
     * @param group the combinator group
     * @param label the key of the combinator, for error messages
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
        if (c.getFileNames() != null) {
            count++;
        }
        if (c.getContentMatches() != null) {
            count++;
        }
        if (c.getInterpreters() != null) {
            count++;
        }
        return count;
    }

    private boolean matchesExtension(final List<String> extensions, final String fileName) {
        for (final @Nullable String extension : extensions) {
            if (extension != null && AiPathSupport.hasExtension(fileName, extension)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesFileName(final List<String> globs, final String fileName) {
        for (final @Nullable String glob : globs) {
            if (glob != null
                    && AiGlob.compile(glob.trim(), true).matcher(fileName).matches()) {
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
