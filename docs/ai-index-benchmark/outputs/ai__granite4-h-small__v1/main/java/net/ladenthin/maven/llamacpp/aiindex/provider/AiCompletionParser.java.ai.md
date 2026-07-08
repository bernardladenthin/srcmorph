### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-07-02T22:05:38Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 5; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 2

> Extracts the final answer from a Gemma‑4 LLM response by removing any internal chain‑of‑thought block.

#### Purpose
- Parses raw LLM completion text.
- Strips internal reasoning blocks before persisting answers.

#### Type
- Class `AiCompletionParser`, non‑final, public.
- Annotated `@ToString`.

#### Input
- `parseCompletion(String response)`: raw completion text, may be `null`.
- Constructor requires no arguments.

#### Output
- Returns a non‑null trimmed string containing only the final answer.
- May throw `IOException` if a thinking block is incomplete.

#### Core logic
- If `response` is `null`, return empty string.
- Find last index of `THINKING_BLOCK_END_MARKER`.
  - If found, return substring after marker, trimmed.
- If no end marker but start marker present, throw `IOException` with explanatory message.
- Otherwise, return trimmed original response.

#### Public API
- `AiCompletionParser() -> no-op constructor`
- `parseCompletion(String) -> String` – parse and clean response

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a thinking block starts but does not close.

#### Concurrency
- Stateless; thread‑safe for concurrent use.
