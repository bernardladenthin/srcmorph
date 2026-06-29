### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:11:29Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Type
- Class, final

#### Input
- `response`: raw completion text from the model, may be `null`

#### Output
- Cleaned answer text, never `null`

#### Core Logic
- If `response` is `null`, return an empty string.
- Find the last occurrence of `THINKING_BLOCK_END_MARKER`.
  - If found, return the substring after this marker, trimmed.
  - If not found but `THINKING_BLOCK_START_MARKER` is present, throw an `IOException` indicating the token budget was exhausted.
  - If neither marker is present, return the trimmed response.

#### Public API
- `parseCompletion(response) -> String`  
  Strips any Gemma-4 thinking block from `response` and returns the clean answer text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a thinking block was started but the token budget was exhausted before the closing marker was emitted.

#### Concurrency
- Not explicitly noted, but the class is non-final to allow subclassing and mocking in tests.
