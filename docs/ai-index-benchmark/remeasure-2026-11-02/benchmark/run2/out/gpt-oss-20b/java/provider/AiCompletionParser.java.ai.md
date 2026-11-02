### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:43Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Extracts the final answer from a Gemma‑4 LLM completion by removing the internal thinking block.

#### Purpose
- Parses raw LLM completion text.
- Removes leading Gemma‑4 thinking block before persisting answer.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated with `@ToString`.

#### Input
- `parseCompletion(String response)` – raw completion text, may be `null`.

#### Output
- `parseCompletion` returns a non‑null trimmed answer string.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise, locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If absent, throw `IOException` with a message about token budget exhaustion.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – strip leading thinking block, return clean answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but the end marker is missing.

#### Concurrency
- Stateless; thread‑safe.
