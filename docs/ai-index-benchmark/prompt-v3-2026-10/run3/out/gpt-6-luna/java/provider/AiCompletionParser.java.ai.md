### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:47Z
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
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Converts `null` input to an empty string; trims non-null input.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text that follows.
- If no closing marker is found, reports token-budget exhaustion and recommends increasing `maxOutputTokens`.

#### Public API
- `AiCompletionParser()` — creates parser.
- `parseCompletion(String) -> String` — strips leading reasoning block.
- `THINKING_BLOCK_START_MARKER` — opening marker constant.
- `THINKING_BLOCK_END_MARKER` — closing marker constant.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` includes the missing marker, token-budget guidance, and response length.
- `null` response is handled as empty text.
