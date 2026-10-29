### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:57:45Z
- G: 
- A: 0.0.0
- X: file
---
> Strips leading Gemma-4 chain-of-thought blocks from raw LLM completion text to extract the clean model answer for persistence.

#### Purpose
- Parses raw LLM completion strings to remove internal reasoning (thinking blocks) before storing results in an AI index.
- Handles token-budget exhaustion scenarios where the thinking block is incomplete.

#### Type
- `public class AiCompletionParser`
- Non-final (allows subclassing/mocking)
- Annotated with `@ToString` (Lombok)

#### Input
- `parseCompletion(String response)`: Raw completion text from the model; may be `null`.

#### Output
- `String`: Cleaned answer text, never `null`.
- Throws `IOException` if thinking block is incomplete.

#### Core logic
- Returns empty string if input is `null`.
- Trims input; if it does not start with `THINKING_BLOCK_START_MARKER`, returns trimmed text as-is.
- If starts with `THINKING_BLOCK_START_MARKER`:
  - Extracts substring after start marker.
  - Finds first occurrence of `THINKING_BLOCK_END_MARKER`.
  - If end marker absent, throws `IOException` with actionable message about token budget.
  - Returns text after end marker, trimmed.
- Only strips thinking block if it appears at the very start of the response; markers elsewhere are preserved as part of the answer.

#### Public API
- `AiCompletionParser()` -> `void` — No-arg constructor.
- `parseCompletion(String response)` -> `String` — Strips leading thinking block.
- `THINKING_BLOCK_START_MARKER` -> `String` — Static constant for block start.
- `THINKING_BLOCK_END_MARKER` -> `String` — Static constant for block end.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is missing (token budget exhausted).
- `null` input treated as empty string, no exception.
