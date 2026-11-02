### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:53:44Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a leading Gemma‑4 thinking block from LLM completions, returning only the model’s final answer.

#### Purpose
- Removes internal reasoning from Gemma‑4 responses before persisting.

#### Type
`public class AiCompletionParser` – class, non‑final, annotated `@ToString`.

#### Input
- `String response` (may be `null`) – raw LLM completion text.

#### Output
- `String` – cleaned answer, never `null`; trimmed; may contain markers if not at start.

#### Core logic
- If `response` is `null`, return empty string.
- Trim input; if it does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Extract substring after the start marker.
- Find first occurrence of `THINKING_BLOCK_END_MARKER`.
- If absent, throw `IOException` with message about token budget exhaustion.
- Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` – no‑op constructor.  
- `String parseCompletion(String response) throws IOException` – strip leading thinking block.

#### Dependencies
- `java.io.IOException`  
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a start marker is present but no end marker is found, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe.
