// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiMdLeadExtractorTest {

    private final AiMdLeadExtractor extractor = new AiMdLeadExtractor();

    // <editor-fold defaultstate="collapsed" desc="blockquote lead">
    @Test
    public void extractLead_blockquoteWithSpace_returnsTextWithoutMarker() {
        // act
        final String lead = extractor.extractLead("> Calculates VAT for invoices.");

        // assert
        assertThat(lead, is(equalTo("Calculates VAT for invoices.")));
    }

    @Test
    public void extractLead_blockquoteWithoutSpace_returnsText() {
        // act
        final String lead = extractor.extractLead(">Calculates VAT.");

        // assert
        assertThat(lead, is(equalTo("Calculates VAT.")));
    }

    @Test
    public void extractLead_blockquoteSurroundedByWhitespace_isTrimmedBothSides() {
        // act: leading spaces before the marker AND padding after it must both be removed
        final String lead = extractor.extractLead("  >   Calculates VAT.  ");

        // assert
        assertThat(lead, is(equalTo("Calculates VAT.")));
    }

    @Test
    public void extractLead_blockquoteThenSections_returnsOnlyLead() {
        // arrange: a realistic body — blockquote lead, then the structured sections
        final String body = "> Synthesizes a package summary.\n" + "\n" + "#### Purpose\n" + "- bullet one\n";

        // act
        final String lead = extractor.extractLead(body);

        // assert
        assertThat(lead, is(equalTo("Synthesizes a package summary.")));
    }

    @Test
    public void extractLead_leadingBlankLines_areSkipped() {
        // act
        final String lead = extractor.extractLead("\n   \n> The lead after blanks.");

        // assert
        assertThat(lead, is(equalTo("The lead after blanks.")));
    }

    @Test
    public void extractLead_bareMarker_returnsEmpty() {
        // act: a lone ">" has no text after the marker
        final String lead = extractor.extractLead(">");

        // assert
        assertThat(lead, is(equalTo("")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="non-blockquote fallback">
    @Test
    public void extractLead_plainFirstLine_returnsTrimmedLine() {
        // act: no blockquote — the first non-blank line is the lead, trimmed
        final String lead = extractor.extractLead("  Plain summary line  \nsecond line\n");

        // assert
        assertThat(lead, is(equalTo("Plain summary line")));
    }

    @Test
    public void extractLead_nonBlockquoteFirstLine_doesNotPromoteLaterBlockquote() {
        // act: only the FIRST non-blank line counts; a blockquote further down is ignored
        final String lead = extractor.extractLead("Title line\n> later quote that is not the lead");

        // assert
        assertThat(lead, is(equalTo("Title line")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="empty / blank bodies">
    @Test
    public void extractLead_emptyBody_returnsEmpty() {
        // act
        final String lead = extractor.extractLead("");

        // assert
        assertThat(lead, is(equalTo("")));
    }

    @Test
    public void extractLead_whitespaceOnlyBody_returnsEmpty() {
        // act
        final String lead = extractor.extractLead("   \n  \n\t\n");

        // assert
        assertThat(lead, is(equalTo("")));
    }
    // </editor-fold>
}
