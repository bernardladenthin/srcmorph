### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:37Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Parses raw LLM completion text to strip Gemma‑4 thinking blocks and return only the model’s final answer.

#### Purpose
- Extracts the answer from a raw LLM completion.
- Removes internal reasoning blocks before persisting.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `String response` (may be `null`) – raw completion text.

#### Output
- `String` – cleaned answer, never `null`.
- Throws `IOException` if a thinking block is started but not closed.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace; if it does not start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise, locate the first `THINKING_BLOCK_END_MARKER` after the start marker.
- If absent, throw `IOException` with a detailed message.
- Return the substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – constructor, no-op.
- `String parseCompletion(String response) throws IOException` – strip thinking block, return answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` when a thinking block is started but the closing marker is missing (token budget exhausted).

#### Concurrency
- Stateless; thread‑safe.
