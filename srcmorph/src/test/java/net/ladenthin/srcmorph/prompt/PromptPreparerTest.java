// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Paths;
import java.util.Arrays;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import org.junit.jupiter.api.Test;

public class PromptPreparerTest {

    private PromptCatalog createPromptSupport() {
        final PromptDefinition promptDefinition = new PromptDefinition();
        promptDefinition.setKey("file-summary");
        promptDefinition.setTemplate("Prompt header\n" + "\n" + "File: %s\n" + "\n" + "Source:\n" + "%s\n");

        return new PromptCatalog(Arrays.asList(promptDefinition));
    }

    private AiMdHeader buildHeader() {
        return new AiMdHeader(
                "Test.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "00000000",
                "2026-03-18T00:00:00Z",
                "2026-03-18T00:00:00Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
    }

    // <editor-fold defaultstate="collapsed" desc="preparePrompt">
    @Test
    public void preparePrompt_promptFitsWithinLimit_notTrimmed() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final String sourceText = "public class Test {\n" + "}\n";
        final GenerationRequest request = new GenerationRequest("file-summary", Paths.get("Test.java"), sourceText);

        // act
        final PreparedPrompt preparedPrompt = support.preparePrompt(request, 10_000);

        // assert
        assertThat(preparedPrompt.trimmed(), is(false));
        assertThat(preparedPrompt.sourceText(), is(equalTo(sourceText)));
        assertThat(preparedPrompt.originalSourceLength(), is(equalTo(sourceText.length())));
        assertThat(preparedPrompt.trimmedSourceLength(), is(equalTo(sourceText.length())));
        assertThat(preparedPrompt.prompt(), containsString("Test.java"));
        assertThat(preparedPrompt.prompt(), containsString(sourceText.trim()));
    }

    @Test
    public void preparePrompt_sourceLongerThanLimit_trimmedFlagIsTrue() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final String sourceText = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final GenerationRequest request = new GenerationRequest("file-summary", Paths.get("Test.java"), sourceText);

        // act
        final PreparedPrompt preparedPrompt = support.preparePrompt(request, 40);

