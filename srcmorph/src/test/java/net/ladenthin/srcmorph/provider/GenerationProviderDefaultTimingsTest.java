// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Paths;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import org.junit.jupiter.api.Test;

public class GenerationProviderDefaultTimingsTest {

    private static final AiMdHeader HEADER = new AiMdHeader(
            "Foo.java", "1.0", "C", "2026-01-01T00:00:00Z", "2026-01-01T00:00:10Z", "0.1.0", "1.0.0", "file");

    // <editor-fold defaultstate="collapsed" desc="generateWithTimings">
    @Test
    public void generateWithTimings_defaultImplementation_delegatesToGenerateWithZeroRates() throws Exception {
        // A provider that implements only generate() inherits the interface default, which must return the
        // generated text with zero timings (no measurement available).
        // arrange
        final GenerationProvider provider = request -> "TEXT";

        // act
        final TimedGeneration timings =
                provider.generateWithTimings(new GenerationRequest("summary", Paths.get("Foo.java"), "src"));

        // assert
        assertThat(timings.text(), is("TEXT"));
        assertThat(timings.promptTokens(), is(0));
        assertThat(timings.cachedPromptTokens(), is(0));
        assertThat(timings.totalPromptTokens(), is(0));
        assertThat(timings.prefillTokensPerSecond(), is(0.0d));
        assertThat(timings.predictedTokens(), is(0));
        assertThat(timings.decodeTokensPerSecond(), is(0.0d));
    }
    // </editor-fold>
}
