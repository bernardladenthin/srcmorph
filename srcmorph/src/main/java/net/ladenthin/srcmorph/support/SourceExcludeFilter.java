// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Matches source paths against a list of glob exclude patterns so the indexer can skip trivial or
 * generated files (for example {@code package-info.java}, {@code module-info.java}, or generated
 * sources) and keep the index focused on meaningful code.
 *
 * <p>Patterns are matched against the file path <em>relative to the project base directory</em>,
 * always with {@code /} separators, so the same pattern works on Windows and POSIX, in the syntax of
 * {@link Glob} ({@code *}, {@code **}, {@code ?} and <code>{a,b}</code>).</p>
 *
 * <p>Matching is case-sensitive and anchored: the whole relative path must match. An empty (or
 * {@code null}) pattern list excludes nothing, preserving the historical "index everything" behaviour.</p>
 */
@ToString
public final class SourceExcludeFilter {

    /**
     * The end of an exclude that takes a directory as a whole: {@code target/**} excludes every file
     * below each directory {@code target} matches ({@link #isExcludedDirectory(String)}).
     */
    static final String WHOLE_DIRECTORY_SUFFIX = "/**";

    private final List<Pattern> patterns;

    /**
     * The patterns of the directories an exclude ending in {@link #WHOLE_DIRECTORY_SUFFIX} takes as a
     * whole: the exclude without that end.
     */
    private final List<Pattern> directoryPatterns;

    /**
     * Creates a filter from the given glob patterns.
     *
     * @param globPatterns exclude globs; {@code null} or empty means "exclude nothing". Individual
     *                     {@code null} or blank entries are ignored.
     * @throws IllegalArgumentException if a pattern opens a <code>{</code> it does not close
     */
    public SourceExcludeFilter(final @Nullable Collection<String> globPatterns) {
        final List<Pattern> compiled = new ArrayList<>();
        final List<Pattern> directories = new ArrayList<>();
        if (globPatterns != null) {
            for (final String glob : globPatterns) {
                if (glob == null || glob.trim().isEmpty()) {
                    continue;
                }
                final String trimmed = glob.trim();
                compiled.add(Glob.compile(trimmed, false));
                // a valid glob that ends in /** closes every alternation before it, so its start is a glob too
                // (an empty one, of the glob /**, matches only the base directory, which is never taken)
                if (trimmed.endsWith(WHOLE_DIRECTORY_SUFFIX)) {
                    directories.add(Glob.compile(
                            trimmed.substring(0, trimmed.length() - WHOLE_DIRECTORY_SUFFIX.length()), false));
                }
            }
        }
        this.patterns = compiled;
        this.directoryPatterns = directories;
    }

    /**
     * Returns {@code true} when {@code relativePath} matches any configured exclude pattern.
     *
     * @param relativePath the source path relative to the project base directory, using {@code /}
     *                     separators
     * @return {@code true} if the path should be excluded from indexing
     */
    public boolean isExcluded(final String relativePath) {
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(relativePath).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} when an exclude takes {@code relativeDirectory} as a whole, so that every file
     * below it is excluded and a walk need not enter it: an exclude {@code <glob>/**} whose {@code <glob>}
     * matches the directory ({@code target/**} takes {@code target}, {@code **}{@code /generated/**} takes
     * every directory {@code generated}). Any other exclude is decided file by file
     * ({@link #isExcluded(String)}), and the base directory itself (the empty path) is never taken.
     *
     * @param relativeDirectory the directory relative to the project base directory, using {@code /}
     *                          separators
     * @return {@code true} if nothing below the directory is indexed
     */
    public boolean isExcludedDirectory(final String relativeDirectory) {
        if (relativeDirectory.isEmpty()) {
            return false;
        }
        for (final Pattern pattern : directoryPatterns) {
            if (pattern.matcher(relativeDirectory).matches()) {
                return true;
            }
        }
        return false;
    }
}
