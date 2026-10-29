### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:50Z
- G: 
- A: 0.0.0
- X: file
---
> Strips internal chain-of-thought reasoning blocks from raw LLM completion text to extract the final model answer.

#### Purpose
- Extracts clean model answers by removing Gemma-4 specific thinking block markers from raw completion responses.
- Validates that thinking blocks are properly closed to prevent persisting incomplete reasoning data.

#### Type
- `public class AiCompletionParser` (non-final, allows subclassing)
- Annotated with `@ToString` (Lombok)
- No explicit `extends` or `implements` clauses.

#### Input
- `response` (String): Raw completion text from an LLM, may be `null`.

#### Output
- Returns `String`: The cleaned answer text (never `null`, empty string if input is `null`).
- Throws `IOException`: If a thinking block starts but the closing marker is missing (token budget exhausted).

#### Core logic
- Treats `null` input as an empty string and returns immediately.
- Trims whitespace from the input response.
- Checks if the trimmed response starts with `THINKING_BLOCK_START_MARKER` (`<|channel>thought`).
- If the start marker is absent, returns the trimmed response as-is (preserving any markers found later in the text).
- If the start marker is present:
  - Substrings the text after the start marker.
  - Searches for the first occurrence of `THINKING_BLOCK_END_MARKER` (`<channel|>`).
  - If the end marker is missing, throws `IOException` with details about token budget exhaustion.
  - If the end marker is found, returns the substring following the end marker, trimmed.

#### Public API
- `AiCompletionParser()` -> Constructor initializes the parser.
- `parseCompletion(String response) -> String` -> Strips thinking blocks and returns clean answer.
- `THINKING_BLOCK_START_MARKER` -> Constant for Gemma-4 thought opening tag.
- `THINKING_BLOCK_END_MARKER` -> Constant for Gemma-4 thought closing tag.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `net.ladenthin.srcmorph.provider` (package)

#### Exceptions / Errors
- `IOException`: Thrown when `THINKING_BLOCK_START_MARKER` is found but `THINKING_BLOCK_END_MARKER` is missing, indicating truncated output.
- Null handling: `null` input is converted to an empty string `""`.

#### Concurrency
- No concurrency, synchronization, or thread-safety mechanisms present.
- State is immutable (no fields).
