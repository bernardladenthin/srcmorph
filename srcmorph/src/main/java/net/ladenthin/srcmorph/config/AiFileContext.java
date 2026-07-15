// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import lombok.ToString;

/**
 * Immutable snapshot of the file facts a {@link AiCondition} is evaluated against: name, base-relative
 * path ({@code /} separators), size in bytes, line count, and last-modified time. Built by the indexer
 * (the I/O side) and consumed by {@link AiConditionEvaluator} (the pure side).
 */
@ToString
public final class AiFileContext {

    private final String fileName;
    private final String relativePath;
    private final long sizeBytes;
    private final int lineCount;
    private final long lastModifiedEpochMilli;

    /**
     * Creates a file context.
     *
     * @param fileName               the file name (e.g. {@code Foo.java})
     * @param relativePath           the base-relative path with {@code /} separators
     * @param sizeBytes              the file size in bytes
     * @param lineCount              the line count ({@code 0} when not computed)
     * @param lastModifiedEpochMilli the last-modified time in epoch milliseconds
     */
    public AiFileContext(
            final String fileName,
            final String relativePath,
            final long sizeBytes,
            final int lineCount,
            final long lastModifiedEpochMilli) {
        this.fileName = fileName;
        this.relativePath = relativePath;
        this.sizeBytes = sizeBytes;
        this.lineCount = lineCount;
        this.lastModifiedEpochMilli = lastModifiedEpochMilli;
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
}
