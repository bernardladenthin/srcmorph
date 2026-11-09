### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:57:10Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw completion text by stripping a leading Gemma-4 thinking block while preserving markers that appear elsewhere in the answer.

#### Type
- `AiCompletionParser` — public class; non-final; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw model completion text; `response` may be `null`.
- Uses `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` to identify a leading thinking block.

#### Output
- Returns trimmed answer text, never `null`; `null` input produces `""`.
- Throws `IOException` if a leading thinking block lacks its closing marker.

#### Core logic
- `parseCompletion` returns `""` for `null`; otherwise assigns `response.trim()` to `trimmed`.
- If `trimmed` does not `startsWith(THINKING_BLOCK_START_MARKER)`, returns `trimmed` unchanged apart from trimming.
- For a leading block, removes the start marker with `substring`, then finds the first `THINKING_BLOCK_END_MARKER` using `indexOf`.
- If no closing marker is found, throws `IOException` with an actionable token-budget message and the raw response length.
- Otherwise returns the text after the first closing marker, trimmed; later marker text is preserved.
- `THINKING_BLOCK_START_MARKER` is `"<|channel>thought"`; `THINKING_BLOCK_END_MARKER` is `"<channel|>"`.

#### Public API
- `AiCompletionParser()` — constructs parser.
- `THINKING_BLOCK_START_MARKER: String` — opening thinking token.
- `THINKING_BLOCK_END_MARKER: String` — closing thinking token.
- `parseCompletion(String response) -> String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block has no closing marker.
- Handles `null` input as an empty response.
