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
 * {@link AiGlob} ({@code *}, {@code **}, {@code ?} and <code>{a,b}</code>).</p>
 *
 * <p>Matching is case-sensitive and anchored: the whole relative path must match. An empty (or
 * {@code null}) pattern list excludes nothing, preserving the historical "index everything" behaviour.</p>
 */
@ToString
public final class AiSourceExcludeFilter {

    private final List<Pattern> patterns;

    /**
     * Creates a filter from the given glob patterns.
     *
     * @param globPatterns exclude globs; {@code null} or empty means "exclude nothing". Individual
     *                     {@code null} or blank entries are ignored.
     * @throws IllegalArgumentException if a pattern opens a <code>{</code> it does not close
     */
    public AiSourceExcludeFilter(final @Nullable Collection<String> globPatterns) {
        final List<Pattern> compiled = new ArrayList<>();
        if (globPatterns != null) {
            for (final String glob : globPatterns) {
                if (glob == null || glob.trim().isEmpty()) {
                    continue;
                }
                compiled.add(AiGlob.compile(glob.trim(), false));
            }
        }
        this.patterns = compiled;
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
}
