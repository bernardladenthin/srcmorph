### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:10Z
- G: 
- A: 0.0.0
- X: file
---
> Strips leading Gemma-4 chain-of-thought blocks from raw LLM completion text to extract the clean model answer for persistence.

#### Purpose
- Parses raw LLM completion strings to remove internal reasoning (thinking blocks) before storing results in an AI index.
- Handles edge cases where the model's token budget is exhausted mid-reasoning.

#### Type
- `class AiCompletionParser`
- Modifiers: `public` (non-final, explicitly subclassable for testing)
- Annotations: `@ToString` (Lombok)

#### Input
- `parseCompletion(String response)`: Raw completion text from the model; may be `null`.

#### Output
- `parseCompletion` returns `String`: Cleaned answer text, never `null`.
- Throws `IOException` if a thinking block is opened but not closed.

#### Core logic
- Returns empty string `""` if input is `null`.
- Trims the input; if it does not start with `THINKING_BLOCK_START_MARKER`, returns the trimmed string as-is.
- If it starts with `THINKING_BLOCK_START_MARKER`:
  - Extracts substring after the start marker.
  - Finds the first occurrence of `THINKING_BLOCK_END_MARKER` in that substring.
  - If end marker is absent (`indexOf` returns -1), throws `IOException` with a message advising to increase `maxOutputTokens`.
  - Otherwise, returns the substring after the end marker, trimmed.
- Only strips thinking blocks at the **start** of the response; markers appearing later are preserved as part of the answer.

#### Public API
- `AiCompletionParser()` -> `void` — no-op constructor.
- `parseCompletion(String response)` -> `String` — strips leading thinking block.
- `THINKING_BLOCK_START_MARKER` -> `String` — constant `"<|channel>thought"`.
- `THINKING_BLOCK_END_MARKER` -> `String` — constant `"<channel|>"`.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is missing (token budget exhausted).
- `null` input handled gracefully (returns `""`).
