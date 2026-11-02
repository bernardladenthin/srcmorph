### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:32Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 model reasoning blocks from raw LLM completions, returning only the final answer.

#### Purpose
- Removes internal chain‑of‑thought blocks from AI responses.
- Ensures only user‑visible answer is persisted.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated with `@ToString`.

#### Input
- `String response` (raw LLM completion, may be `null`).

#### Output
- `String` – cleaned answer, never `null`.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise, locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If absent, throw `IOException` with diagnostic message.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – strip leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but not closed due to token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
