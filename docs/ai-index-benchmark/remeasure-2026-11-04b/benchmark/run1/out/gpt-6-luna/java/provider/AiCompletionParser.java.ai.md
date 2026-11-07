### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:37Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Removes leading Gemma-4 chain-of-thought text from model completions before the answer is stored.

#### Purpose
- `AiCompletionParser` cleans raw model completion text, preserving answer text while preventing leading internal reasoning from being persisted.

#### Type
- `public class AiCompletionParser` — class; non-final; no supertypes declared; annotated `@ToString`.

#### Input
- `AiCompletionParser()` takes no parameters.
- `parseCompletion(String response)` consumes raw completion text; `null` is allowed and treated as empty.

#### Output
- `parseCompletion` returns a non-null, trimmed answer `String`.
- Produces `IOException` when a leading thinking block has no closing marker.

#### Core logic
- `parseCompletion` returns `""` for `null`; otherwise trims `response`.
- If the trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming; markers elsewhere remain text.
- For a leading thinking block, removes the start marker, finds the first `THINKING_BLOCK_END_MARKER`, and returns the trimmed text after it.
- If no end marker is found, throws `IOException` advising an increase to `maxOutputTokens`; message includes the original response length.

#### Public API
- `AiCompletionParser()` — creates parser.
- `parseCompletion(String response) -> String` — strips leading thinking block.
- `THINKING_BLOCK_START_MARKER: String` — opening marker.
- `THINKING_BLOCK_END_MARKER: String` — closing marker.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a response starts with `THINKING_BLOCK_START_MARKER` but lacks `THINKING_BLOCK_END_MARKER`.
- Handles `null` input; returned value is never `null`.
