### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:44:36Z
- G: 
- A: 0.0.0
- X: file
---
> Strips model-internal reasoning from Gemma-style responses so only the final answer is persisted.

#### Purpose
- Parses raw LLM completion text for `AiCompletionParser`.
- Removes leading Gemma thinking blocks before storing the answer.

#### Type
- Kind: class
- Modifiers: `public`; non-final
- Extends: none; Implements: none
- Annotations: `@ToString`

#### Input
- Constructor: `AiCompletionParser()` with no parameters.
- `parseCompletion(String response)` consumes the raw completion text; `null` is treated as an empty response.
- Reads only the supplied `String` and uses the static marker constants in the same class.

#### Output
- Returns a cleaned `String` without the leading thinking block.
- On `null` input, returns `""`.
- No file writes, streams, or external state mutations.

#### Core logic
- Checks whether the trimmed response starts with `THINKING_BLOCK_START_MARKER`.
- If not, returns the trimmed response unchanged.
- If it does start with the marker, removes everything up to and including the first `THINKING_BLOCK_END_MARKER`.
- Throws `IOException` when a thinking block begins but the closing marker is absent, with a message instructing to increase `maxOutputTokens`.
- Returns the remaining text trimmed, preserving any later markers as literal answer text.

#### Public API
- `AiCompletionParser() -> AiCompletionParser` creates parser
- `THINKING_BLOCK_START_MARKER -> String` Gemma start token
- `THINKING_BLOCK_END_MARKER -> String` Gemma end token
- `parseCompletion(String) -> String` strips leading reasoning

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `String`
- `AiCompletionParser` static marker constants

#### Exceptions / Errors
- `IOException` when a leading thinking block starts without a closing marker.
- `null` input is normalized to `""` instead of causing an exception.
- No other checked exceptions are thrown in the method body.

#### Concurrency
- No threading or synchronization.
- Static marker constants are immutable `String` values.
- No shared mutable state.
