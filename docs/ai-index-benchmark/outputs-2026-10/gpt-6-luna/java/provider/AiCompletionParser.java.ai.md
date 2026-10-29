### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:16Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading Gemma-style reasoning blocks from AI completion text before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw model completion text by removing a leading internal-thinking block.
- Preserves marker-like text when it occurs elsewhere in the answer.

#### Type
- Public, non-final class; annotated with Lombok `@ToString`.

#### Input
- `parseCompletion` accepts raw completion text as a `String`; `null` is allowed.
- Uses `THINKING_BLOCK_START_MARKER` (`<|channel>thought`) and `THINKING_BLOCK_END_MARKER` (`<channel|>`).

#### Output
- Returns trimmed answer text; returns `""` for `null`.
- Does not persist or mutate completion data.

#### Core logic
- Trims the response; if it does not start with the thinking-block start marker, returns it unchanged apart from trimming.
- For a leading block, finds the first end marker after the start marker and returns the trimmed text following it.
- Throws `IOException` if the leading block has no closing marker.

#### Public API
- `AiCompletionParser()` → constructor; no-op.
- `parseCompletion(String)` → `String`; strips a leading reasoning block.
- `THINKING_BLOCK_START_MARKER` → `String`; identifies block start.
- `THINKING_BLOCK_END_MARKER` → `String`; identifies block end.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker; message recommends increasing `maxOutputTokens` and includes response length.
- `null` input is handled as an empty response.

#### Concurrency
- Stateless; no synchronization or mutable instance state.
