### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:16Z
- G: 
- A: 0.0.0
- X: file
---
> Removes a leading Gemma-compatible reasoning block from model completions before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw LLM completion text while preserving marker text that appears within an answer.
- Rejects responses that begin a thinking block but lack its closing marker.

#### Type
- Public, non-final class; annotated `@ToString`.

#### Input
- `parseCompletion` accepts raw completion text, including `null`.
- Uses `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` to identify a leading thinking block.

#### Output
- Returns cleaned answer text; never returns `null`.
- Throws `IOException` if a leading thinking block has no closing marker.

#### Core logic
- Treats `null` input as an empty string; trims non-null input.
- If trimmed text does not start with the thinking-start marker, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first end marker after the start marker and returns the trimmed text following it.
- If no end marker is found, reports token-budget exhaustion and suggests increasing `maxOutputTokens`.

#### Public API
- `AiCompletionParser()` → constructor; creates parser.
- `THINKING_BLOCK_START_MARKER` (`String`) → opens thinking block.
- `THINKING_BLOCK_END_MARKER` (`String`) → closes thinking block.
- `parseCompletion(String response) -> String` → strips leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a response starts with the thinking marker but has no closing marker; message includes response length and an actionable token-budget suggestion.
- `null` input is handled as an empty response.
