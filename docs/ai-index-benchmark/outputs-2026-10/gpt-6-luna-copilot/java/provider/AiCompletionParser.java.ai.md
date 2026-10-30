### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:34:56Z
- G: 
- A: 0.0.0
- X: file
---
> Removes a leading Gemma-4 reasoning block so only the model’s answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text before storage.
- Preserves markers appearing inside the answer.

#### Type
- Public, non-final class; annotated `@ToString`.

#### Input
- `parseCompletion` accepts raw completion text, including `null`.

#### Output
- Returns trimmed answer text; `null` input returns an empty string.
- Does not mutate state or write resources.

#### Core logic
- Trims the response; if it does not start with the thinking marker, returns it unchanged apart from trimming.
- For a leading thinking block, removes text through the first closing marker and trims the remainder.
- Throws `IOException` if the leading block has no closing marker.

#### Public API
- `AiCompletionParser()` → `AiCompletionParser` — constructs parser.
- `THINKING_BLOCK_START_MARKER` → `String` — identifies block start.
- `THINKING_BLOCK_END_MARKER` → `String` — identifies block end.
- `parseCompletion(String response)` → `String` — strips leading reasoning.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block lacks its closing marker; message recommends increasing `maxOutputTokens` and includes response length.
