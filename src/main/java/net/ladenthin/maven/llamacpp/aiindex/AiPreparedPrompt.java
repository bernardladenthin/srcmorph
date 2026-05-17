// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

@ConvertToRecord
public class AiPreparedPrompt {
    private final String prompt;
    private final String sourceText;
    private final boolean trimmed;
    private final int originalSourceLength;
    private final int trimmedSourceLength;
    private final int availableSourceChars;


    public AiPreparedPrompt(String prompt, String sourceText, boolean trimmed, int originalSourceLength, int trimmedSourceLength, int availableSourceChars) {
        Objects.requireNonNull(prompt, "prompt");
        Objects.requireNonNull(sourceText, "sourceText");
        this.prompt = prompt;
        this.sourceText = sourceText;
        this.trimmed = trimmed;
        this.originalSourceLength = originalSourceLength;
        this.trimmedSourceLength = trimmedSourceLength;
        this.availableSourceChars = availableSourceChars;
    }

    public String prompt() {
        return prompt;
    }

    public String sourceText() {
        return sourceText;
    }

    public boolean trimmed() {
        return trimmed;
    }

    public int originalSourceLength() {
        return originalSourceLength;
    }

    public int trimmedSourceLength() {
        return trimmedSourceLength;
    }

    public int availableSourceChars() {
        return availableSourceChars;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiPreparedPrompt that = (AiPreparedPrompt) obj;
        return Objects.equals(this.prompt, that.prompt) &&
                Objects.equals(this.sourceText, that.sourceText) &&
                this.trimmed == that.trimmed &&
                this.originalSourceLength == that.originalSourceLength &&
                this.trimmedSourceLength == that.trimmedSourceLength &&
                this.availableSourceChars == that.availableSourceChars;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prompt, sourceText, trimmed, originalSourceLength, trimmedSourceLength, availableSourceChars);
    }

    @Override
    public String toString() {
        return "AiPreparedPrompt[" +
                "prompt=" + prompt + ", " +
                "sourceText=" + sourceText + ", " +
                "trimmed=" + trimmed + ", " +
                "originalSourceLength=" + originalSourceLength + ", " +
                "trimmedSourceLength=" + trimmedSourceLength + ", " +
                "availableSourceChars=" + availableSourceChars + ']';
    }

}