### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:44Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored, while preserving markers that appear in the answer.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text for storage in an AI index file.
- Recognizes a Gemma-4 thinking block only at the start of the response.

#### Type
- `public class AiCompletionParser` — non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.
- Uses the public start and end marker constants.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Treats `null` input as an empty string; otherwise trims the response.
- If the trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text following it.
- Preserves marker text appearing after the leading block, or anywhere in a response without a leading block.

#### Public API
- `AiCompletionParser()` — constructs parser.
- `parseCompletion(String) -> String` — strips a leading thinking block.
- `THINKING_BLOCK_START_MARKER: String` — Gemma-4 thinking-block opener.
- `THINKING_BLOCK_END_MARKER: String` — Gemma-4 thinking-block closer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a leading thinking block lacks its closing marker; message identifies the missing marker, recommends increasing `maxOutputTokens`, and includes response length.
- `null` input is handled as an empty response.

#### Concurrency
- No mutable instance state or synchronization; parsing uses local variables.
