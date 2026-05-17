// @formatter:off

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex;

import java.nio.file.Path;

public class AiPathSupport {

    public Path relativizeFromSrc(final Path baseDirectory, final Path path) {
        final Path relative = baseDirectory.relativize(path);
        if (relative.getNameCount() > 0 && "src".equals(relative.getName(0).toString())) {
            return relative.subpath(1, relative.getNameCount());
        }
        return relative;
    }
}
