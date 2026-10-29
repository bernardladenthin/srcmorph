### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:08:20Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text.
- Strips model-internal thinking block before storing the answer.

#### Type
- Class, final.

#### Input
- `response`: raw completion text from the model, may be `null`.

#### Output
- Cleaned answer text, never `null`.

#### Core Logic
- If `response` is `null`, return empty string.
- Trim `response`.
- If trimmed response does not start with `THINKING_BLOCK_START_MARKER`, return trimmed response.
- Otherwise, extract text after `THINKING_BLOCK_START_MARKER`.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If not found, throw `IOException` with actionable message.
  - Otherwise, return text after `THINKING_BLOCK_END_MARKER`, trimmed.

#### Public API
- `parseCompletion(response) -> String` (strips leading thinking block and returns answer).

#### Dependencies
- `java.io.IOException`.
- `lombok.ToString`.

#### Exceptions / Errors
- Throws `IOException` if `THINKING_BLOCK_END_MARKER` is absent after `THINKING_BLOCK_START_MARKER`.

#### Concurrency
- Not thread-safe due to reliance on static constants.
