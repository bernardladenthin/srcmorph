### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:43Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Removes leading Gemma-4 reasoning blocks from model completions so only the answer is stored in AI index files.

#### Purpose
- Cleans raw LLM completion text before it is persisted.
- Preserves marker text when it appears outside a leading thinking block.

#### Type
- `public class AiCompletionParser` — class; non-final; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw completion text; `null` is allowed.
- Uses static Gemma-4 markers `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER`.

#### Output
- Returns trimmed answer text; returns `""` for `null`.
- Does not mutate state or write resources.

#### Core logic
- Trims the response; if it does not start with `<|channel>thought`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first `<channel|>` after the start marker and returns trimmed text following it.
- Throws `IOException` when a leading thinking block has no closing marker.

#### Public API
- `AiCompletionParser()` — creates parser.
- `parseCompletion(String) -> String` — strips leading thinking block.
- `THINKING_BLOCK_START_MARKER` — opening token constant.
- `THINKING_BLOCK_END_MARKER` — closing token constant.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` if the leading thinking block lacks its closing marker; message advises increasing `maxOutputTokens` and includes response length.
- `null` input becomes an empty string; output is never `null`.

#### Concurrency
- Stateless; no mutable instance fields.
