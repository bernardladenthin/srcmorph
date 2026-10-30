### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:49Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma-4 chain-of-thought thinking blocks from raw LLM completion text to return the model's final answer.

#### Purpose
- Extracts the model answer from raw LLM output before it is stored in an AI index file.
- Discards leading internal reasoning so it is never persisted.

#### Type
- Class, public, non-final (allows subclassing and mocking in tests).
- Annotation: `@ToString` (Lombok).
- Extends `Object`; implements no interfaces.

#### Input
- `parseCompletion(String response)`: raw completion text, may be `null`.
- Constants `THINKING_BLOCK_START_MARKER` (`<|channel>thought`) and `THINKING_BLOCK_END_MARKER` (`<channel|>`) as delimiters.

#### Output
- Returns `String`: trimmed answer text, never `null`.
- Throws `IOException` when a start marker exists without an end marker.
- No mutated state, no written resources.

#### Core logic
- Treat `null` input as empty string.
- Trim the response.
- If the trimmed text does not start with the start marker, return it as is (markers mid-text are kept).
- If it starts with the start marker, search the remainder for the first end marker.
- No end marker found: throw `IOException` advising to increase `maxOutputTokens`.
- End marker found: return the text after it, trimmed.

#### Public API
- `AiCompletionParser()` -> constructor, no-op.
- `parseCompletion(String response) -> String` - strips leading thinking block, returns answer.
- `THINKING_BLOCK_START_MARKER` -> `String` constant, opening delimiter.
- `THINKING_BLOCK_END_MARKER` -> `String` constant, closing delimiter.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- Gemma-4 model output format (thinking channel tokens).

#### Exceptions / Errors
- `IOException`: start marker present, end marker absent (token budget exhausted).
- `null` input: returns `""`, no exception.

#### Concurrency
- Stateless; no fields. Safe for concurrent use.
- Constants are immutable `String` values.
