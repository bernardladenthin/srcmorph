// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.nio.file.Path;
import java.util.Objects;

@ConvertToRecord
public class AiGenerationRequest {
    private final String promptKey;
    private final Path sourceFile;
    private final String sourceText;
    private final AiMdHeader currentHeader;


    public AiGenerationRequest(String promptKey, Path sourceFile, String sourceText, AiMdHeader currentHeader) {
        Objects.requireNonNull(promptKey, "promptKey");
        Objects.requireNonNull(sourceFile, "sourceFile");
        Objects.requireNonNull(sourceText, "sourceText");
        Objects.requireNonNull(currentHeader, "currentHeader");
        this.promptKey = promptKey;
        this.sourceFile = sourceFile;
        this.sourceText = sourceText;
        this.currentHeader = currentHeader;
    }

    public String promptKey() {
        return promptKey;
    }

    public Path sourceFile() {
        return sourceFile;
    }

    public String sourceText() {
        return sourceText;
    }

    public AiMdHeader currentHeader() {
        return currentHeader;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiGenerationRequest that = (AiGenerationRequest) obj;
        return Objects.equals(this.promptKey, that.promptKey) &&
                Objects.equals(this.sourceFile, that.sourceFile) &&
                Objects.equals(this.sourceText, that.sourceText) &&
                Objects.equals(this.currentHeader, that.currentHeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promptKey, sourceFile, sourceText, currentHeader);
    }

    @Override
    public String toString() {
        return "AiGenerationRequest[" +
                "promptKey=" + promptKey + ", " +
                "sourceFile=" + sourceFile + ", " +
                "sourceText=" + sourceText + ", " +
                "currentHeader=" + currentHeader + ']';
    }

}