### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:37:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text.
- Extracts the model answer by removing internal thinking blocks.
- Stores the clean answer text in an AI index file.

#### Type
- Class
- Non-final

#### Input
- `response`: The raw completion text from the model, may be `null`.

#### Output
- Returns the cleaned answer text, never `null`.
- Throws `IOException` if a thinking block was started but the token budget was exhausted before the closing marker was emitted.

#### Core logic
- If `response` is `null`, return an empty string.
- Find the last occurrence of `THINKING_BLOCK_END_MARKER`.
  - If found, return the text after the marker, trimmed.
  - If not found and `THINKING_BLOCK_START_MARKER` is present, throw an `IOException`.
- If neither marker is present, return the response, trimmed.

#### Public API
- `AiCompletionParser() -> void`: Creates a new `AiCompletionParser`.
- `parseCompletion(final String response) -> String`: Strips any Gemma-4 thinking block from `response` and returns the clean answer text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a thinking block was started but the token budget was exhausted before the closing marker was emitted.

#### Concurrency
- Not applicable
