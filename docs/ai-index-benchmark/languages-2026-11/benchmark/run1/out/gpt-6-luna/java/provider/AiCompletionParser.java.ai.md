### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:29Z
- G: 
- A: 0.0.0
- X: file
---
> Removes leading model-internal reasoning from AI completion text before the answer is stored.

#### Purpose
- `AiCompletionParser` extracts the answer following a leading Gemma-compatible thinking block.
- Preserves marker text when it appears later in the answer.

#### Type
- `public class AiCompletionParser` — class; non-final to permit subclassing and test mocking.
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)` consumes raw completion text; `null` is treated as empty.
- Uses public marker constants to identify the thinking-block boundaries.

#### Output
- Returns trimmed answer text, never `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Trims the response; returns it unchanged if it does not start with the thinking start marker.
- For a leading thinking block, finds the first closing marker after the start marker.
- Returns the trimmed text after that closing marker; quoted markers later in the answer remain intact.
- If no closing marker is found, reports token-budget exhaustion and recommends increasing `maxOutputTokens`.

#### Public API
- `AiCompletionParser()` — creates parser.
- `THINKING_BLOCK_START_MARKER` (`String`) — Gemma thinking-block opener.
- `THINKING_BLOCK_END_MARKER` (`String`) — Gemma thinking-block closer.
- `parseCompletion(String) -> String` — strips leading reasoning.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a response starts with the thinking marker but lacks the closing marker; message includes response length and token-budget guidance.
- `null` input returns an empty string.

#### Concurrency
- No mutable instance state or synchronization; parsing uses local variables.
