### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:04:32Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma‑4 model reasoning blocks from LLM completions, returning only the final answer.

#### Purpose
- Removes internal “thinking” blocks from model output.
- Ensures only the answer is persisted in AI index files.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `parseCompletion(String response)` – raw LLM completion text (may be `null`).

#### Output
- `String` – cleaned answer, never `null`.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`; if absent, throw `IOException` with detailed message.
- Return text after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – strip leading thinking block, return answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but the end marker is missing (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
