// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

/**
 * Immutable result of preparing a prompt: the substituted prompt text, the source text
 * actually included, and metrics describing how the source was trimmed (if at all).
 */
@ConvertToRecord
public class AiPreparedPrompt {
    private final String prompt;
    private final String sourceText;
    private final boolean trimmed;
    private final int originalSourceLength;
    private final int trimmedSourceLength;
    private final int availableSourceChars;

    /**
     * Creates a new {@link AiPreparedPrompt}.
     *
     * @param prompt              fully prepared prompt text
     * @param sourceText          source text that was substituted into the prompt
     * @param trimmed             {@code true} when {@code sourceText} was shorter than the original input
     * @param originalSourceLength original number of characters in the source text before trimming
     * @param trimmedSourceLength  number of characters retained after trimming
     * @param availableSourceChars character budget that was available for substitution
     */
    public AiPreparedPrompt(
            String prompt,
            String sourceText,
            boolean trimmed,
            int originalSourceLength,
            int trimmedSourceLength,
            int availableSourceChars) {
        Objects.requireNonNull(prompt, "prompt");
        Objects.requireNonNull(sourceText, "sourceText");
        this.prompt = prompt;
        this.sourceText = sourceText;
        this.trimmed = trimmed;
        this.originalSourceLength = originalSourceLength;
        this.trimmedSourceLength = trimmedSourceLength;
        this.availableSourceChars = availableSourceChars;
    }

    /**
     * Returns the prepared prompt text.
     *
     * @return prepared prompt text
     */
    public String prompt() {
        return prompt;
    }

    /**
     * Returns the source text that was substituted into the prompt.
     *
     * @return source text used for substitution
     */
    public String sourceText() {
        return sourceText;
    }

    /**
     * Returns whether the source text was trimmed to fit within the available budget.
     *
     * @return {@code true} when trimming occurred
     */
    public boolean trimmed() {
        return trimmed;
    }

    /**
     * Returns the original source text length before trimming.
     *
     * @return original source text length in characters
     */
    public int originalSourceLength() {
        return originalSourceLength;
    }

    /**
     * Returns the source text length after trimming.
     *
     * @return retained source text length in characters
     */
    public int trimmedSourceLength() {
        return trimmedSourceLength;
    }

    /**
     * Returns the character budget that was available for source substitution.
     *
     * @return available character budget
     */
    public int availableSourceChars() {
        return availableSourceChars;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiPreparedPrompt that = (AiPreparedPrompt) obj;
        return Objects.equals(this.prompt, that.prompt)
                && Objects.equals(this.sourceText, that.sourceText)
                && this.trimmed == that.trimmed
                && this.originalSourceLength == that.originalSourceLength
                && this.trimmedSourceLength == that.trimmedSourceLength
                && this.availableSourceChars == that.availableSourceChars;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                prompt, sourceText, trimmed, originalSourceLength, trimmedSourceLength, availableSourceChars);
    }

    @Override
    public String toString() {
        return "AiPreparedPrompt[" + "prompt="
                + prompt + ", " + "sourceText="
                + sourceText + ", " + "trimmed="
                + trimmed + ", " + "originalSourceLength="
                + originalSourceLength + ", " + "trimmedSourceLength="
                + trimmedSourceLength + ", " + "availableSourceChars="
                + availableSourceChars + ']';
    }
}
