### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:42:00Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma-4 chain-of-thought reasoning blocks from raw LLM completion text before the answer is persisted to an AI index file.

#### Purpose
- Parses raw model completion output to extract the clean answer by removing a leading thinking block.
- Prevents internal reasoning tokens from being stored in the AI index.

#### Type
- `public class AiCompletionParser` (non-final, subclassable for mocking)
- Annotated `@ToString` (Lombok)
- No extends / implements

#### Input
- `parseCompletion(String response)` — raw completion text from the model; may be `null`

#### Output
- Returns `String` — the cleaned answer text, never `null`
- Throws `IOException` when the thinking block is unterminated (token budget exhausted)

#### Core logic
- If `response` is `null`, return `""`.
- Trim the response; if it does **not** start with `THINKING_BLOCK_START_MARKER`, return the trimmed string as-is (any markers inside are preserved as text).
- If it does start with the start marker, locate the **first** occurrence of `THINKING_BLOCK_END_MARKER` after the start marker.
- If the end marker is absent → throw `IOException` with a message advising to increase `maxOutputTokens`.
- Otherwise return the substring after the end marker, trimmed.
- Design constraint: only a thinking block at the **start** of the response is stripped; markers appearing later are part of the answer.

#### Public API
- `THINKING_BLOCK_START_MARKER` (static final String) — Gemma-4 thought-block open token
- `THINKING_BLOCK_END_MARKER` (static final String) — Gemma-4 thought-block close token
- `AiCompletionParser()` — no-arg constructor
- `parseCompletion(String response) -> String` — strips leading thinking block

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when `THINKING_BLOCK_START_MARKER` is present at the start but `THINKING_BLOCK_END_MARKER` is never found (token budget exhausted mid-reasoning).
- `null` input is handled gracefully (returns empty string, no NPE).

#### Concurrency
- Stateless; all fields are `static final` constants. Thread-safe by design.
