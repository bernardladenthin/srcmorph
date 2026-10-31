### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:24Z
- G: 
- A: 0.0.0
- X: file
---
> Removes a leading Gemma-4 reasoning block from model completions before answer text is stored.

#### Purpose
- `AiCompletionParser` cleans raw model completion text for persistence by stripping internal reasoning at the beginning of a response.
- Preserves marker text occurring elsewhere in the answer.

#### Type
- `AiCompletionParser` — public, non-final class.
- Annotated with Lombok `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.
- Uses Gemma-4 thinking-block start and end marker constants.

#### Output
- Returns non-null, trimmed answer text.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Treats `null` input as an empty string; otherwise trims the response.
- If the trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, searches for the first `THINKING_BLOCK_END_MARKER` after the start marker.
- Removes the block through that end marker, then trims and returns the remaining answer.
- If no closing marker is found, reports token-budget exhaustion and suggests increasing `maxOutputTokens`.

#### Public API
- `AiCompletionParser()` -> constructor
- `THINKING_BLOCK_START_MARKER` -> opening Gemma-4 reasoning marker
- `THINKING_BLOCK_END_MARKER` -> closing Gemma-4 reasoning marker
- `parseCompletion(String)` -> `String` — strip leading reasoning block

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `String`

#### Exceptions / Errors
- `IOException` when a response starts with the thinking marker but lacks the closing marker; message includes both markers and raw response length.
- `null` input produces an empty result.

#### Concurrency
- No mutable instance state or synchronization is present.
