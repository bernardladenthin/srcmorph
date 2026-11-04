### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:10Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Strips Gemma‑4 style chain‑of‑thought blocks from LLM completions, returning only the final answer.

#### Purpose
- Extracts the model’s answer from raw completion text.
- Removes internal reasoning blocks before persisting to an AI index.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `parseCompletion(String response)` – raw LLM completion, may be `null`.

#### Output
- `parseCompletion` returns a non‑null trimmed answer string.
- Throws `IOException` if a thinking block starts but no closing marker appears.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Remove the leading marker, locate the first `THINKING_BLOCK_END_MARKER`.
- If the end marker is missing, throw `IOException` with a detailed message.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.  
- `String parseCompletion(String response) throws IOException` – clean answer extraction.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but ends abruptly (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
