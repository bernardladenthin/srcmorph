### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:00:46Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts the model answer from Gemma-4 LLM completions by stripping thinking blocks.

#### Purpose
- Parses raw LLM completion text to extract the model answer for AI indexing.

#### Type
- Class `AiCompletionParser`, non-final, `@ToString`, extends `Object`, no generic bounds.

#### Input
- `parseCompletion(final String response)`: raw completion text from model, may be null.

#### Output
- Returns `String`, never null: empty string for null input, trimmed answer text otherwise; throws `IOException` if thinking block started but end marker absent.

#### Core logic
- If response is null, return "".
- Trim response; if does not start with `THINKING_BLOCK_START_MARKER`, return trimmed.
- Locate first `THINKING_BLOCK_END_MARKER` after start marker.
- If absent, throw `IOException` with actionable message.
- Return text after end marker, trimmed.

#### Public API
- `AiCompletionParser() -> AiCompletionParser` instantiates new parser.
- `parseCompletion(final String response) -> String` strips leading Gemma-4 thinking block from LLM completion.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when leading thinking block started but closing marker missing (token budget exhausted).
- Null input treated as empty response, no exception thrown.

#### Concurrency
- Stateless instance; no synchronization required; thread-safe for concurrent use.
