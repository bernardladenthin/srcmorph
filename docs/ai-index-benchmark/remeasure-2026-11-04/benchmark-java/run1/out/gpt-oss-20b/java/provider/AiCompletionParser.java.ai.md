### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:15:18Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Extracts the final answer from a Gemma‑4 LLM completion by removing the leading thinking block.

#### Purpose
- Cleans raw LLM completion text.
- Removes internal reasoning before persisting the answer.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `parseCompletion(String response)` – raw completion text, may be `null`.

#### Output
- `String` – trimmed answer text, never `null`.
- Throws `IOException` if a thinking block starts but no closing marker appears.

#### Core logic
- Return empty string if input is `null`.
- Trim input.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise:
  - Extract substring after the start marker.
  - Find first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If absent, throw `IOException` with guidance to increase token budget.
  - Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – strip leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but ends abruptly (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
