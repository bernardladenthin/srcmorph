// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

public class AiTimeSupportTest {

    private final AiTimeSupport timeSupport = new AiTimeSupport();

    @Test
    public void formatInstant_formatsAsIso8601WithSecondPrecision() {
        Instant instant = Instant.parse("2026-01-02T03:04:05.678Z");
        assertThat(timeSupport.formatInstant(instant), is("2026-01-02T03:04:05Z"));
    }

    @Test
    public void formatInstant_epoch_matchesEpochDateConstant() {
        // Ties the two derivations of the Unix epoch string together: EPOCH_DATE is a static-context
        // constant (used as a fold seed by PackageIndexer/ProjectIndexer, where no instance-bound
        // formatInstant call is convenient), formatInstant is the instance-bound formatter every other
        // timestamp in a .ai.md header goes through. Both must agree.
        assertThat(timeSupport.formatInstant(Instant.EPOCH), is(AiTimeSupport.EPOCH_DATE));
    }

    @Test
    public void epochDate_isTheUnixEpochInIso8601() {
        assertThat(AiTimeSupport.EPOCH_DATE, is("1970-01-01T00:00:00Z"));
    }
}
