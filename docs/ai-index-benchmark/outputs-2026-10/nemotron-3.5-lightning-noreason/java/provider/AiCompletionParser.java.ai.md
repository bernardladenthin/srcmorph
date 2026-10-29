### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:52:14Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to strip Gemma-4 thinking blocks and extract the model answer.

#### Purpose
Strips leading Gemma-4 thinking blocks from raw LLM completion text to extract the clean model answer before storage.

#### Type
Class `AiCompletionParser`, non-final, `@ToString`. No extends / implements. Key constants: `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.

#### Input
Parameter `response` (String, may be null). Relies on constants `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER`.

#### Output
Returns cleaned answer text (String, never null). Throws `IOException` if a leading thinking block starts but the closing marker is absent.

#### Core logic
- If `response` is null, return empty string.
- Trim input; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Strip the start marker; locate the first `THINKING_BLOCK_END_MARKER` in the remainder.
- If the end marker is missing, throw `IOException` with token budget message.
- Otherwise, return the text after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` -> constructor
- `parseCompletion(String response) -> String` -> strips leading thinking block from completion text
- `THINKING_BLOCK_START_MARKER` -> `<|channel>thought`
- `THINKING_BLOCK_END_MARKER` -> `<channel|>`

#### Dependencies
`java.io.IOException`, `lombok.ToString`

#### Exceptions / Errors
Throws `IOException` when a leading thinking block starts but `THINKING_BLOCK_END_MARKER` is absent, indicating exhausted model token budget.

#### Concurrency
Stateless; no threading or synchronization concerns.
