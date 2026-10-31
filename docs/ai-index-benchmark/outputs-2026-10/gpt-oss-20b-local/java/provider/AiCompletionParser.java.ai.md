### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:11:33Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts the final answer from a Gemma‑4 LLM completion by removing the leading chain‑of‑thought block.

#### Purpose
- Parses raw LLM completion text.
- Removes internal reasoning before persisting the answer.

#### Type
- Class `AiCompletionParser` (public, non‑final).
- Annotated with `@ToString`.

#### Input
- `parseCompletion(String response)`:
  - `response`: raw completion text, may be `null`.

#### Output
- `parseCompletion` returns a non‑null trimmed answer string.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace from `response`.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return it.
- Otherwise:
  - Remove the start marker.
  - Find the first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If absent, throw `IOException` with guidance to increase token budget.
  - Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → constructs parser.
- `String parseCompletion(String response) throws IOException` → cleans completion.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but not closed due to token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
