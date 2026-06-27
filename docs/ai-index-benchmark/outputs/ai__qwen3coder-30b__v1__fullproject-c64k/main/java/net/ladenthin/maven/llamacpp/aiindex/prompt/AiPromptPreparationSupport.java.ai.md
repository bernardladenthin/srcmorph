### AiPromptPreparationSupport.java
- H: 1.0
- C: BC50BFE5
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:57:28Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Prepares AI prompts by trimming source code to fit within character limits while preserving syntax integrity.

#### Purpose
- Trims source text for AI prompts at line boundaries.
- Ensures prompt length fits within configured maximum character limits.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString annotation.

#### Input
- Constructor: AiPromptSupport dependency.
- Method preparePrompt: AiGenerationRequest, int maxInputChars.
- Method getBasePromptLength: String promptKey, Path contextFile.
- Method trimSourceAtLineBreak: String sourceText, int targetIndex.

#### Output
- Method preparePrompt returns AiPreparedPrompt with trimmed content and metrics.
- Method getBasePromptLength returns int character count of template with empty source.
- Method trimSourceAtLineBreak returns String trimmed at line boundary.

#### Core logic
- Renders full prompt using template and source text.
- Compares rendered length to max input characters.
- If within limit, returns full prompt and source.
- Else, calculates available space after removing source from template.
- Trims source at last newline before available space.
- Appends EOF marker to indicate truncation.
- Re-renders prompt with trimmed source and marker.

#### Public API
- preparePrompt(request, maxInputChars) → AiPreparedPrompt: Trims and renders prompt.
- getBasePromptLength(promptKey, contextFile) → int: Calculates fixed template overhead.
- trimSourceAtLineBreak(sourceText, targetIndex) → String: Trims source at line boundary.

#### Dependencies
net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest  
net.ladenthin.maven.llamacpp.aiindex.prompt.AiPreparedPrompt  
net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport  

#### Exceptions / Errors
- IllegalArgumentException thrown by promptSupport.buildPrompt if no template found.
- Null handling: depends on AiPromptSupport and AiGenerationRequest implementations.

#### Concurrency
No explicit concurrency control. Assumes AiPromptSupport and AiGenerationRequest are immutable or thread-safe.
