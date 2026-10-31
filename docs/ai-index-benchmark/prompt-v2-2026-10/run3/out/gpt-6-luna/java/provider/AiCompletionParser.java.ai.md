### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:12Z
- G: 
- A: 0.0.0
- X: file
---
> Removes a leading Gemma-4 reasoning block from model completions before the answer is stored.

#### Purpose
- `AiCompletionParser` extracts the answer text from raw LLM completion text.
- Preserves marker text appearing outside a leading thinking block.

#### Type
- `public class AiCompletionParser` — non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` when a leading thinking block has no closing marker.

#### Core logic
- Treats `null` as an empty response; trims non-null input.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- Otherwise finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text after it.
- Throws an actionable token-budget error if the closing marker is absent; error includes the original response length.

#### Public API
- `AiCompletionParser()` — creates parser.
- `parseCompletion(String) -> String` — strips a leading thinking block.
- `THINKING_BLOCK_START_MARKER` — opening token constant.
- `THINKING_BLOCK_END_MARKER` — closing token constant.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a response starts with the thinking marker but lacks the closing marker.
- `null` input returns an empty string.

#### Concurrency
- No mutable instance state or synchronization; parsing uses local variables.
