### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:25:59Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 style chain‑of‑thought blocks from LLM completions, returning only the final answer.

#### Purpose
- Cleans raw LLM output by removing internal reasoning.
- Ensures only the model’s answer is persisted.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated with `@ToString`.

#### Input
- `parseCompletion(String response)` – raw completion text, may be `null`.
- No injected dependencies; uses only static constants.

#### Output
- Returns a trimmed answer string, never `null`.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
- If absent, throw `IOException` with detailed message.
- Return the text after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – cleans completion text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but not closed due to token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
