// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import org.junit.jupiter.api.Test;

public class MockGenerationProviderTest {

    private final MockGenerationProvider provider = new MockGenerationProvider();

    // <editor-fold defaultstate="collapsed" desc="generate">
    @Test
    public void generate_pathWithDirectories_usesFileNameOnly() throws IOException {
        // arrange
        GenerationRequest request = new GenerationRequest("summary", Paths.get("a", "b", "Foo.java"), "src");

        // act, assert
        // Only the file name (not the full path) is used — kills the negate mutant on the
        // getFileName() null-guard, which would fall back to the full path string.
        assertThat(provider.generate(request), is("Mock summary for Foo.java"));
    }

    @Test
    public void generate_fileNameNull_fallsBackToFullPath() throws IOException {
        // A root path has a null getFileName(); the provider must use file.toString(). Derive the
        // expected separator from the same Path so the assertion holds on Windows too (the root
        // renders with the platform separator: "/" on POSIX, "\" on Windows).
        // arrange
        Path root = Paths.get("/");
        GenerationRequest request = new GenerationRequest("summary", root, "src");

        // act, assert
        assertThat(provider.generate(request), is("Mock summary for " + root));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="generateWithTimings">
    @Test
    public void generateWithTimings_sixteenCharSource_reportsDeterministicSyntheticTimings() throws IOException {
        // 16 chars of source / 4 chars-per-token -> 4 mock prompt tokens; positive synthetic rates so a
        // calibrate run works without a real model.
        // arrange
        final GenerationRequest request = new GenerationRequest("summary", Paths.get("Foo.java"), "0123456789012345");

        // act
        final TimedGeneration timings = provider.generateWithTimings(request);

        // assert
        assertThat(timings.text(), is("Mock summary for Foo.java"));
        assertThat(timings.promptTokens(), is(4));
        assertThat(timings.prefillTokensPerSecond() > 0.0d, is(true));
        assertThat(timings.decodeTokensPerSecond() > 0.0d, is(true));
    }

    /**
     * The mock models the system prompt being served from the KV cache: a fixed, non-zero cached prefix on
     * top of the source-derived evaluated tokens. Pinned exactly, because "constant" is the property the
     * calibration differential relies on -- a cached count that varied with the source would not cancel.
     */
    @Test
    public void generateWithTimings_differentSourceSizes_reportsAConstantCachedPrefix() throws IOException {
        // act
        final TimedGeneration small =
                provider.generateWithTimings(new GenerationRequest("summary", Paths.get("Foo.java"), "0123"));
        final TimedGeneration large = provider.generateWithTimings(
                new GenerationRequest("summary", Paths.get("Foo.java"), "0123456789012345"));

        // assert
        assertThat(small.cachedPromptTokens(), is(32));
        assertThat(large.cachedPromptTokens(), is(small.cachedPromptTokens()));
        // Only the evaluated half grows with the source, so the totals differ by the source delta alone.
        assertThat(small.totalPromptTokens(), is(33));
        assertThat(large.totalPromptTokens(), is(36));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="lastUsage">
    @Test
    public void lastUsage_afterGenerate_isAFixedEstimateOfTheLastCall() throws IOException {
        // pre-assert
        assertThat(provider.lastUsage(), is(GenerationUsage.NONE));

        // act
        final String text = provider.generate(new GenerationRequest("k", Paths.get("A.java"), "abcdefghijklmnop"));

        // assert
        assertThat(provider.lastUsage(), is(new GenerationUsage(1, 4, text.length() / 4, 0, "stop")));
    }
    // </editor-fold>
}
