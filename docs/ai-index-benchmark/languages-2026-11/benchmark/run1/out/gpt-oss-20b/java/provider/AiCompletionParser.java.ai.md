### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:38Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a Gemma‑4 model’s internal thinking block from a raw LLM completion, leaving only the final answer.

#### Purpose
- Removes internal reasoning from LLM responses.
- Ensures only the model’s final answer is persisted.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated with `@ToString`.

#### Input
- `String response` (raw completion, may be `null`).
- No injected dependencies.

#### Output
- `String` – cleaned answer, never `null`.
- May throw `IOException` if a thinking block is started but not closed.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise, locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If absent, throw `IOException` with a detailed message.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – strip leading thinking block, return cleaned answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a thinking block is started but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
