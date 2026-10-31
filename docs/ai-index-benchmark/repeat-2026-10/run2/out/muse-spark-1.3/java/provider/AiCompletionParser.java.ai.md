### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:15:21Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts clean LLM answer by stripping leading chain-of-thought thinking block before AI index persistence.
#### Purpose
- Parse raw LLM completion text into storable model answer.
- Prevent persisting Gemma-4 internal reasoning in AI index file.
#### Type
- public non-final class AiCompletionParser, @ToString, no extends/implements.
#### Input
- response String raw completion text, may be null.
- THINKING_BLOCK_START_MARKER `<|channel>thought` leading-block detector.
- THINKING_BLOCK_END_MARKER `<channel|>` block terminator.
#### Output
- String cleaned answer text trimmed, never null; empty on null input.
- IOException on truncated thinking block, no state mutation.
#### Core logic
- Null response returns empty AiCompletionParser answer.
- Trim response and check startsWith THINKING_BLOCK_START_MARKER.
- Non-leading markers kept verbatim as answer text.
- Leading block strips substring up to first THINKING_BLOCK_END_MARKER inclusive, returns remainder trimmed.
- Leading start without end signals token-budget exhaustion during reasoning.
#### Public API
- AiCompletionParser() -> AiCompletionParser — creates new parser.
- parseCompletion(String response) -> String — strips leading thinking block.
#### Dependencies
- java.io.IOException, lombok.ToString, java.lang.String.
#### Exceptions / Errors
- IOException when THINKING_BLOCK_START_MARKER present without THINKING_BLOCK_END_MARKER, message advises Increase maxOutputTokens with response length.
- Null input handled as empty response, not error.
