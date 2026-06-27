### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:00:09Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract and clean model answers by removing internal reasoning blocks.

#### Purpose
- Strips Gemma-4 thinking blocks from model responses.
- Provides cleaned output for AI index storage.

#### Type
Class, non-final. Implements no interfaces. Uses @ToString annotation.

#### Input
- Constructor: no parameters.
- Method parseCompletion: String response (raw LLM text, may be null).

#### Output
- Method parseCompletion returns String (cleaned answer text).
- Throws IOException if thinking block is incomplete.

#### Core logic
- Checks for presence of THINKING_BLOCK_END_MARKER in response.
- If found, extracts text after the end marker and trims it.
- If THINKING_BLOCK_START_MARKER exists without END_MARKER, throws IOException.
- Returns trimmed input if no markers are present.

#### Public API
parseCompletion(response) -> String removes thinking blocks and returns clean answer
THINKING_BLOCK_START_MARKER -> String identifies start of reasoning block
THINKING_BLOCK_END_MARKER -> String identifies end of reasoning block

#### Dependencies
java.io.IOException, lombok.ToString

#### Exceptions / Errors
Throws IOException when THINKING_BLOCK_START_MARKER is found but END_MARKER is missing.

#### Concurrency
No concurrency considerations; class is stateless.
