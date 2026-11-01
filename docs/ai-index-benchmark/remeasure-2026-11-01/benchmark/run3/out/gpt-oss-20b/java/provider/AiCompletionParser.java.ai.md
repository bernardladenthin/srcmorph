### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:27Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 model thinking blocks from raw LLM completions, returning only the final answer.

#### Purpose
- Cleans LLM completion text by removing internal reasoning blocks.
- Ensures only the model’s final answer is persisted.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated with `@ToString`.

#### Input
- `String response` (may be `null`) – raw completion text from the model.

#### Output
- `String` – cleaned answer text, never `null`.
- Throws `IOException` if a thinking block is started but not closed.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
- If absent, throw `IOException` with diagnostic message.
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – removes leading thinking block, returns clean answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but the end marker is missing (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
