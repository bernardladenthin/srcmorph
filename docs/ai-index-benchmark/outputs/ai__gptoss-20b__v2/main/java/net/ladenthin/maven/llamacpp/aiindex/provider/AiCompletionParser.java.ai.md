### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:10:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses LLM completion text to strip internal thinking blocks and retrieve the final answer.

#### Purpose
- Parses Gemma‑4 responses, removing internal chain‑of‑thought reasoning.
- Provides a clean answer string for AI indexing.

#### Type
public class AiCompletionParser @ToString

#### Input
- `parseCompletion(String response)` receives raw completion text, may be `null`.

#### Output
- Returns a non‑null trimmed answer string.
- Throws `IOException` if a thinking block is opened but not closed.

#### Core logic
- If `response` is `null`, return empty string.
- Find last index of `THINKING_BLOCK_END_MARKER`.
- If found, return substring after the end marker, trimmed.
- If not found but `THINKING_BLOCK_START_MARKER` exists, throw `IOException` with actionable message.
- Otherwise return trimmed `response`.

#### Public API
- `parseCompletion(String response) -> String cleaned answer` (throws IOException if thinking block incomplete)

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` if `THINKING_BLOCK_START_MARKER` is present without a corresponding `THINKING_BLOCK_END_MARKER`; message suggests increasing token budget.
