### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:47:09Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma-4 chain-of-thought reasoning blocks from raw LLM completion text to extract the clean model answer for persistence.

#### Purpose
- Parses raw LLM completion output to remove internal thinking/reasoning blocks before storing results in an AI index.
- Handles Gemma-4 (and compatible) models that wrap reasoning in `<|channel>thought` ... `<channel|>` markers.

#### Type
- `class AiCompletionParser` — public, non-final (allows subclassing/mocking in tests)
- Annotated `@ToString` (Lombok)
- No extends/implements

#### Input
- `parseCompletion(String response)` — raw completion text from the model; may be `null`

#### Output
- `String` — cleaned answer text, never `null`
- Throws `IOException` if thinking block is unterminated (token budget exhausted)

#### Core logic
- `null` input → return `""`
- Trim the response; if it does **not** start with `THINKING_BLOCK_START_MARKER` (`<|channel>thought`), return the trimmed text as-is (any markers inside are preserved as answer content)
- If it **does** start with the start marker:
  - Strip the start marker prefix
  - Find the **first** occurrence of `THINKING_BLOCK_END_MARKER` (`<channel|>`) in the remainder
  - If not found → throw `IOException` with actionable message (increase `maxOutputTokens`)
  - If found → return the text after the end marker, trimmed
- Only a **leading** thinking block is stripped; markers appearing later in the text are treated as part of the answer

#### Public API
- `AiCompletionParser()` -> `void` — no-arg constructor
- `parseCompletion(String response)` -> `String` — strips leading thinking block
- `THINKING_BLOCK_START_MARKER` (static final `String`) — `"<|channel>thought"`
- `THINKING_BLOCK_END_MARKER` (static final `String`) — `"<channel|>"`

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when `THINKING_BLOCK_START_MARKER` is present at the start but `THINKING_BLOCK_END_MARKER` is absent — indicates token budget exhaustion mid-reasoning; message includes response length and remediation hint
- `null` input handled gracefully (returns empty string, no NPE)
