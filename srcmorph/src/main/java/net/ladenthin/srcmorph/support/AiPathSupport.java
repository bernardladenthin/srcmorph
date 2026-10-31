// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/** Path utilities for resolving and trimming source paths used during indexing. */
@ToString
public class AiPathSupport {

    /** Creates a new {@link AiPathSupport}. */
    public AiPathSupport() {
        // no-op
    }

    /** Leading path segment stripped from a relativised source path. */
    private static final String SRC_DIRECTORY_NAME = "src";

    /**
     * Relativises {@code path} against {@code baseDirectory} and strips a leading
     * {@code src} segment when present.
     *
     * @param baseDirectory base directory to relativise against
     * @param path          path to relativise
     * @return relativised path with any leading {@code src} segment removed
     */
    public Path relativizeFromSrc(final Path baseDirectory, final Path path) {
        final Path relative = baseDirectory.relativize(path);
        // Path#startsWith compares whole name elements, so it is false for the empty
        // relativised path and needs no separate getNameCount() guard before subpath().
        if (relative.startsWith(SRC_DIRECTORY_NAME)) {
            return relative.subpath(1, relative.getNameCount());
        }
        return relative;
    }

    /**
     * Resolves a configured path against a directory: a relative {@code path} is taken inside
     * {@code directory}, one with a root of its own (absolute, or on Windows root- or drive-relative)
     * is used as it is. Without a directory the path stays relative to the working directory of the
     * JVM.
     *
     * @param directory directory relative paths are resolved against, or {@code null} to leave them
     *                  relative to the working directory
     * @param path      the configured path, or {@code null}
     * @return the path to use, or {@code null} when {@code path} is {@code null}
     */
    public static @Nullable String resolveAgainst(final @Nullable File directory, final @Nullable String path) {
        if (directory == null || path == null || hasRoot(path)) {
            return path;
        }
        return new File(directory, path).getPath();
    }

    /**
     * Whether {@code path} carries a root of its own: absolute, or on Windows root- or drive-relative.
     *
     * @param path the configured path
     * @return {@code true} if the path has a root, or cannot be parsed as a path at all
     */
    private static boolean hasRoot(final String path) {
        try {
            return Paths.get(path).getRoot() != null;
        } catch (final InvalidPathException e) {
            return true;
        }
    }
}
