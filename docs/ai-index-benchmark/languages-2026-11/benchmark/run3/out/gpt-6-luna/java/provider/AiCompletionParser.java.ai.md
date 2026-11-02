### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:59Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completions before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw completion text by stripping a leading Gemma-style thinking block.
- Preserves markers that occur elsewhere in the answer.

#### Type
- `AiCompletionParser` — public, non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Returns an empty string for `null`; otherwise trims the response.
- If the trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, searches for the first `THINKING_BLOCK_END_MARKER` after the opening marker.
- Returns trimmed text after the closing marker; throws an actionable `IOException` if it is absent.

#### Public API
- `AiCompletionParser()` -> constructor; no-op.
- `THINKING_BLOCK_START_MARKER` -> `String`; opening thinking token.
- `THINKING_BLOCK_END_MARKER` -> `String`; closing thinking token.
- `parseCompletion(String)` -> `String`; strips leading thinking block.

#### Dependencies
- `java.io.IOException` — parse failure signal.
- `lombok.ToString` — generated string representation.

#### Exceptions / Errors
- Throws `IOException` when a leading thinking block lacks its closing marker; message advises increasing `maxOutputTokens` and includes response length.
- Treats `null` input as empty text.
