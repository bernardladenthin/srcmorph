### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:32Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored, while preserving markers quoted in the answer.

#### Purpose
- Cleans raw model completions so AI index files contain the answer rather than a leading thinking block.
- Rejects responses where a started thinking block lacks its closing marker.

#### Type
- `public class AiCompletionParser` — class; non-final, no superclass or implemented interfaces.
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw model completion text; `null` is accepted.
- Uses the Gemma-4 thinking-block start and end marker constants.

#### Output
- Returns trimmed answer text; `null` input produces `""`.
- Does not mutate fields or write resources.

#### Core logic
- `AiCompletionParser.parseCompletion` trims the response and checks whether it starts with `THINKING_BLOCK_START_MARKER`.
- If it does not, returns the trimmed response unchanged, keeping any markers occurring within the answer.
- If it does, finds the first `THINKING_BLOCK_END_MARKER` after the start marker and returns the trimmed text after it.
- If no closing marker exists, throws `IOException` with token-budget guidance and the response length.

#### Public API
- `AiCompletionParser()` -> constructor; creates parser.
- `THINKING_BLOCK_START_MARKER` -> `String`; Gemma-4 thinking-block opener.
- `THINKING_BLOCK_END_MARKER` -> `String`; Gemma-4 thinking-block closer.
- `parseCompletion(String)` -> `String`; strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` if a leading thinking block has no closing marker.
- `null` input is handled as an empty response.

#### Concurrency
- No mutable instance state or synchronization; parsing uses local variables.
