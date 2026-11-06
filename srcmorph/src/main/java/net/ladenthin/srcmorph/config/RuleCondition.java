// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * A composable file-matching condition for a routing rule — a recursive boolean tree.
 *
 * <p>Each node sets <strong>exactly one</strong> of the following (validated by
 * {@link net.ladenthin.srcmorph.routing.ConditionEvaluator#validate}):</p>
 * <ul>
 *   <li><b>combinators:</b> {@code and} (all children true), {@code or} (any child true),
 *       {@code not} (single child negated);</li>
 *   <li><b>leaves:</b> {@code extensions} (file name ends with any of the listed extensions),
 *       {@code size} ({@link RangeCondition} over file bytes), {@code lines}
 *       ({@link RangeCondition} over line count), {@code modifiedAfter} / {@code modifiedBefore}
 *       (ISO-8601 instant compared to the file's last-modified time), {@code pathGlob} (glob over the
 *       base-relative path, same syntax as {@code excludes}), {@code fileNames} (globs over the file
 *       name alone, in any case: {@code Dockerfile}, {@code Jenkinsfile}, {@code *.gradle.kts}),
 *       {@code contentMatches} (a Java regular expression found in the first characters of the file,
 *       {@code contentHeadChars} of the configuration, {@value SrcMorphConfiguration#DEFAULT_CONTENT_HEAD_CHARS}
 *       by default), {@code interpreters} (the interpreter the file's {@code #!} line names, or a
 *       version of it: {@code bash}, {@code perl}, {@code python}; see
 *       {@link net.ladenthin.srcmorph.support.Shebang}).</li>
 * </ul>
 *
 * <p>Because every variant is a distinctly named field, the configuration file binds the tree
 * without type hints; nesting is a condition under {@code not}, or a list of conditions under
 * {@code and}/{@code or} ({@link ConditionGroup}). New leaf kinds are added by adding a field here
 * and a branch in the evaluator. Mutable JavaBean, bound through its setters.</p>
 */
@ToString
public class RuleCondition {

    private @Nullable ConditionGroup and;
    private @Nullable ConditionGroup or;
    private @Nullable RuleCondition not;
    private @Nullable List<String> extensions;
    private @Nullable RangeCondition size;
    private @Nullable RangeCondition lines;
    private @Nullable String modifiedAfter;
    private @Nullable String modifiedBefore;
    private @Nullable String pathGlob;
    private @Nullable List<String> fileNames;
    private @Nullable String contentMatches;
    private @Nullable List<String> interpreters;

    /** Creates a new {@link RuleCondition}. */
    public RuleCondition() {
        // no-op
    }

    /**
     * Returns the AND group, or {@code null} when this is not an AND node.
     *
     * @return the AND group, or {@code null}
     */
    public @Nullable ConditionGroup getAnd() {
        return and;
    }

    /**
     * Sets the AND group (all children must match).
     *
     * @param and the AND group
     */
    public void setAnd(final @Nullable ConditionGroup and) {
        this.and = and;
    }

    /**
     * Returns the OR group, or {@code null} when this is not an OR node.
     *
     * @return the OR group, or {@code null}
     */
    public @Nullable ConditionGroup getOr() {
        return or;
    }

    /**
     * Sets the OR group (any child may match).
     *
     * @param or the OR group
     */
    public void setOr(final @Nullable ConditionGroup or) {
        this.or = or;
    }

    /**
     * Returns the negated child, or {@code null} when this is not a NOT node.
     *
     * @return the negated child, or {@code null}
     */
    public @Nullable RuleCondition getNot() {
        return not;
    }

    /**
     * Sets the negated child.
     *
     * @param not the negated child
     */
    public void setNot(final @Nullable RuleCondition not) {
        this.not = not;
    }

    /**
     * Returns the matching extensions, or {@code null} when this is not an extensions node.
     *
     * @return the extensions, or {@code null}
     */
    public @Nullable List<String> getExtensions() {
        return extensions;
    }

    /**
     * Sets the matching extensions.
     *
     * @param extensions the extensions (e.g. {@code .java})
     */
    public void setExtensions(final @Nullable Collection<String> extensions) {
        this.extensions = extensions != null ? new ArrayList<>(extensions) : null;
    }

    /**
     * Returns the size range, or {@code null} when this is not a size node.
     *
     * @return the size range, or {@code null}
     */
    public @Nullable RangeCondition getSize() {
        return size;
    }

    /**
     * Sets the size range: the source's length in UTF-8 bytes as srcmorph reads it ({@code
     * } line
     * endings, no byte order mark), so a CRLF and an LF checkout of a file reach the same rule.
     *
     * @param size the size range
     */
    public void setSize(final @Nullable RangeCondition size) {
        this.size = size;
    }

    /**
     * Returns the line-count range, or {@code null} when this is not a lines node.
     *
     * @return the line range, or {@code null}
     */
    public @Nullable RangeCondition getLines() {
        return lines;
    }

    /**
     * Sets the line-count range.
     *
     * @param lines the line range
     */
    public void setLines(final @Nullable RangeCondition lines) {
        this.lines = lines;
    }

    /**
     * Returns the ISO-8601 instant the file must be modified after, or {@code null}.
     *
     * @return the lower modified bound, or {@code null}
     */
    public @Nullable String getModifiedAfter() {
        return modifiedAfter;
    }

    /**
     * Sets the ISO-8601 instant the file must be modified after.
     *
     * @param modifiedAfter the ISO-8601 instant (e.g. {@code 2026-01-01T00:00:00Z})
     */
    public void setModifiedAfter(final @Nullable String modifiedAfter) {
        this.modifiedAfter = modifiedAfter;
    }

    /**
     * Returns the ISO-8601 instant the file must be modified before, or {@code null}.
     *
     * @return the upper modified bound, or {@code null}
     */
    public @Nullable String getModifiedBefore() {
        return modifiedBefore;
    }

    /**
     * Sets the ISO-8601 instant the file must be modified before.
     *
     * @param modifiedBefore the ISO-8601 instant
     */
    public void setModifiedBefore(final @Nullable String modifiedBefore) {
        this.modifiedBefore = modifiedBefore;
    }

    /**
     * Returns the base-relative path glob, or {@code null} when this is not a pathGlob node.
     *
     * @return the path glob, or {@code null}
     */
    public @Nullable String getPathGlob() {
        return pathGlob;
    }

    /**
     * Sets the base-relative path glob.
     *
     * @param pathGlob the glob (e.g. {@code **}{@code /generated/**})
     */
    public void setPathGlob(final @Nullable String pathGlob) {
        this.pathGlob = pathGlob;
    }

    /**
     * Returns the file-name globs, or {@code null} when this is not a fileNames node.
     *
     * @return the globs, or {@code null}
     */
    public @Nullable List<String> getFileNames() {
        return fileNames;
    }

    /**
     * Sets the file-name globs: the node matches a file whose name, without its directory, matches one
     * of them in any case.
     *
     * @param fileNames the globs (e.g. {@code Dockerfile}, {@code Dockerfile.*})
     */
    public void setFileNames(final @Nullable Collection<String> fileNames) {
        this.fileNames = fileNames != null ? new ArrayList<>(fileNames) : null;
    }

    /**
     * Returns the content pattern, or {@code null} when this is not a contentMatches node.
     *
     * @return the Java regular expression, or {@code null}
     */
    public @Nullable String getContentMatches() {
        return contentMatches;
    }

    /**
     * Sets the content pattern: the node matches a file in whose first characters (the configuration's
     * {@code contentHeadChars}) the Java regular expression is found.
     *
     * @param contentMatches the regular expression (e.g. {@code (?m)^apiVersion:})
     */
    public void setContentMatches(final @Nullable String contentMatches) {
        this.contentMatches = contentMatches;
    }

    /**
     * Returns the interpreter names, or {@code null} when this is not an interpreters node.
     *
     * @return the names, or {@code null}
     */
    public @Nullable List<String> getInterpreters() {
        return interpreters;
    }

    /**
     * Sets the interpreter names: the node matches a file whose {@code #!} line names one of them or a
     * version of one.
     *
     * @param interpreters the names (e.g. {@code bash}, {@code sh}, {@code python})
     */
    public void setInterpreters(final @Nullable Collection<String> interpreters) {
        this.interpreters = interpreters != null ? new ArrayList<>(interpreters) : null;
    }
}
