### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:59:36Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completion text so only the answer is returned for storage.

#### Purpose
- `AiCompletionParser` cleans raw model completions before they are stored in an AI index file.
- Preserves marker text when it appears outside a leading thinking block.

#### Type
- `public class AiCompletionParser` — non-final class; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.
- Uses `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` to recognize a leading reasoning block.

#### Output
- Returns trimmed answer text, never `null`.
- Does not mutate state or write resources.

#### Core logic
- Treats `null` as an empty response; otherwise trims the response.
- If trimmed text does not start with the thinking-block start marker, returns it unchanged apart from trimming.
- For a leading block, finds the first end marker after the start marker and returns the trimmed text following it.
- Keeps any markers quoted in the returned answer.
- Throws `IOException` if the leading block has no closing marker.

#### Public API
- `AiCompletionParser()` — creates parser.
- `THINKING_BLOCK_START_MARKER` (`String`) — opening thinking-block token.
- `THINKING_BLOCK_END_MARKER` (`String`) — closing thinking-block token.
- `parseCompletion(String) -> String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block is unterminated; message advises increasing `maxOutputTokens` and includes response length.
- `null` input is handled as an empty response.
