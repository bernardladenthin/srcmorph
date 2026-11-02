### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:40:31Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a leading Gemma‑4 thinking block from LLM completions, returning only the model’s final answer.

#### Purpose
- Cleans raw LLM completion text.
- Removes internal reasoning wrapped in a Gemma‑4 thinking block.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `String response` (may be `null`).
- No injected dependencies.

#### Output
- `String` containing the cleaned answer (never `null`).
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace from `response`.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise:
  - Extract substring after the start marker.
  - Find first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If absent, throw `IOException` with a detailed message.
  - Return the text after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – cleans completion text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
