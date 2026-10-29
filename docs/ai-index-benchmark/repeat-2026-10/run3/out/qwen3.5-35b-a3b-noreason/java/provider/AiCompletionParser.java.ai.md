### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:53Z
- G: 
- A: 0.0.0
- X: file
---
> Strips internal chain-of-thought reasoning blocks from raw LLM completion text to extract the final model answer.

#### Purpose
- Extracts clean model answers from raw LLM responses by removing Gemma-4 specific thinking blocks.
- Prevents internal reasoning data from being persisted in AI index files.

#### Type
- `public class AiCompletionParser`
- Annotated with `@ToString` (Lombok).
- Non-final to allow subclassing and mocking.

#### Input
- `response` (String): Raw completion text from the model, may be `null`.

#### Output
- Returns `String`: Cleaned answer text (never `null`).
- Throws `IOException`: If a thinking block starts but the closing marker is missing (token budget exhausted).

#### Core logic
- Treats `null` input as an empty string.
- Trims the input response.
- Checks if the trimmed response starts with `THINKING_BLOCK_START_MARKER` (`<|channel>thought`).
- If no start marker is found, returns the trimmed response as-is.
- If start marker is found:
  - Locates the first occurrence of `THINKING_BLOCK_END_MARKER` (`<channel|>`) within the remaining text.
  - If the end marker is missing, throws `IOException` with details about token budget exhaustion.
  - If the end marker is found, extracts the substring immediately following the marker.
  - Trims and returns the extracted substring.

#### Public API
- `parseCompletion(response: String) -> String`
  - Strips leading thinking block and returns clean answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException`: Thrown when `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is absent, indicating truncated reasoning.

#### Concurrency
- No concurrency, synchronization, or thread-safety notes present.
