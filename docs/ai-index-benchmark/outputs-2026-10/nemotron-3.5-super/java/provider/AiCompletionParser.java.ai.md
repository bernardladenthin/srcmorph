### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:43:22Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to strip leading Gemma-4 thinking blocks and return only the model answer.

#### Purpose
- Extracts the clean model answer from raw LLM completion text by removing internal reasoning blocks.
- Prevents persistence of chain-of-thought reasoning from AiCompletionParser.

#### Type
- public non-final class AiCompletionParser
- @ToString (Lombok)
- No superclass; no interfaces

#### Input
- Constructor AiCompletionParser(): no parameters
- parseCompletion(final String response): raw completion text, may be null
- Static constants THINKING_BLOCK_START_MARKER and THINKING_BLOCK_END_MARKER

#### Output
- Returns cleaned answer text, never null
- Returns empty string for null input
- Throws IOException when a leading thinking block lacks the closing marker
- Stateless; no mutated fields or written resources

#### Core logic
- parseCompletion treats null response as empty and returns ""
- Trims the response before marker detection
- If trimmed text does not start with THINKING_BLOCK_START_MARKER, returns trimmed text unchanged
- If it starts with THINKING_BLOCK_START_MARKER, searches for the first THINKING_BLOCK_END_MARKER after the start marker
- If the end marker is absent, throws IOException with an actionable message and response length
- If the end marker is present, returns the substring after it, trimmed

#### Public API
- AiCompletionParser() -> AiCompletionParser, creates a new parser instance
- parseCompletion(String) -> String, strips leading thinking block or returns trimmed input

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException thrown when THINKING_BLOCK_START_MARKER is present but THINKING_BLOCK_END_MARKER is missing
- null input is treated as empty response, no exception

#### Concurrency
- Stateless with only static final constants; safe for concurrent use by multiple threads
