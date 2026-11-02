### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:03:36Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 style chain‑of‑thought blocks from LLM completions, returning only the final answer.

#### Purpose
- Removes internal reasoning from model responses.
- Ensures only user‑visible answer is persisted.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `String response` (may be `null`).
- No injected dependencies.

#### Output
- `String` – cleaned answer, never `null`.
- May throw `IOException` if a thinking block is incomplete.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
- If absent, throw `IOException` with diagnostic message.
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructs parser.
- `String parseCompletion(String response) throws IOException` – cleans completion.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a start marker is present but no end marker is found (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
