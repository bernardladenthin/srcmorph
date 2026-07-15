// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.ToString;

/** Utility helpers for formatting timestamps written into {@code .ai.md} headers. */
@ToString
public class AiTimeSupport {

    /** Creates a new {@link AiTimeSupport}. */
    public AiTimeSupport() {
        // no-op
    }

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    /**
     * The earliest possible ISO-8601 date, formatted the same way as every other timestamp in this
     * class. Used as the starting value when folding over a set of child dates to find the latest
     * one (e.g. {@code latest = EPOCH_DATE; for (child) latest = laterDate(latest, child.date());}) —
     * never itself persisted, since callers only run that fold when at least one child exists.
     */
    public static final String EPOCH_DATE = ISO_FORMATTER.format(Instant.EPOCH);

    /**
     * Formats the given instant as an ISO-8601 string truncated to seconds.
     *
     * @param instant instant to format
     * @return ISO-8601 timestamp with second precision
     */
    public String formatInstant(final Instant instant) {
        return ISO_FORMATTER.format(instant.truncatedTo(ChronoUnit.SECONDS));
    }
}
