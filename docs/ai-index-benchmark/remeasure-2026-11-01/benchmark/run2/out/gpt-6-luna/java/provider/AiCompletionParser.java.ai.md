### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:19Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw model completions by stripping a leading Gemma-compatible thinking block.
- Preserves markers appearing elsewhere in the answer.

#### Type
- `AiCompletionParser` — public, non-final class.
- Annotated with Lombok `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw completion text; `null` is accepted.

#### Output
- Returns cleaned, trimmed answer text; never returns `null`.
- Exposes public marker constants for the thinking-block delimiters.

#### Core logic
- `null` input becomes an empty string; otherwise trims the response.
- If the trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the opening marker and returns the trimmed text following it.
- Throws `IOException` if the block starts but has no closing marker, with guidance to increase `maxOutputTokens` and the response length.

#### Public API
- `AiCompletionParser()` -> `AiCompletionParser` — creates parser.
- `THINKING_BLOCK_START_MARKER` — opening thinking-block token.
- `THINKING_BLOCK_END_MARKER` — closing thinking-block token.
- `parseCompletion(String)` -> `String` — strips leading reasoning block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `String` methods: `trim`, `startsWith`, `substring`, `indexOf`, `length`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker.
- `null` input is treated as an empty response.

#### Concurrency
- No mutable instance state; parsing uses local variables.
