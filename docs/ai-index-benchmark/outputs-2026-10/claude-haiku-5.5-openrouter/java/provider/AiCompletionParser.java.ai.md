### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:49Z
- G: 
- A: 0.0.0
- X: file
---
> Strips a Gemma-4 leading chain-of-thought thinking block from raw LLM completion text and returns the clean model answer.

#### Purpose
- Extracts the final model answer from raw LLM output by removing a leading internal reasoning block before persisting to an AI index file.
- Guards against persisting truncated reasoning by raising an error when the closing marker is missing.

#### Type
- Class, public, non-final (explicitly allowed for subclassing and mocking).
- Lombok `@ToString` annotation.
- Implicit public no-arg constructor (explicitly declared, empty body).

#### Input
- `parseCompletion(String response)`: raw completion text from the model, may be `null`.
- Constants read: `THINKING_BLOCK_START_MARKER` ("<|channel>thought"), `THINKING_BLOCK_END_MARKER` ("<channel|>").

#### Output
- `String` cleaned answer text, trimmed, never `null`.
- `IOException` on unterminated thinking block.
- No fields mutated; no resources written.

#### Core logic
- Null input returns empty string.
- Trim the response.
- If trimmed text does not start with the start marker, return it unchanged (markers inside are kept as text).
- If it starts with the start marker, search for the first end marker in the text following the start marker.
- If no end marker is found, throw `IOException` with message advising to increase `maxOutputTokens` and including the original response length.
- If found, return the substring after the end marker, trimmed.
- Only a leading block is recognized; mid-text markers are not stripped.

#### Public API
- `AiCompletionParser()` -> constructor, no-op.
- `parseCompletion(String response) -> String` throws IOException: extracts answer, strips leading thinking block.
- `THINKING_BLOCK_START_MARKER` -> `String` constant: opening thinking token.
- `THINKING_BLOCK_END_MARKER` -> `String` constant: closing thinking token.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- Referenced: `AiCompletionParser` constants, `String.trim`, `String.startsWith`, `String.substring`, `String.indexOf`.

#### Exceptions / Errors
- `IOException` thrown when start marker present but end marker absent (token budget exhausted).
- `null` input handled, returns `""`; no NullPointerException.
- Error message includes raw response length in characters.

#### Concurrency
- Stateless; no fields. Safe for concurrent use across threads.
