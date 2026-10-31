### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:45:03Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading Gemma thinking text from model completions before the answer is stored, while preserving marker text that appears within an answer.

#### Purpose
- `AiCompletionParser` cleans raw model completion text for storage by stripping a leading thinking block.
- Preserves ordinary response text and markers quoted within it.

#### Type
- `public class AiCompletionParser` — class; non-final to permit subclassing and test mocking.
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw completion text; accepts `null`.

#### Output
- Returns a non-null, trimmed answer string.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- `null` input returns `""`; otherwise trims the response.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first closing marker after the start marker and returns the trimmed text following it.
- If no closing marker exists, reports token-budget exhaustion and suggests increasing `maxOutputTokens`.

#### Public API
- `AiCompletionParser()` — creates parser.
- `THINKING_BLOCK_START_MARKER` -> `String` — Gemma thinking-block opener.
- `THINKING_BLOCK_END_MARKER` -> `String` — Gemma thinking-block closer.
- `parseCompletion(String)` -> `String` — strips leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block is unterminated; message includes marker names and response length.
- `null` input is handled as an empty response.

#### Concurrency
- No mutable instance state or synchronization; parsing uses local variables.
