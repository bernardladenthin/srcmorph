### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:02:53Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts the actual model answer from raw LLM completion text by stripping leading Gemma-4 thinking blocks before storage.

#### Purpose
*   Parses raw LLM completion text to remove internal reasoning blocks.
*   Returns clean answer text or throws error if reasoning block is incomplete.

#### Type
Kind: class. Modifiers: public, non-final. Annotations: @ToString.

#### Input
*   `response`: Raw completion text from the model (String, may be null).

#### Output
*   `String`: Cleaned answer text (never null, empty string if input null).
*   `IOException`: Thrown if thinking block started but never closed.

#### Core logic
*   Returns empty string if input `response` is null.
*   Trims whitespace from `response` to check for start marker.
*   If `response` does not start with `THINKING_BLOCK_START_MARKER`, returns trimmed text.
*   Substrings text after start marker to search for `THINKING_BLOCK_END_MARKER`.
*   Throws `IOException` if end marker is missing (incomplete reasoning).
*   Returns text after end marker, trimmed, otherwise.

#### Public API
`parseCompletion(String response) -> String` (strips leading thinking block).

#### Dependencies
*   `java.io.IOException`
*   `lombok.ToString`

#### Exceptions / Errors
*   `IOException`: Thrown when `THINKING_BLOCK_END_MARKER` is absent after `THINKING_BLOCK_START_MARKER`.
*   Null handling: `null` input returns empty string.

#### Concurrency
*   Thread-safe: Uses only immutable constants and basic string operations.
*   No synchronization required.