        // assert
        assertThat(preparedPrompt.trimmed(), is(true));
        assertThat(preparedPrompt.trimmedSourceLength() < preparedPrompt.originalSourceLength(), is(true));
        // sourceText includes EOF marker, so it's longer than trimmedSourceLength
        assertThat(
                preparedPrompt.sourceText().length(),
                is(equalTo(preparedPrompt.trimmedSourceLength() + PromptPreparer.EOF_MARKER_LENGTH)));
        assertThat(preparedPrompt.availableSourceChars() >= 0, is(true));
    }

    @Test
    public void preparePrompt_overheadAlreadyExceedsLimit_sourceTrimmedToEmpty() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final GenerationRequest request =
                new GenerationRequest("file-summary", Paths.get("Test.java"), "abcdefghijklmnopqrstuvwxyz");

        // act
        final PreparedPrompt preparedPrompt = support.preparePrompt(request, 5);

        // assert
        assertThat(preparedPrompt.trimmed(), is(true));
        assertThat(preparedPrompt.sourceText(), is(equalTo("\n/* [EOF - source was truncated] */")));
        assertThat(preparedPrompt.trimmedSourceLength(), is(equalTo(0)));
        assertThat(preparedPrompt.availableSourceChars(), is(equalTo(0)));
    }

    @Test
    public void preparePrompt_lengthExactlyAtLimit_notTrimmed() {
        // arrange
        final PromptCatalog promptSupport = createPromptSupport();
        final PromptPreparer support = new PromptPreparer(promptSupport);
        final String sourceText = "public class Test {}\n";
        final GenerationRequest request = new GenerationRequest("file-summary", Paths.get("Test.java"), sourceText);
        // limit set to EXACTLY the full prompt length: the <= guard must treat this as "fits".
        final int exactLimit = promptSupport
                .buildPrompt("file-summary", Paths.get("Test.java"), sourceText)
                .length();

        // act
        final PreparedPrompt prepared = support.preparePrompt(request, exactLimit);

        // assert — kills the "<=" -> "<" conditional-boundary mutant on the fits-within-limit check.
        assertThat(prepared.trimmed(), is(false));
    }

    @Test
    public void preparePrompt_aTrimmedPrompt_isNeverLongerThanItsWindow() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final StringBuilder source = new StringBuilder();
        for (int i = 0; i < 400; i++) {
            source.append("line ").append(i).append('\n');
        }
        for (int window = 300; window < 1_500; window += 7) {
            // act
            final PreparedPrompt prepared = support.preparePrompt(
                    new GenerationRequest("file-summary", Paths.get("A.java"), source.toString()), window);

            // assert
            assertThat(prepared.trimmed(), is(true));
            assertThat(prepared.prompt().length() <= window, is(true));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="basePromptLength">
    @Test
    public void basePromptLength_fileSummaryKey_returnsRenderedBaseLength() {
        // arrange
        final PromptCatalog promptSupport = createPromptSupport();
        final PromptPreparer support = new PromptPreparer(promptSupport);
        final int expected = promptSupport
                .buildPrompt("file-summary", Paths.get("Test.java"), "")
                .length();

        // act, assert
        // Exact, non-zero base length kills the "return 0" primitive-return mutant.
        assertThat(support.basePromptLength("file-summary", Paths.get("Test.java")), is(equalTo(expected)));
        assertThat(expected > 0, is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="trimSourceAtLineBreak (package-private, direct)">
    @Test
    public void trimSourceAtLineBreak_targetAtOrBeyondLength_returnsFullSource() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());

        // act, assert
        // targetIndex == length: "a\nb" distinguishes the ">=" boundary from ">" (the mutant would
        // fall through and cut at the newline, yielding "a\n"). Also covers the "return source" path.
        assertThat(support.trimSourceAtLineBreak("a\nb", 3), is(equalTo("a\nb")));
        // targetIndex > length: kills the empty-object return mutant on the "return sourceText" path.
        assertThat(support.trimSourceAtLineBreak("abc", 5), is(equalTo("abc")));
    }

    @Test
    public void trimSourceAtLineBreak_noNewlineBeforeTarget_cutsAtTarget() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());

        // act, assert
        // lastNewline < 0 branch -> substring(0, target). Exact value kills the empty-return mutant.
        assertThat(support.trimSourceAtLineBreak("abcdef", 3), is(equalTo("abc")));
    }

    @Test
    public void trimSourceAtLineBreak_newlineBeforeTarget_cutsAtLineBoundary() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());

        // act, assert
        // Newline at index 1 -> substring(0, lastNewline + 1) = "a\n". Exact value kills the math
        // mutant on (lastNewline + 1) and the empty-return mutant; covers the newline branch (was NO_COVERAGE).
        assertThat(support.trimSourceAtLineBreak("a\nbcd", 3), is(equalTo("a\n")));
    }

    @Test
    public void trimSourceAtLineBreak_newlineAtStart_distinguishesConditionalBoundary() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());

        // act, assert
        // lastNewline == 0 distinguishes "< 0" from the "<= 0" boundary mutant ("\n" vs "\na"); the line
        // break keeps one of the two characters, half the target
        assertThat(support.trimSourceAtLineBreak("\nabc", 2), is(equalTo("\n")));
    }

    @Test
    public void trimSourceAtLineBreak_aLineBreakKeepingLessThanHalf_cutsAtTheTarget() {
        // arrange: the label of a chunk in front of one long (minified) line
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final String label = "Chunk 3 of 9, half 2, half 1:\n";
        final String minified = label + "x".repeat(1_500);

        // act
        final String trimmed = support.trimSourceAtLineBreak(minified, 1_200);

        // assert: the line, not only the label, is read
        assertThat(trimmed, is(equalTo(minified.substring(0, 1_200))));
    }

    @Test
    public void trimSourceAtLineBreak_aLineBreakKeepingExactlyHalf_isTheCut() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());

        // act, assert: two of four characters are half the target, one of three less
        assertThat(support.trimSourceAtLineBreak("a\nbcdef", 4), is(equalTo("a\n")));
        assertThat(support.trimSourceAtLineBreak("\nabcdef", 3), is(equalTo("\nab")));
    }

    @Test
    public void trimSourceAtLineBreak_aHardCutInsideASurrogatePair_keepsThePairWhole() {
        // arrange: the target falls between the two chars of a character outside the BMP
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final String emoji = new String(Character.toChars(0x1F600));

        // act, assert
        assertThat(support.trimSourceAtLineBreak("ab" + emoji + "cd", 3), is(equalTo("ab")));
        assertThat(support.trimSourceAtLineBreak("\nabcd" + emoji + "ef", 6), is(equalTo("\nabcd")));
    }

    @Test
    public void preparePrompt_aHeaderInFrontOfOneLongLine_readsTheLongLine() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());
        final String source = "-- header\n" + "x".repeat(5_000);
        final GenerationRequest request = new GenerationRequest("file-summary", Paths.get("Dump.sql"), source);
        final int window = 1_000;

        // act
        final PreparedPrompt prepared = support.preparePrompt(request, window);

        // assert: the head that fits is read, not the header alone
        assertThat(prepared.trimmed(), is(true));
        assertThat(prepared.trimmedSourceLength(), is(prepared.availableSourceChars()));
        assertThat(prepared.prompt().length() <= window, is(true));
    }

    @Test
    public void trimSourceAtLineBreak_newlineAtTarget_keepsAtMostTargetCharacters() {
        // arrange
        final PromptPreparer support = new PromptPreparer(createPromptSupport());

        // act, assert
        // the line break at index 2 would keep three characters for a target of two
        assertThat(support.trimSourceAtLineBreak("ab\ncd", 2), is(equalTo("ab")));
        assertThat(support.trimSourceAtLineBreak("a\n\ncd", 2), is(equalTo("a\n")));
    }
    // </editor-fold>
}
