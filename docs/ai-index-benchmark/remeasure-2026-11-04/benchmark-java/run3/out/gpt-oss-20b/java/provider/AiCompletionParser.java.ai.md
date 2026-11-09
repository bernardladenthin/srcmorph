### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:48:05Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Extracts the model’s final answer from a raw LLM completion by removing any leading Gemma‑4 thinking block.

#### Purpose
- Parses LLM completion text.
- Strips internal reasoning before persisting the answer.

#### Type
`public class AiCompletionParser` – non‑final, annotated `@ToString`.

#### Input
- `String response` (may be `null`).

#### Output
- `String` – cleaned answer, never `null`.
- May throw `IOException` if a thinking block is incomplete.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise, locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If absent, throw `IOException` with guidance to increase token budget.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → construct parser.
- `String parseCompletion(String)` → clean answer, may throw IOException.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block starts but no closing marker is found; message advises increasing `maxOutputTokens`.

#### Concurrency
- Stateless; thread‑safe.
