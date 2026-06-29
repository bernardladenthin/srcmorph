// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * A composable file-matching condition for a routing rule — a recursive boolean tree.
 *
 * <p>Each node sets <strong>exactly one</strong> of the following (validated by
 * {@link AiConditionEvaluator#validate}):</p>
 * <ul>
 *   <li><b>combinators:</b> {@code and} (all children true), {@code or} (any child true),
 *       {@code not} (single child negated);</li>
 *   <li><b>leaves:</b> {@code extension} (file name ends with any of the listed extensions),
 *       {@code size} ({@link AiRangeCondition} over file bytes), {@code lines}
 *       ({@link AiRangeCondition} over line count), {@code modifiedAfter} / {@code modifiedBefore}
 *       (ISO-8601 instant compared to the file's last-modified time), {@code pathGlob} (glob over the
 *       base-relative path, same syntax as {@code excludes}).</li>
 * </ul>
 *
 * <p>Because every variant is a distinctly named field, the Maven plugin framework binds the tree
 * natively (no {@code implementation=} attributes); nesting is expressed by repeating {@code
 * <condition>} inside {@code <and>}/{@code <or>}/{@code <not>}. New leaf kinds are added by adding a
 * field here and a branch in the evaluator. Mutable JavaBean for the Maven configurator.</p>
 */
@ToString
public class AiCondition {

    private @Nullable AiConditionGroup and;
    private @Nullable AiConditionGroup or;
    private @Nullable AiCondition not;
    private @Nullable List<String> extensions;
    private @Nullable AiRangeCondition size;
    private @Nullable AiRangeCondition lines;
    private @Nullable String modifiedAfter;
    private @Nullable String modifiedBefore;
    private @Nullable String pathGlob;

    /** Creates a new {@link AiCondition}. */
    public AiCondition() {
        // no-op
    }

    /**
     * Returns the AND group, or {@code null} when this is not an AND node.
     *
     * @return the AND group, or {@code null}
     */
    public @Nullable AiConditionGroup getAnd() {
        return and;
    }

    /**
     * Sets the AND group (all children must match).
     *
     * @param and the AND group
     */
    public void setAnd(final @Nullable AiConditionGroup and) {
        this.and = and;
    }

    /**
     * Returns the OR group, or {@code null} when this is not an OR node.
     *
     * @return the OR group, or {@code null}
     */
    public @Nullable AiConditionGroup getOr() {
        return or;
    }

    /**
     * Sets the OR group (any child may match).
     *
     * @param or the OR group
     */
    public void setOr(final @Nullable AiConditionGroup or) {
        this.or = or;
    }

    /**
     * Returns the negated child, or {@code null} when this is not a NOT node.
     *
     * @return the negated child, or {@code null}
     */
    public @Nullable AiCondition getNot() {
        return not;
    }

    /**
     * Sets the negated child.
     *
     * @param not the negated child
     */
    public void setNot(final @Nullable AiCondition not) {
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
    public @Nullable AiRangeCondition getSize() {
        return size;
    }

    /**
     * Sets the size range (bytes).
     *
     * @param size the size range
     */
    public void setSize(final @Nullable AiRangeCondition size) {
        this.size = size;
    }

    /**
     * Returns the line-count range, or {@code null} when this is not a lines node.
     *
     * @return the line range, or {@code null}
     */
    public @Nullable AiRangeCondition getLines() {
        return lines;
    }

    /**
     * Sets the line-count range.
     *
     * @param lines the line range
     */
    public void setLines(final @Nullable AiRangeCondition lines) {
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
}
