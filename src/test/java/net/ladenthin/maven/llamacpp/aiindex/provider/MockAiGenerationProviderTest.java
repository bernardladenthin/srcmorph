// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader;
import org.junit.jupiter.api.Test;

public class MockAiGenerationProviderTest {

    private static final AiMdHeader HEADER = new AiMdHeader(
            "Foo.java", "1.0", "C", "2026-01-01T00:00:00Z", "2026-01-01T00:00:10Z", "0.1.0", "1.0.0", "file");

    private final MockAiGenerationProvider provider = new MockAiGenerationProvider();

    @Test
    public void generateUsesFileNameOnly() throws IOException {
        AiGenerationRequest request =
                new AiGenerationRequest("summary", Paths.get("a", "b", "Foo.java"), "src", HEADER);
        // Only the file name (not the full path) is used — kills the negate mutant on the
        // getFileName() null-guard, which would fall back to the full path string.
        assertThat(provider.generate(request), is("Mock summary for Foo.java"));
    }

    @Test
    public void generateFallsBackToFullPathWhenFileNameNull() throws IOException {
        // A root path has a null getFileName(); the provider must use file.toString(). Derive the
        // expected separator from the same Path so the assertion holds on Windows too (the root
        // renders with the platform separator: "/" on POSIX, "\" on Windows).
        Path root = Paths.get("/");
        AiGenerationRequest request = new AiGenerationRequest("summary", root, "src", HEADER);
        assertThat(provider.generate(request), is("Mock summary for " + root));
    }

    @Test
    public void temperatureOverrideDelegatesToPlainGenerate() throws IOException {
        // Exercises AiGenerationProvider's default generate(request, temperature) which delegates
        // to generate(request) — kills the empty-string return mutant on the interface default.
        AiGenerationRequest request = new AiGenerationRequest("summary", Paths.get("Foo.java"), "src", HEADER);
        assertThat(provider.generate(request, 0.9f), is("Mock summary for Foo.java"));
    }
}
