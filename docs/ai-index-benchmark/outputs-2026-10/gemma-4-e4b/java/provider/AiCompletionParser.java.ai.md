### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:56:47Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw large language model completion text to isolate and return only the final answer by stripping internal reasoning blocks.

#### Purpose
*   Parses raw LLM completion text to extract the model answer.
*   Handles Gemma-4 specific thinking block formatting.

#### Type
class AiCompletionParser

#### Input
*   `parseCompletion(String response)`: Raw completion text from the model.

#### Output
*   `parseCompletion(String response)`: Cleaned answer text (String), never null.

#### Core logic
*   Checks if input `response` is null; returns "" if true.
*   Trims `response` to create `trimmed`.
*   If `trimmed` does not start with `THINKING_BLOCK_START_MARKER`, returns `trimmed`.
*   If it starts with the start marker, extracts the substring following it (`thinking`).
*   Finds the index of `THINKING_BLOCK_END_MARKER` within `thinking`.
*   If the end marker is not found (`thinkingEnd < 0`), throws `IOException`.
*   If found, returns the substring following the end marker, trimmed.

#### Public API
parseCompletion(String response) -> String throws IOException: Strips leading thinking block from response.

#### Dependencies
java.io.IOException
lombok.ToString

#### Exceptions / Errors
*   Throws IOException if `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is absent.
*   Handles null input by returning an empty string.

#### Concurrency
No explicit concurrency control mechanisms present.
