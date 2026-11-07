### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:32Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Removes leading Gemma-4 reasoning blocks from model completions so only the answer is stored, while preserving marker text that appears elsewhere.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text before it is stored in an AI index file.
- Detects an unfinished leading thinking block and reports token-budget exhaustion instead of returning incomplete reasoning.

#### Type
- `public class AiCompletionParser` — class; non-final and subclassable; annotated `@ToString`.

#### Input
- `parseCompletion(final String response)` accepts raw completion text; `null` is allowed.
- Uses the `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` constants.

#### Output
- Returns a non-null `String`: trimmed response, or trimmed answer text after a leading thinking block.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- `parseCompletion` returns `""` for `null`; otherwise trims `response` into `trimmed`.
- If `trimmed` does not start with `THINKING_BLOCK_START_MARKER`, returns `trimmed` unchanged apart from trimming; markers within the text remain.
- For a leading block, removes the start marker with `substring`, then finds the first `THINKING_BLOCK_END_MARKER` using `indexOf`.
- If no end marker is found, throws `IOException` with an actionable message to increase `maxOutputTokens`, including `response.length()`.
- Otherwise returns the trimmed substring after the first end marker.
- `THINKING_BLOCK_START_MARKER` is `"<|channel>thought"`; `THINKING_BLOCK_END_MARKER` is `"<channel|>"`.

#### Public API
- `AiCompletionParser()` — constructs parser.
- `parseCompletion(String) -> String` — strips a leading thinking block.
- `THINKING_BLOCK_START_MARKER: String` — opening token.
- `THINKING_BLOCK_END_MARKER: String` — closing token.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `String`

#### Exceptions / Errors
- `IOException` when a leading thinking block is unterminated; message identifies the missing marker and recommends increasing `maxOutputTokens`.
- `null` input is handled as an empty response.
