### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:44:02Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Removes leading Gemma 4 chain-of-thought text from model completions so only the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text before it is persisted.
- Preserves markers appearing in answer text; strips a thinking block only when it begins the response.

#### Type
- `public class AiCompletionParser` — class; non-final; annotated `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw model completion text; `null` is allowed.

#### Output
- Returns trimmed answer text, never `null`; a `null` response becomes `""`.
- No fields are mutated and no resources are written.

#### Core logic
- Trims the response; returns it unchanged if it does not start with `THINKING_BLOCK_START_MARKER`.
- For a leading thinking block, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text following it.
- Throws `IOException` if the leading block has no closing marker, with guidance to increase `maxOutputTokens` and the response length.

#### Public API
- `AiCompletionParser()` — creates parser.
- `parseCompletion(String)` -> `String` — strips leading thinking block.
- `THINKING_BLOCK_START_MARKER` (`String`) — Gemma 4 thinking-block opener.
- `THINKING_BLOCK_END_MARKER` (`String`) — Gemma 4 thinking-block closer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker.
- `null` input is handled as an empty response.
