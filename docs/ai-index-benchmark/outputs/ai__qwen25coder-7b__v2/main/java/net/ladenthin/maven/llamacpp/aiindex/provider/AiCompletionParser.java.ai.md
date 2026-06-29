### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:31:35Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses LLM completion text to isolate the actual model answer.
- Strips internal reasoning blocks to ensure only the final answer is retained.
- Handles edge cases where the token budget is exhausted or no markers are present.

#### Type
public class AiCompletionParser

#### Input
- `response` (String): The raw completion text from the model, may be `null`.

#### Output
- Returns the cleaned answer text as a String, never `null`.
- Throws `IOException` if a thinking block was started but not completed.

#### Core logic
- Trims the input `response` if it is `null`.
- Searches for `THINKING_BLOCK_END_MARKER` to remove internal reasoning.
- If `THINKING_BLOCK_START_MARKER` is found without `THINKING_BLOCK_END_MARKER`, throws `IOException`.
- Returns the trimmed text after `THINKING_BLOCK_END_MARKER`.

#### Public API
- `parseCompletion(final String response) -> String`: Strips any Gemma-4 thinking block from the response and returns the clean answer text.

#### Dependencies
- `java.io.IOException`

#### Exceptions / Errors
- Throws `IOException` if the token budget is exhausted inside a thinking block.
