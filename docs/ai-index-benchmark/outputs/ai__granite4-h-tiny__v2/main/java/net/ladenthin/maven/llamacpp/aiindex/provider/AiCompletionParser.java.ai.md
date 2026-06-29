### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:33:51Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text.
- Strips model-internal thinking block.
- Returns the actual model answer.

#### Type
- Class: `AiCompletionParser` (non-final).

#### Input
- `String response`: raw completion text from the model, may be `null`.

#### Output
- `String`: cleaned answer text, never `null`.

#### Core Logic
- Checks if `response` is `null`; returns empty string if true.
- Finds the last occurrence of `THINKING_BLOCK_END_MARKER`.
  - If found, returns text after the marker, trimmed.
  - If not found but `THINKING_BLOCK_START_MARKER` is present, throws `IOException`.
  - If neither marker is present, returns trimmed `response`.

#### Public API
- `parseCompletion(String response) throws IOException`: Strips thinking block and returns answer.

#### Dependencies
- `java.io.IOException`.
- `lombok.ToString`.

#### Exceptions / Errors
- Throws `IOException` if thinking block starts but end marker is absent.

#### Concurrency
- Not explicitly covered in the source.
