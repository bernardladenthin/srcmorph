### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:40Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Removes leading Gemma-4 chain-of-thought text from model completions before the answer is stored, while preserving markers that appear in the answer itself.

#### Purpose
- `AiCompletionParser` cleans raw model completion text for storage by stripping a leading thinking block.
- Rejects a started thinking block if its closing marker is missing.

#### Type
- `public class AiCompletionParser` — class; non-final; annotated `@ToString`; no supertypes declared.

#### Input
- `AiCompletionParser()` — no parameters.
- `parseCompletion(final String response)` — raw completion text; accepts `null`.
- Reads public static marker constants `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER`.

#### Output
- `parseCompletion` returns trimmed answer text, never `null`.
- Returns `""` for `null` input; does not mutate fields or write resources.

#### Core logic
- Return `""` for a `null` response; otherwise trim it.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, return it unchanged apart from trimming.
- For a leading marker, find the first `THINKING_BLOCK_END_MARKER` after it and return the trimmed text following that marker.
- If the end marker is absent, throw `IOException` with token-budget guidance and the original response length.

#### Public API
- `AiCompletionParser()` — creates parser.
- `THINKING_BLOCK_START_MARKER: String` — opening thinking token.
- `THINKING_BLOCK_END_MARKER: String` — closing thinking token.
- `parseCompletion(String) -> String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` if a leading thinking block has no closing marker.
- `null` input is handled as an empty response.
