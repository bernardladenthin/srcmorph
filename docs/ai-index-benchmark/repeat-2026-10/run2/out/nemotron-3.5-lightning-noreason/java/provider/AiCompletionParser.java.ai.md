### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:39Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to strip Gemma-4 thinking blocks and extract the model answer.

#### Purpose
Strips leading Gemma-4 thinking blocks from raw LLM completion text to extract the model answer before persisting to an AI index file.

#### Type
Class `AiCompletionParser`; final field constants; `@ToString` annotation; non-final to allow subclassing and mocking.

#### Input
- `response` (String): raw completion text from the model, may be `null`.
- Relies on constants `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER`.

#### Output
- Returns cleaned answer text (String), never `null`; returns empty string for `null` input; throws `IOException` if thinking block starts but end marker is absent.

#### Core logic
- If `response` is `null`, return `""`.
- Trim the response; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If end marker is missing, throw `IOException` with actionable message.
- Return the substring after the end marker, trimmed.

#### Public API
- `parseCompletion(String response) -> String` — strips leading Gemma-4 thinking block and returns the clean answer text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a leading thinking block is started but the closing marker is absent (model token budget exhausted).

#### Concurrency
- Stateless; no synchronization requirements.
