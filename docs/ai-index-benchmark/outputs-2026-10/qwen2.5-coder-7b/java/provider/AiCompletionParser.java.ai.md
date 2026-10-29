### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:17:07Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text to isolate the model's answer.
- Strips internal reasoning blocks to ensure only the final answer is stored.

#### Type
- Class
- Non-final

#### Input
- `response` (String): The raw completion text from the model, which may be `null`.

#### Output
- Returns the cleaned answer text as a String.
- Returns an empty String if the input is `null`.
- Throws an IOException if the token budget is exhausted inside the thinking block.

#### Core logic
- Trims the input response.
- Checks if the trimmed response starts with the `THINKING_BLOCK_START_MARKER`.
- If it does, it extracts the text after the start marker.
- Searches for the `THINKING_BLOCK_END_MARKER` in the extracted text.
- If found, it returns the text after the end marker, trimmed.
- If not found, it throws an IOException indicating the token budget was exhausted.

#### Public API
- `parseCompletion(final String response) -> String`: Strips a leading Gemma-4 thinking block from the response and returns the clean answer text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws an IOException if the token budget is exhausted inside the thinking block.

#### Concurrency
- This class is non-final to allow subclassing and mocking in tests, which implies it is not thread-safe.
