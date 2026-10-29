### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:52:55Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a Gemma‑4 model’s internal thinking block from a raw LLM completion, returning only the final answer.

#### Purpose
- Extracts the model’s answer from a raw completion.
- Removes internal reasoning wrapped in a thinking block.

#### Type
- Class `AiCompletionParser` (public, non‑final).
- Extends `Object`.
- Annotated with `@ToString`.

#### Input
- `parseCompletion(String response)`:
  - `response`: raw completion text, may be `null`.

#### Output
- `parseCompletion` returns a non‑null trimmed answer string.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim `response`.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise:
  - Extract substring after the start marker.
  - Find first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If not found, throw `IOException` with explanatory message.
  - Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → constructs parser.
- `parseCompletion(String response) -> String` → cleans completion.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but not closed due to token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
