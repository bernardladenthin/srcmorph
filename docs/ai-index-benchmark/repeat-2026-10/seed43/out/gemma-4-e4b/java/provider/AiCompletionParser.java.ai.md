### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:56:43Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw large language model completion text to isolate and return the final answer by removing internal reasoning blocks.

#### Purpose
* Provides utility for cleaning LLM responses.
* Implements logic to strip model-internal thinking markers.

#### Type
class AiCompletionParser; @ToString

#### Input
* Constructor: No parameters.
* Method `parseCompletion(String response)`: `response` (raw completion text, may be null).

#### Output
* Method `parseCompletion(String response)`: `String` (cleaned answer text, never null).

#### Core logic
* Checks if input `response` is null; returns empty string if so.
* Trims the input `response`.
* If trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns trimmed response.
* If it starts with the start marker, it searches for the first occurrence of `THINKING_BLOCK_END_MARKER` within the remainder of the string.
* If the end marker is not found, throws `IOException` indicating budget exhaustion.
* If found, returns the substring immediately following the end marker, trimmed.

#### Public API
parseCompletion(String response) -> String: Strips leading thinking block from response.

#### Dependencies
java.io.IOException, lombok.ToString

#### Exceptions / Errors
* Throws IOException if `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is absent.
* Handles null input by returning an empty string.

#### Concurrency
No explicit concurrency mechanisms present; class is not marked as thread-safe or unsafe.
