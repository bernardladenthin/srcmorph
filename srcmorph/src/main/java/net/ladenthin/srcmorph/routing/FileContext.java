// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.routing;

import lombok.ToString;
import net.ladenthin.srcmorph.config.RuleCondition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;

/**
 * Immutable snapshot of the file facts a {@link RuleCondition} is evaluated against: name, base-relative
 * path ({@code /} separators), size (the source's UTF-8 length with {@code
 * } line endings), line count, and last-modified time. Built by the indexer
 * (the I/O side) and consumed by {@link ConditionEvaluator} (the pure side).
 */
@ToString
public final class FileContext {

    private final String fileName;
    private final String relativePath;
    private final long sizeBytes;
    private final int lineCount;
    private final long lastModifiedEpochMilli;

    @ToString.Exclude
    private final String head;

    /**
     * Creates a file context.
     *
     * @param fileName               the file name (e.g. {@code Foo.java})
     * @param relativePath           the base-relative path with {@code /} separators
     * @param sizeBytes              the file size in bytes
     * @param lineCount              the line count ({@code 0} when not computed)
     * @param lastModifiedEpochMilli the last-modified time in epoch milliseconds
     */
    public FileContext(
            final String fileName,
            final String relativePath,
            final long sizeBytes,
            final int lineCount,
            final long lastModifiedEpochMilli) {
        this(fileName, relativePath, sizeBytes, lineCount, lastModifiedEpochMilli, "");
    }

    /**
     * Creates a file context with the start of the file's text, for a {@code contentMatches} condition.
     *
     * @param fileName               the file name (e.g. {@code Foo.java})
     * @param relativePath           the base-relative path with {@code /} separators
     * @param sizeBytes              the file size in bytes
     * @param lineCount              the line count ({@code 0} when not computed)
     * @param lastModifiedEpochMilli the last-modified time in epoch milliseconds
     * @param head                   the first {@value SrcMorphConfiguration#DEFAULT_CONTENT_HEAD_CHARS} characters
     *                               of the text, or {@code ""} when not read
     */
    public FileContext(
            final String fileName,
            final String relativePath,
            final long sizeBytes,
            final int lineCount,
            final long lastModifiedEpochMilli,
            final String head) {
        this.fileName = fileName;
        this.relativePath = relativePath;
        this.sizeBytes = sizeBytes;
        this.lineCount = lineCount;
        this.lastModifiedEpochMilli = lastModifiedEpochMilli;
        this.head = head;
    }

    /**
     * Returns the file name.
     *
     * @return the file name
     */
    public String fileName() {
        return fileName;
    }

    /**
     * Returns the base-relative path with {@code /} separators.
     *
     * @return the relative path
     */
    public String relativePath() {
        return relativePath;
    }

    /**
     * Returns the file size in bytes.
     *
     * @return the size in bytes
     */
    public long sizeBytes() {
        return sizeBytes;
    }

    /**
     * Returns the line count.
     *
     * @return the line count
     */
    public int lineCount() {
        return lineCount;
    }

    /**
     * Returns the last-modified time in epoch milliseconds.
     *
     * @return the last-modified epoch milliseconds
     */
    public long lastModifiedEpochMilli() {
        return lastModifiedEpochMilli;
    }

    /**
     * Returns the start of the file's text.
     *
     * @return the first {@value SrcMorphConfiguration#DEFAULT_CONTENT_HEAD_CHARS} characters, or {@code ""} when
     *         no rule reads the content
     */
    public String head() {
        return head;
    }
}
