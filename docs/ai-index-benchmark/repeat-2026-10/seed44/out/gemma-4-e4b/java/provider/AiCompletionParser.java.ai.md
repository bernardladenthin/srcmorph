### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:03:38Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw large language model completion text to isolate and return the final answer by removing internal reasoning blocks.

#### Purpose
* Provides utility for extracting clean model answers from raw LLM output.
* Manages the parsing logic for Gemma-4 style thinking blocks.

#### Type
class AiCompletionParser

#### Input
* Constructor: No parameters.
* Method `parseCompletion(String response)`: Raw completion text from the model.

#### Output
* Method `parseCompletion(String response)`: Cleaned answer text (String), never null.

#### Core logic
* Checks if input `response` is null; returns empty string if so.
* Trims the input `response`.
* If trimmed response does not start with `THINKING_BLOCK_START_MARKER`, returns the trimmed response.
* If it starts with the start marker, it searches for the first occurrence of `THINKING_BLOCK_END_MARKER` within the remainder of the string.
* If the end marker is not found, throws `IOException` indicating token budget exhaustion.
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
