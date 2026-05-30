// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Path;

/** Deterministic {@link AiGenerationProvider} that returns a mock summary; used for testing. */
public class MockAiGenerationProvider implements AiGenerationProvider {

    /** Creates a new {@link MockAiGenerationProvider}. */
    public MockAiGenerationProvider() {
        // no-op
    }

    @Override
    public String generate(final AiGenerationRequest request) throws IOException {
        final Path file = request.sourceFile();
        final Path fileNamePath = file.getFileName();
        final String fileName = fileNamePath != null ? fileNamePath.toString() : file.toString();
        return "Mock summary for " + fileName;
    }
}
