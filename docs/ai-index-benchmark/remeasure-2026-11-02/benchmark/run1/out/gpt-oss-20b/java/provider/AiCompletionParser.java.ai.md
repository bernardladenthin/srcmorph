### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:22Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Extracts the final answer from a Gemma‑4 LLM completion by removing the internal thinking block.

#### Purpose
- Cleans LLM responses for storage.
- Ensures only the model’s final answer is persisted.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated with `@ToString`.

#### Input
- `parseCompletion(String response)` – raw completion text, may be `null`.

#### Output
- `String` – trimmed answer, never `null`.
- Throws `IOException` if a thinking block is started but not closed.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`; if absent, throw `IOException` with diagnostic message.
- Return text after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.  
- `String parseCompletion(String response) throws IOException` – strip leading thinking block.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
