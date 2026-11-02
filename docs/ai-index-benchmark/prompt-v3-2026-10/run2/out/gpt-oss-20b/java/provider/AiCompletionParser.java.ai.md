### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:18:25Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a leading Gemma‑4 thinking block from a raw LLM completion, returning only the model’s final answer.

#### Purpose
- Cleans LLM responses by removing internal reasoning blocks.
- Ensures only the final answer is persisted in AI index files.

#### Type
`public class AiCompletionParser` – non‑final, annotated `@ToString`.

#### Input
- `String response` (raw completion, may be `null`).
- No injected dependencies; uses only static constants.

#### Output
- `String` containing the cleaned answer (never `null`).
- Throws `IOException` if a thinking block is started but not closed.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise, locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If no end marker is found, throw `IOException` with a detailed message.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.  
- `String parseCompletion(String response) throws IOException` – cleans a raw completion.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but the closing marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
