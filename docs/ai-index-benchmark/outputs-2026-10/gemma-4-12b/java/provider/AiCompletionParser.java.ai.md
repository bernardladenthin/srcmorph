### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:38:27Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts the final model answer from raw LLM completions by stripping out internal chain-of-thought reasoning blocks.

#### Purpose
* Parses raw LLM completion text to isolate the final answer from internal reasoning.
* Handles specific Gemma-4 style thinking blocks delimited by start and end markers.

#### Type
class AiCompletionParser

#### Input
* `response` (String): The raw completion text from the model, which may be null.

#### Output
* `String`: The cleaned answer text (never null).
* Side effect: Throws `IOException` if a thinking block is started but not closed due to token limits.

#### Core logic
* Checks if the input string is null; returns an empty string if so.
* Trims the input response.
* Detects if the response starts with the `THINKING_BLOCK_START_MARKER`.
* If a thinking block is detected:
    * Locates the first occurrence of `THINKING_BLOCK_END_MARKER` within the remaining text.
    * If the end marker is missing, throws an `IOException` indicating the token budget was exhausted.
    * If the end marker is found, returns the substring following that marker, trimmed.
* If no thinking block is detected at the start, returns the trimmed response as-is.

#### Public API
* `AiCompletionParser()` -> void (Constructor)
* `THINKING_BLOCK_START_MARKER` (String) (Constant)
* `THINKING_BLOCK_END_MARKER` (String) (Constant)
* `parseCompletion(String response)` -> String (Parses and cleans the completion text)

#### Dependencies
* `java.io.IOException`
* `lombok.ToString`

#### Exceptions / Errors
* `IOException`: Thrown when `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is missing, signifying a truncated response.
* Handles `null` input by returning an empty string.

#### Concurrency
* Thread-safe (stateless logic).
