// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Path;

public class MockAiGenerationProvider implements AiGenerationProvider {

    @Override
    public String generate(final AiGenerationRequest request) throws IOException {
        final Path file = request.sourceFile();
        final Path fileNamePath = file.getFileName();
        final String fileName = fileNamePath != null ? fileNamePath.toString() : file.toString();
        return "Mock summary for " + fileName;
    }
}