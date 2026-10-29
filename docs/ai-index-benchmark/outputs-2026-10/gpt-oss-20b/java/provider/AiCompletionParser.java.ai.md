### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:49:20Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to strip Gemma‑4 thinking blocks and return only the model’s answer.

#### Purpose
- Extracts the final answer from a raw LLM completion.
- Removes internal reasoning wrapped in Gemma‑4 thinking block markers.

#### Type
- Class `AiCompletionParser` (public, non‑final).
- Extends `Object`; no interfaces.
- Annotated with `@ToString`.

#### Input
- `parseCompletion(String response)` receives raw completion text (may be `null`).
- No injected dependencies.

#### Output
- Returns a non‑null trimmed answer string.
- Throws `IOException` if a thinking block starts but no closing marker is found.

#### Core logic
- If `response` is `null`, return empty string.
- Trim whitespace from `response`.
- If trimmed text does **not** start with `THINKING_BLOCK_START_MARKER`, return trimmed text.
- Otherwise:
  - Extract substring after the start marker.
  - Find first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If absent, throw `IOException` with detailed message.
  - Return substring after the end marker, trimmed.

#### Public API
- `AiCompletionParser()` → constructor, no side effects.
- `parseCompletion(String response) -> String` → strip leading thinking block, return answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when a thinking block starts but the end marker is missing, indicating token budget exhaustion.

#### Concurrency
- Stateless; thread‑safe. No mutable shared state.
