// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import lombok.ToString;
import net.ladenthin.srcmorph.support.SourceLines;

/**
 * Prepares prompts for {@link net.ladenthin.srcmorph.provider.GenerationProvider} calls by substituting the source
 * text into a template and trimming it, at a line boundary unless that would keep less than half of what
 * fits (see {@link #trimSourceAtLineBreak}), so that the prompt fits within the configured character budget.
 */
@ToString
public class PromptPreparer {

    /**
     * Marker appended to source text when it has been trimmed to indicate that
     * the source is incomplete and not representative of the full file.
     */
    private static final String EOF_MARKER = "\n/* [EOF - source was truncated] */";

    /**
     * Character length of {@link #EOF_MARKER}. Exposed so that callers calculating
     * the maximum input character budget can account for this overhead without
     * duplicating the constant.
     */
    public static final int EOF_MARKER_LENGTH = EOF_MARKER.length();

    /**
     * A trim at a line boundary keeps at least {@code 1/n} of the characters it may keep, or the source is
     * cut where the characters end instead: half, so a line of a normal source is never torn for a few
     * characters more, and a label or a header line in front of one long line is not all that is read.
     */
    private static final int LINE_CUT_MIN_SHARE_DIVISOR = 2;

    private final PromptCatalog promptSupport;

    /**
     * Creates a new {@link PromptPreparer}.
     *
     * @param promptSupport prompt lookup used to render templates
     */
    public PromptPreparer(final PromptCatalog promptSupport) {
        this.promptSupport = promptSupport;
    }

    /**
     * Prepares the prompt for {@code request}, trimming the source text ({@link #trimSourceAtLineBreak})
     * when the rendered prompt would exceed {@code maxInputChars}.
     *
     * @param request       generation request to prepare a prompt for
     * @param maxInputChars maximum number of characters allowed in the rendered prompt
     * @return prepared prompt along with trimming metrics
     */
    public PreparedPrompt preparePrompt(final GenerationRequest request, final int maxInputChars) {
        final String fullPrompt = promptSupport.buildPrompt(request);
        final int originalSourceLength = request.sourceText().length();

        if (fullPrompt.length() <= maxInputChars) {
            return new PreparedPrompt(
                    fullPrompt,
                    request.sourceText(),
                    false,
                    originalSourceLength,
                    originalSourceLength,
                    originalSourceLength);
        }

        final GenerationRequest emptySourceRequest =
                new GenerationRequest(request.promptKey(), request.sourceFile(), "");

        final String promptWithoutSource = promptSupport.buildPrompt(emptySourceRequest);
        // the marker goes behind the trimmed source, so it takes its room from the source's: a trimmed
        // prompt is never longer than maxInputChars
        final int availableSourceChars = Math.max(0, maxInputChars - promptWithoutSource.length() - EOF_MARKER_LENGTH);

        final String sourceText = request.sourceText();
        final int trimPoint = Math.min(sourceText.length(), availableSourceChars);
        final String trimmedSource = trimSourceAtLineBreak(sourceText, trimPoint);
        final String trimmedSourceWithMarker = trimmedSource + EOF_MARKER;

        final GenerationRequest trimmedRequest =
                new GenerationRequest(request.promptKey(), request.sourceFile(), trimmedSourceWithMarker);

        final String trimmedPrompt = promptSupport.buildPrompt(trimmedRequest);

        return new PreparedPrompt(
                trimmedPrompt,
                trimmedSourceWithMarker,
                true,
                originalSourceLength,
                trimmedSource.length(),
                availableSourceChars);
    }

    /**
     * Returns the character length of the prompt template for the given key when the source
     * text substitution is empty. This gives callers a reliable measure of the fixed
     * overhead introduced by the prompt template itself (excluding the source code),
     * which is used in the automatic {@code maxInputChars} calculation.
     *
     * @param promptKey  the key identifying the prompt template to measure
     * @param contextFile the file path substituted as the {@code %s} filename argument
     * @return character count of the rendered prompt with an empty source body
     * @throws IllegalArgumentException if no template is registered for {@code promptKey}
     */
    public int basePromptLength(final String promptKey, final java.nio.file.Path contextFile) {
        return promptSupport.buildPrompt(promptKey, contextFile, "").length();
    }

    /**
     * Trims the source text at or before the given character index, at a line boundary (after a newline)
     * rather than mid-line, so a line is not torn and the model is not confused. A line boundary that would
     * keep fewer than {@code targetIndex / }{@link #LINE_CUT_MIN_SHARE_DIVISOR} characters is not taken: the
     * source is cut at the index instead, before a surrogate pair that it would split. Otherwise a long
     * line behind a short one (a minified chunk behind its label, a dump behind a header line) would be
     * read as the short line alone.
     *
     * <p>A test seam: production trims through {@link #preparePrompt}.</p>
     *
     * @param sourceText the source code to trim
     * @param targetIndex the target character index
     * @return at most {@code targetIndex} characters of the source: up to its last newline before
     *         {@code targetIndex} when that keeps at least half of them, otherwise cut at the index;
     *         the entire source when it is not longer than the index
     */
    // Package-private (not private) so the line-boundary trimming branches can be unit-tested
    // directly; the method is not part of the public API.
    String trimSourceAtLineBreak(final String sourceText, final int targetIndex) {
        if (targetIndex >= sourceText.length()) {
            return sourceText;
        }

        // a line break at targetIndex itself would keep targetIndex + 1 characters
        final int lastNewline = sourceText.lastIndexOf('\n', targetIndex - 1);
        if (lastNewline < 0 || (long) (lastNewline + 1) * LINE_CUT_MIN_SHARE_DIVISOR < targetIndex) {
            return sourceText.substring(0, SourceLines.cutIndex(sourceText, targetIndex));
        }

        return sourceText.substring(0, lastNewline + 1);
    }
}
