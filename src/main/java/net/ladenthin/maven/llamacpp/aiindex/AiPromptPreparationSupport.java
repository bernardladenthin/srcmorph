// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

/**
 * Prepares prompts for {@link AiGenerationProvider} calls by substituting the source
 * text into a template and trimming it at a line boundary so that the prompt fits
 * within the configured character budget.
 */
public class AiPromptPreparationSupport {

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

    private final AiPromptSupport promptSupport;

    /**
     * Creates a new {@link AiPromptPreparationSupport}.
     *
     * @param promptSupport prompt lookup used to render templates
     */
    public AiPromptPreparationSupport(final AiPromptSupport promptSupport) {
        this.promptSupport = promptSupport;
    }

    /**
     * Prepares the prompt for {@code request}, trimming the source text at a line
     * boundary when the rendered prompt would exceed {@code maxInputChars}.
     *
     * @param request       generation request to prepare a prompt for
     * @param maxInputChars maximum number of characters allowed in the rendered prompt
     * @return prepared prompt along with trimming metrics
     */
    public AiPreparedPrompt preparePrompt(final AiGenerationRequest request, final int maxInputChars) {
        final String fullPrompt = promptSupport.buildPrompt(request);
        final int originalSourceLength = request.sourceText().length();

        if (fullPrompt.length() <= maxInputChars) {
            return new AiPreparedPrompt(
                    fullPrompt,
                    request.sourceText(),
                    false,
                    originalSourceLength,
                    originalSourceLength,
                    originalSourceLength);
        }

        final AiGenerationRequest emptySourceRequest =
                new AiGenerationRequest(request.promptKey(), request.sourceFile(), "", request.currentHeader());

        final String promptWithoutSource = promptSupport.buildPrompt(emptySourceRequest);
        final int availableSourceChars = Math.max(0, maxInputChars - promptWithoutSource.length());

        final String sourceText = request.sourceText();
        final int trimPoint = Math.min(sourceText.length(), availableSourceChars);
        final String trimmedSource = trimSourceAtLineBreak(sourceText, trimPoint);
        final String trimmedSourceWithMarker = trimmedSource + EOF_MARKER;

        final AiGenerationRequest trimmedRequest = new AiGenerationRequest(
                request.promptKey(), request.sourceFile(), trimmedSourceWithMarker, request.currentHeader());

        final String trimmedPrompt = promptSupport.buildPrompt(trimmedRequest);

        return new AiPreparedPrompt(
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
    public int getBasePromptLength(final String promptKey, final java.nio.file.Path contextFile) {
        return promptSupport.buildPrompt(promptKey, contextFile, "").length();
    }

    /**
     * Trims the source text at or before the given character index, ensuring the trim
     * occurs at a line boundary (after a newline) rather than mid-line. This prevents
     * breaking Java syntax and confusing the AI model.
     *
     * @param sourceText the source code to trim
     * @param targetIndex the target character index
     * @return the source trimmed at the last newline at or before {@code targetIndex},
     *         or the entire source if no newline is found before the index
     */
    private String trimSourceAtLineBreak(final String sourceText, final int targetIndex) {
        if (targetIndex >= sourceText.length()) {
            return sourceText;
        }

        final int lastNewline = sourceText.lastIndexOf('\n', targetIndex);
        if (lastNewline < 0) {
            return sourceText.substring(0, targetIndex);
        }

        return sourceText.substring(0, lastNewline + 1);
    }
}
