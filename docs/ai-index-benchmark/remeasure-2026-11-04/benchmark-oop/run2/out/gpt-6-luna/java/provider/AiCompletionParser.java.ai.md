### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:36:09Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Removes leading Gemma-4 chain-of-thought text from model completions before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text, excluding a leading thinking block from the persisted answer.

#### Type
- `public class AiCompletionParser` — class; non-final; annotated `@ToString`; no declared supertypes.

#### Input
- `AiCompletionParser()` takes no parameters.
- `parseCompletion(String response)` consumes raw completion text; accepts `null`.

#### Output
- `parseCompletion` returns trimmed answer text; returns `""` for `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- `parseCompletion` returns `""` for `null`, otherwise trims the response.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming; markers within the text remain.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text after it.
- If no closing marker is found, throws an actionable `IOException` recommending a higher `maxOutputTokens` limit.

#### Public API
- `AiCompletionParser()` -> constructor; creates parser.
- `THINKING_BLOCK_START_MARKER: String` — opening Gemma-4 thinking token.
- `THINKING_BLOCK_END_MARKER: String` — closing Gemma-4 thinking token.
- `parseCompletion(String response)` -> `String`; strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a leading thinking block lacks its closing marker; message includes both markers and response length.
- `null` input is handled as an empty response.
