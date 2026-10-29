### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:37:02Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Type
- Class; final; non-final to allow subclassing and mocking in tests; @ToString.
- Key constants: THINKING_BLOCK_START_MARKER, THINKING_BLOCK_END_MARKER.
- Extends: Object; Implements: None.

#### Input
- Constructor: no-arg constructor (no-op).
- Method: parseCompletion(final String response) - the raw completion text from the model, may be null.

#### Output
- Method: parseCompletion returns String - the cleaned answer text, never null.
- Throws IOException if a leading thinking block was started but the token budget was exhausted before the closing marker was emitted.

#### Core logic
- If response is null, return empty string.
- Trim the response; if it does not start with THINKING_BLOCK_START_MARKER, return trimmed text.
- Strip the start marker; locate the first end marker after it.
- If end marker is absent, throw IOException with actionable message.
- Return the text after the end marker, trimmed.

#### Public API
- AiCompletionParser() -> void (creates a new AiCompletionParser)
- parseCompletion(final String response) -> String (cleans answer text, throws IOException if thinking block started but not closed)
- THINKING_BLOCK_START_MARKER -> String ("<|channel>thought")
- THINKING_BLOCK_END_MARKER -> String ("<channel|>")

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException thrown if a leading thinking block was started but the token budget was exhausted before the closing marker was emitted.

#### Concurrency
- No threading, synchronization, immutability, or thread-safety notes.
