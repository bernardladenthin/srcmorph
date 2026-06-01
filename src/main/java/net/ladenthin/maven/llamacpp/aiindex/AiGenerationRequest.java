// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.nio.file.Path;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Immutable request object passed to an {@link AiGenerationProvider}: identifies the
 * prompt template, the source file being processed, its current text, and the existing
 * header (if any).
 */
@ConvertToRecord
public class AiGenerationRequest {
    private final String promptKey;
    private final Path sourceFile;
    private final String sourceText;
    private final AiMdHeader currentHeader;

    /**
     * Creates a new {@link AiGenerationRequest}.
     *
     * @param promptKey     key of the prompt template to use
     * @param sourceFile    path to the source file being processed
     * @param sourceText    contents of the source file
     * @param currentHeader current header of the corresponding {@code .ai.md} file
     */
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

    /**
     * Returns the prompt template key.
     *
     * @return prompt template key
     */
    public String promptKey() {
        return promptKey;
    }

    /**
     * Returns the path to the source file.
     *
     * @return source file path
     */
    public Path sourceFile() {
        return sourceFile;
    }

    /**
     * Returns the source file contents.
     *
     * @return source file contents
     */
    public String sourceText() {
        return sourceText;
    }

    /**
     * Returns the current header of the corresponding {@code .ai.md} file.
     *
     * @return current header
     */
    public AiMdHeader currentHeader() {
        return currentHeader;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiGenerationRequest that = (AiGenerationRequest) obj;
        return Objects.equals(this.promptKey, that.promptKey)
                && Objects.equals(this.sourceFile, that.sourceFile)
                && Objects.equals(this.sourceText, that.sourceText)
                && Objects.equals(this.currentHeader, that.currentHeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promptKey, sourceFile, sourceText, currentHeader);
    }

    @Override
    public String toString() {
        return "AiGenerationRequest[" + "promptKey="
                + promptKey + ", " + "sourceFile="
                + sourceFile + ", " + "sourceText="
                + sourceText + ", " + "currentHeader="
                + currentHeader + ']';
    }
}
