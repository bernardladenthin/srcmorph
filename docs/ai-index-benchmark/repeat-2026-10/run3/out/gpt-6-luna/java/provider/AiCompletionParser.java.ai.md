### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:20:19Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw model completions by stripping a leading Gemma-compatible thinking block.
- Preserves marker text when it appears outside a leading thinking block.

#### Type
- Public, non-final class `AiCompletionParser`.
- Annotated with Lombok `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw completion text; `null` is accepted.
- Uses the `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` constants.

#### Output
- Returns trimmed answer text, never `null`.
- Does not mutate fields or write resources.

#### Core logic
- Maps `null` input to an empty string; otherwise trims the response.
- If trimmed text does not begin with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the opening marker and returns the trimmed text following it.
- Throws `IOException` if the leading block has no closing marker.

#### Public API
- `AiCompletionParser()` -> `void` — creates parser
- `THINKING_BLOCK_START_MARKER` -> `String` — opens thinking block
- `THINKING_BLOCK_END_MARKER` -> `String` — closes thinking block
- `parseCompletion(String response)` -> `String` — strips leading reasoning

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker; message identifies the markers, suggests increasing `maxOutputTokens`, and includes response length.
- `null` input returns `""`.

#### Concurrency
- No mutable instance state; parsing uses local variables.
