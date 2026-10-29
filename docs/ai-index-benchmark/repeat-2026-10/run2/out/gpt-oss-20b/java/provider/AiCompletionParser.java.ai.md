### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:12:44Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 model thinking blocks from raw LLM completions, returning only the final answer.

#### Purpose
- Removes internal reasoning from AI completion text.
- Ensures only the model’s answer is persisted.

#### Type
- Class `AiCompletionParser` (public, non‑final).
- Annotated with `@ToString`.

#### Input
- `parseCompletion(String response)` receives raw completion text (may be `null`).

#### Output
- Returns trimmed answer string (never `null`).
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
- If absent, throw `IOException` with diagnostic message.
- Return text after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `parseCompletion(String response) -> String` – strip leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but not closed due to token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
