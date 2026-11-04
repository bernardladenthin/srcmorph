### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:51:08Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Removes leading Gemma 4 internal reasoning from model completions so only the answer is stored in AI index files.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text by stripping a leading thinking block.
- Preserves markers appearing elsewhere in the answer and rejects incomplete leading thinking blocks.

#### Type
- `public class AiCompletionParser` — class; non-final; annotated `@ToString`.
- No supertypes explicitly declared.

#### Input
- `AiCompletionParser()` — no arguments.
- `parseCompletion(final String response)` — raw completion text; accepts `null`.
- Uses `THINKING_BLOCK_START_MARKER` (`"<|channel>thought"`) and `THINKING_BLOCK_END_MARKER` (`"<channel|>"`).

#### Output
- `parseCompletion` returns trimmed answer text, never `null`.
- Returns `""` for `null` input.
- No state mutation or resource writes.

#### Core logic
- `null` input becomes an empty string; otherwise trims the response.
- If the trimmed response does not start with the thinking-block start marker, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first end marker after the start marker and returns the trimmed text after it.
- Throws `IOException` if the leading thinking block has no end marker.

#### Public API
- `AiCompletionParser()` — creates parser.
- `THINKING_BLOCK_START_MARKER: String` — opening thinking-block token.
- `THINKING_BLOCK_END_MARKER: String` — closing thinking-block token.
- `parseCompletion(String) -> String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker; message recommends increasing `maxOutputTokens` and includes response length.
