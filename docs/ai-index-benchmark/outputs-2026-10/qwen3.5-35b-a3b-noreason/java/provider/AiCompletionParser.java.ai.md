### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:10Z
- G: 
- A: 0.0.0
- X: file
---
> Strips internal chain-of-thought reasoning blocks from raw LLM completion text to extract the final model answer.

#### Purpose
- Extracts clean model answers by removing Gemma-4 specific thinking blocks from raw completion responses.
- Prevents internal reasoning data from being persisted in AI index files.

#### Type
- `public class AiCompletionParser`
- Annotated with `@ToString` (Lombok)
- Non-final to allow subclassing and mocking in tests.

#### Input
- `response` (String): Raw completion text from the model, may be `null`.

#### Output
- Returns `String`: Cleaned answer text (never `null`, empty string if input is `null`).
- Throws `IOException`: If a thinking block starts but the closing marker is missing (token budget exhausted).

#### Core logic
- Treats `null` input as an empty string.
- Trims the input response to remove leading/trailing whitespace.
- Checks if the trimmed response starts with `THINKING_BLOCK_START_MARKER` (`<|channel>thought`).
- If no start marker is found, returns the trimmed response immediately.
- If start marker is present:
  - Substrings the text after the start marker.
  - Searches for the first occurrence of `THINKING_BLOCK_END_MARKER` (`<channel|>`).
  - If the end marker is missing, throws `IOException` with details about token budget exhaustion.
  - If the end marker is found, returns the substring following the end marker, trimmed.

#### Public API
- `parseCompletion(String response) -> String`
  - Strips leading thinking block and returns clean answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `net.ladenthin.srcmorph.provider` (package)

#### Exceptions / Errors
- `IOException`: Thrown when `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is absent, indicating truncated reasoning due to token limits.
- Null handling: `null` input is converted to an empty string `""`.

#### Concurrency
- No concurrency mechanisms or thread-safety notes present.
- Stateless class with no mutable fields.
