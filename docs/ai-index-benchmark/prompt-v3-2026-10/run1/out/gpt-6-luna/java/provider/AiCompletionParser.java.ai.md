### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:47Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading Gemma-4 internal reasoning from model completions before the answer is stored, while preserving markers that appear in answer text.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text for storage.
- Rejects responses that start a thinking block but lack its closing marker.

#### Type
- `AiCompletionParser` — public, non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.
- Uses `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` to recognize a leading thinking block.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` when a leading thinking block is unterminated.

#### Core logic
- Maps `null` to an empty string; trims other input.
- If trimmed input does not start with the start marker, returns it unchanged apart from trimming; markers within it remain text.
- For a leading block, finds the first end marker after the start marker and returns the trimmed text following it.
- If no end marker exists, throws an actionable token-budget error including the original response length.

#### Public API
- `AiCompletionParser()` — constructs parser.
- `parseCompletion(String)` -> `String` — strips leading thinking block.
- `THINKING_BLOCK_START_MARKER` — `String` constant `<|channel>thought`.
- `THINKING_BLOCK_END_MARKER` — `String` constant `<channel|>`.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a leading thinking block has no closing marker; message recommends increasing `maxOutputTokens`.
- `null` input is handled as an empty response.
