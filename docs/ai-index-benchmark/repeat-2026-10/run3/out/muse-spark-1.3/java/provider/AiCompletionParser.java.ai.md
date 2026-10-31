### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:21:45Z
- G: 
- A: 0.0.0
- X: file
---
> Strips LLM chain-of-thought thinking blocks to persist only the clean model answer for AI indexing.
#### Purpose
- Parses raw LLM completion text into storable AI index answer
- Strips Gemma-4 thinking-block reasoning before persisting result
#### Type
- class, non-final, allows subclassing/mocking
- extends Object implicit; implements none; no generics
- Notable annotation: Lombok @ToString on AiCompletionParser
#### Input
- response String raw completion text in parseCompletion, may be null
- THINKING_BLOCK_START_MARKER String `<|channel>thought` leading-block detector
- THINKING_BLOCK_END_MARKER String `<channel|>` block terminator
- AiCompletionParser() no-arg constructor, no dependencies injected
#### Output
- parseCompletion -> String cleaned answer text, never null
- null input produces empty String output
- Non-block response produces trimmed response output
#### Core logic
- AiCompletionParser.parseCompletion returns "" for null response
- Trims response, returns trimmed text if not starting with THINKING_BLOCK_START_MARKER
- Recognizes thinking block only at start, preserves later markers as answer text
- Removes start marker through first THINKING_BLOCK_END_MARKER inclusive, returns trimmed remainder
- Throws IOException when leading block lacks closing marker due to token-budget exhaustion
#### Public API
- AiCompletionParser() -> AiCompletionParser creates new parser
- parseCompletion(String) -> String strips leading thinking block
- THINKING_BLOCK_START_MARKER() -> String opens Gemma-4 thinking block
- THINKING_BLOCK_END_MARKER() -> String closes Gemma-4 thinking block
#### Dependencies
- java.io.IOException failure signaling
- lombok.ToString string generation
- java.lang.String trimming/searching/substring logic
#### Exceptions / Errors
- IOException thrown when THINKING_BLOCK_START_MARKER present without THINKING_BLOCK_END_MARKER
- Error message includes markers, maxOutputTokens hint, response length in chars
- Null response handled as empty, not error; missing markers kept as text
