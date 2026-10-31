### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:38:19Z
- G: 
- A: 0.0.0
- X: file
---
> Strips internal chain-of-thought reasoning blocks from raw LLM completion text to extract the final model answer.

#### Purpose
* Extracts the actual model answer by removing content wrapped between specific thinking block markers.
* Prevents internal reasoning from being persisted in AI index files.

#### Type
class; @ToString

#### Input
* `response` (String): The raw completion text from the model, can be null.

#### Output
* Cleaned answer text (String).
* Throws `IOException` if a thinking block is opened but not closed.

#### Core logic
* Trims the input `response`.
* Checks if the trimmed text starts with `THINKING_BLOCK_START_MARKER`.
* If the start marker is present, searches for the first occurrence of `THINKING_BLOCK_END_MARKER`.
* If the end marker is missing, throws `IOException` indicating token budget exhaustion.
* If the end marker is found, returns the substring following the marker, trimmed.
* Returns the original trimmed text if no start marker is detected.

#### Public API
* `AiCompletionParser()` -> `void` Constructor.
* `parseCompletion(String)` -> `String` Extracts clean answer from raw response.

#### Dependencies
* `java.io.IOException`
* `lombok.ToString`

#### Exceptions / Errors
* `IOException`: Thrown when `THINKING_BLOCK_START_MARKER` is found but `THINKING_BLOCK_END_MARKER` is missing (indicates truncated response).
* Handles `null` input by returning an empty string.

#### Concurrency
* Stateless implementation; thread-safe.
