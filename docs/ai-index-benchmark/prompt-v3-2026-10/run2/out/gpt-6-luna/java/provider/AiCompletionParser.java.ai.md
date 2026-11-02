### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:22Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading Gemma-style internal reasoning from model completions before the answer is stored, while preserving markers that appear in answer text.

#### Purpose
- `AiCompletionParser` extracts the final answer from raw LLM completion text.
- Rejects incomplete leading thinking blocks so unfinished reasoning is not persisted.

#### Type
- `AiCompletionParser` — public, non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.
- Uses the public start and end marker constants to identify a leading thinking block.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Treats `null` as an empty response; otherwise trims the response.
- If the trimmed response does not start with the thinking marker, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first closing marker after the opening marker and returns the trimmed text after it.
- Preserves marker text occurring later in the answer.
- Throws an actionable token-budget error if the closing marker is absent.

#### Public API
- `AiCompletionParser()` -> `void` — creates parser.
- `THINKING_BLOCK_START_MARKER` -> `String` — opening Gemma thinking token.
- `THINKING_BLOCK_END_MARKER` -> `String` — closing Gemma thinking token.
- `parseCompletion(String)` -> `String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `String`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker; message recommends increasing `maxOutputTokens` and includes response length.
- `null` input is handled as an empty response.

#### Concurrency
- No mutable instance state or synchronization is present.
