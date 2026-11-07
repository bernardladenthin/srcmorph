### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:49Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Removes leading Gemma-4 internal reasoning from model completions so only the answer is stored, while preserving markers that occur within answer text.

#### Purpose
- Cleans raw LLM completion text before it is persisted in an AI index.
- Rejects responses that begin a thinking block but lack its closing marker.

#### Type
- `public class AiCompletionParser` — class; non-final, no superclass or interfaces.
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)` — raw model completion; may be `null`.
- Public markers define the Gemma-4 thinking block boundaries.

#### Output
- Returns trimmed answer text, never `null`; `null` input returns `""`.
- No fields are mutated and no resources are written.

#### Core logic
- Trims the response before checking its prefix.
- If the trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming; any markers within it remain text.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text following it.
- Throws `IOException` if the leading block has no closing marker, reporting token-budget exhaustion and the original response length.

#### Public API
- `AiCompletionParser()` -> constructor; no-op initialization.
- `THINKING_BLOCK_START_MARKER` -> `String`; Gemma-4 thinking-block opener.
- `THINKING_BLOCK_END_MARKER` -> `String`; Gemma-4 thinking-block closer.
- `parseCompletion(String)` -> `String`; strip leading reasoning.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block is unclosed.
- `null` input is handled as an empty response.
