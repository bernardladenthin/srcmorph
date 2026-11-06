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
public class SourcePaths {

    /** Creates a new {@link SourcePaths}. */
    public SourcePaths() {
        // no-op
    }

    /** Leading path segment stripped from a relativised source path. */
    private static final String SRC_DIRECTORY_NAME = "src";

    /** The dot an extension starts with. */
    private static final String EXTENSION_DOT = ".";

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
        // the first name element compared as a string, case-sensitively: Path#startsWith ignores the
        // case on Windows, so a leading Src/ would be stripped there and kept on Linux. The empty
        // relativised path has one empty name, which is not src; src itself (a subtree "src") is the
        // empty path, since subpath() needs a name to keep
        if (!SRC_DIRECTORY_NAME.equals(relative.getName(0).toString())) {
            return relative;
        }
        return relative.getNameCount() == 1
                ? relative.getFileSystem().getPath("")
                : relative.subpath(1, relative.getNameCount());
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
     * Whether a file name has an extension: it ends with the extension, which is read with its leading
     * dot ({@code "java"} as {@code ".java"}, so {@code "md"} does not match {@code README.cmd}) and
     * in any case ({@code Foo.JAVA} is a {@code .java} file). A blank extension matches nothing.
     *
     * @param fileName  the file name
     * @param extension the configured extension
     * @return {@code true} when the file name ends with it
     */
    public static boolean hasExtension(final String fileName, final String extension) {
        final String trimmed = extension.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        final String dotted = trimmed.startsWith(EXTENSION_DOT) ? trimmed : EXTENSION_DOT + trimmed;
        final int length = dotted.length();
        return fileName.regionMatches(true, fileName.length() - length, dotted, 0, length);
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
