### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:39Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored.

#### Purpose
- `AiCompletionParser` extracts the answer after a leading Gemma-compatible thinking block.
- Preserves markers appearing elsewhere in the response as answer text.

#### Type
- `AiCompletionParser` — public, non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is treated as empty.
- Uses the public start and end marker constants to recognize a leading thinking block.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Returns `""` for `null`; otherwise trims the response.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading block, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text after it.
- Throws an actionable token-budget error if the closing marker is absent; reports the original response length.

#### Public API
- `AiCompletionParser()` -> `AiCompletionParser` — creates parser.
- `THINKING_BLOCK_START_MARKER` (`String`) — Gemma thinking-block opener.
- `THINKING_BLOCK_END_MARKER` (`String`) — Gemma thinking-block closer.
- `parseCompletion(String)` -> `String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a response starts with the thinking marker but lacks the closing marker.
- `null` input produces an empty string; other input is trimmed.

#### Concurrency
- No mutable instance state or synchronization; parsing uses local variables.
