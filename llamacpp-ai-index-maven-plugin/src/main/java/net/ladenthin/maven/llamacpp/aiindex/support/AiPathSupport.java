// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import java.nio.file.Path;
import lombok.ToString;

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
}
