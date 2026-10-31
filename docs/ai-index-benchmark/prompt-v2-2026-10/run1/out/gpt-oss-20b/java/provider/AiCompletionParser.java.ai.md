### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:37:11Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a leading Gemma‑4 thinking block from LLM completions, returning only the model’s final answer.

#### Purpose
- Cleans raw LLM completion text by removing internal reasoning blocks.
- Ensures only the final answer is persisted in AI index files.

#### Type
`public class AiCompletionParser` – non‑final, extends `Object`. Annotated with `@ToString`.

#### Input
- `String response` (may be `null`) – raw completion text from the model.

#### Output
- `String` – cleaned answer text, never `null`.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace from `response`.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
- If absent, throw `IOException` with a detailed message.
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.  
- `String parseCompletion(String response) throws IOException` – removes leading thinking block, returns cleaned answer.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but the end marker is missing (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
