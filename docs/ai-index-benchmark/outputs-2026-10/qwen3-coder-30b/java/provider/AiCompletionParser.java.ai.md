### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:13:36Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping internal thinking blocks before storing in an AI index file.

#### Purpose
- Strips Gemma-4 thinking blocks from LLM responses
- Extracts clean model answers for storage in AI index files

#### Type
class, non-final, implements no interfaces, no generics, no type bounds, @ToString annotation

#### Input
- Constructor takes no parameters
- parseCompletion method takes a String response parameter
- Reads static final fields THINKING_BLOCK_START_MARKER and THINKING_BLOCK_END_MARKER

#### Output
- parseCompletion returns a String (cleaned answer text)
- Returns empty string for null input
- Throws IOException for incomplete thinking blocks

#### Core logic
- Checks if input response is null and returns empty string
- Trims input response
- If response doesn't start with thinking block marker, returns trimmed response
- If response starts with thinking block marker, finds first end marker
- If end marker not found, throws IOException with actionable message
- If end marker found, returns text after end marker, trimmed

#### Public API
- parseCompletion(final String response) -> String throws IOException
- Constructor AiCompletionParser() -> void

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException thrown when thinking block start marker found but end marker absent
- IOException includes actionable message about increasing maxOutputTokens
- Null input handled gracefully by returning empty string

#### Concurrency
- No concurrency considerations mentioned
- Class appears immutable and thread-safe for read operations
