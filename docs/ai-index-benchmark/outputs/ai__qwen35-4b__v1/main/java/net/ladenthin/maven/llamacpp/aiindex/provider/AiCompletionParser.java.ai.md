### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:17:03Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
`).

#### Input
*   `response`: Raw completion string from the model (may be null).
*   Constants: Defined markers for Gemma-4 reasoning blocks.

#### Output
*   `String`: Cleaned answer text following the closing marker or trimmed original input.
*   Side effects: Throws `IOException` if start marker exists without end marker.
*   State: Mutates no state; returns new string.

#### Core logic
*   Returns empty string if input is null.
*   Finds last occurrence of `THINKING_BLOCK_END_MARKER` to truncate reasoning.
*   Throws `IOException` if `THINKING_BLOCK_START_MARKER` appears but end marker is missing (budget exhaustion).
*   Returns trimmed original response if no markers are detected.

#### Public API
`parseCompletion(String response) -> String`: Extracts clean answer from raw completion.

#### Dependencies
*   `java.io.IOException`
*   `lombok.ToString`
*   Constants: `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.

#### Exceptions / Errors
*   Throws `IOException` when thinking block is started but not closed (token budget exhausted).
*   Handles null input gracefully as empty string.

#### Concurrency
*   Thread-safe via pure functional logic and immutability of returned strings.
*   No synchronization required.
