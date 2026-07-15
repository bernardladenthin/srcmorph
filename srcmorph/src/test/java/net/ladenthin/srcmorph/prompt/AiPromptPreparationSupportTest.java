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
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import org.junit.jupiter.api.Test;

public class AiPromptPreparationSupportTest {

    private AiPromptSupport createPromptSupport() {
        final AiPromptDefinition promptDefinition = new AiPromptDefinition();
        promptDefinition.setKey("file-summary");
        promptDefinition.setTemplate("Prompt header\n" + "\n" + "File: %s\n" + "\n" + "Source:\n" + "%s\n");

        return new AiPromptSupport(Arrays.asList(promptDefinition));
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
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        final String sourceText = "public class Test {\n" + "}\n";
        final AiGenerationRequest request =
                new AiGenerationRequest("file-summary", Paths.get("Test.java"), sourceText, buildHeader());

        // act
        final AiPreparedPrompt preparedPrompt = support.preparePrompt(request, 10_000);

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
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        final String sourceText = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final AiGenerationRequest request =
                new AiGenerationRequest("file-summary", Paths.get("Test.java"), sourceText, buildHeader());

        // act
        final AiPreparedPrompt preparedPrompt = support.preparePrompt(request, 40);

        // assert
        assertThat(preparedPrompt.trimmed(), is(true));
        assertThat(preparedPrompt.trimmedSourceLength() < preparedPrompt.originalSourceLength(), is(true));
        // sourceText includes EOF marker, so it's longer than trimmedSourceLength
        assertThat(
                preparedPrompt.sourceText().length(),
                is(equalTo(preparedPrompt.trimmedSourceLength() + AiPromptPreparationSupport.EOF_MARKER_LENGTH)));
        assertThat(preparedPrompt.availableSourceChars() >= 0, is(true));
    }

    @Test
    public void preparePrompt_overheadAlreadyExceedsLimit_sourceTrimmedToEmpty() {
        // arrange
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        final AiGenerationRequest request = new AiGenerationRequest(
                "file-summary", Paths.get("Test.java"), "abcdefghijklmnopqrstuvwxyz", buildHeader());

        // act
        final AiPreparedPrompt preparedPrompt = support.preparePrompt(request, 5);

        // assert
        assertThat(preparedPrompt.trimmed(), is(true));
        assertThat(preparedPrompt.sourceText(), is(equalTo("\n/* [EOF - source was truncated] */")));
        assertThat(preparedPrompt.trimmedSourceLength(), is(equalTo(0)));
        assertThat(preparedPrompt.availableSourceChars(), is(equalTo(0)));
    }

    @Test
    public void preparePrompt_lengthExactlyAtLimit_notTrimmed() {
        // arrange
        final AiPromptSupport promptSupport = createPromptSupport();
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(promptSupport);
        final String sourceText = "public class Test {}\n";
        final AiGenerationRequest request =
                new AiGenerationRequest("file-summary", Paths.get("Test.java"), sourceText, buildHeader());
        // limit set to EXACTLY the full prompt length: the <= guard must treat this as "fits".
        final int exactLimit = promptSupport
                .buildPrompt("file-summary", Paths.get("Test.java"), sourceText)
                .length();

        // act
        final AiPreparedPrompt prepared = support.preparePrompt(request, exactLimit);

        // assert — kills the "<=" -> "<" conditional-boundary mutant on the fits-within-limit check.
        assertThat(prepared.trimmed(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getBasePromptLength">
    @Test
    public void getBasePromptLength_returnsRenderedBaseLength() {
        final AiPromptSupport promptSupport = createPromptSupport();
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(promptSupport);
        final int expected = promptSupport
                .buildPrompt("file-summary", Paths.get("Test.java"), "")
                .length();
        // Exact, non-zero base length kills the "return 0" primitive-return mutant.
        assertThat(support.getBasePromptLength("file-summary", Paths.get("Test.java")), is(equalTo(expected)));
        assertThat(expected > 0, is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="trimSourceAtLineBreak (package-private, direct)">
    @Test
    public void trim_targetAtOrBeyondLength_returnsFullSource() {
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        // targetIndex == length: "a\nb" distinguishes the ">=" boundary from ">" (the mutant would
        // fall through and cut at the newline, yielding "a\n"). Also covers the "return source" path.
        assertThat(support.trimSourceAtLineBreak("a\nb", 3), is(equalTo("a\nb")));
        // targetIndex > length: kills the empty-object return mutant on the "return sourceText" path.
        assertThat(support.trimSourceAtLineBreak("abc", 5), is(equalTo("abc")));
    }

    @Test
    public void trim_noNewlineBeforeTarget_cutsAtTarget() {
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        // lastNewline < 0 branch -> substring(0, target). Exact value kills the empty-return mutant.
        assertThat(support.trimSourceAtLineBreak("abcdef", 3), is(equalTo("abc")));
    }

    @Test
    public void trim_newlineBeforeTarget_cutsAtLineBoundary() {
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        // Newline at index 1 -> substring(0, lastNewline + 1) = "a\n". Exact value kills the math
        // mutant on (lastNewline + 1) and the empty-return mutant; covers the newline branch (was NO_COVERAGE).
        assertThat(support.trimSourceAtLineBreak("a\nbcd", 3), is(equalTo("a\n")));
    }

    @Test
    public void trim_newlineAtStart_distinguishesConditionalBoundary() {
        final AiPromptPreparationSupport support = new AiPromptPreparationSupport(createPromptSupport());
        // lastNewline == 0 distinguishes "< 0" from the "<= 0" boundary mutant ("\n" vs "\nab").
        assertThat(support.trimSourceAtLineBreak("\nabc", 3), is(equalTo("\n")));
    }
    // </editor-fold>
}
