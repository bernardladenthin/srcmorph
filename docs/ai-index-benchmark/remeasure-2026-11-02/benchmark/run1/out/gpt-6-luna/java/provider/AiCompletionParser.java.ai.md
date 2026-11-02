### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:27:05Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Removes leading Gemma-style reasoning blocks from AI completions so only the answer is stored, while preserving markers that appear in answer text.

#### Purpose
- `AiCompletionParser` cleans raw model completions before they are persisted in an AI index file.
- Rejects responses that begin a thinking block but omit its closing marker.

#### Type
- `AiCompletionParser` — public, non-final class.
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is treated as empty.
- Uses the public start and end marker constants to identify a leading thinking block.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` when a leading thinking block has no closing marker.

#### Core logic
- `null` input returns `""`; otherwise trims the response.
- If trimmed text does not start with the thinking-block start marker, returns it unchanged apart from trimming; markers within it remain text.
- For a leading block, finds the first closing marker after the start marker and returns the trimmed text following it.
- If no closing marker exists, reports token-budget exhaustion and suggests increasing `maxOutputTokens`; includes response length.

#### Public API
- `AiCompletionParser()` -> constructor
- `THINKING_BLOCK_START_MARKER` (`String`) -> opening Gemma thinking token
- `THINKING_BLOCK_END_MARKER` (`String`) -> closing Gemma thinking token
- `parseCompletion(String)` -> `String` — strip leading thinking block

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a response starts with the thinking marker but has no closing marker.
- `null` input returns an empty string.

#### Concurrency
- No mutable instance state or synchronization.
