### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:22:31Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma-4 thinking blocks from LLM completions to extract the final model answer for storage.

#### Purpose
- Parses raw LLM completion text to extract the model answer by stripping internal reasoning blocks before persisting to an AI index file.

#### Type
- class AiCompletionParser, public, non-final, @ToString (Lombok), no superclass or interfaces.

#### Input
- String response (raw completion text, may be null); static constants THINKING_BLOCK_START_MARKER and THINKING_BLOCK_END_MARKER.

#### Output
- String (cleaned answer text, never null); throws IOException on token budget exhaustion.

#### Core logic
- Returns empty string if response is null.
- Trims response; if it does not start with the thinking start marker, returns trimmed text unchanged.
- Otherwise, finds the first end marker after the start marker.
- If end marker is absent, throws IOException with actionable message about maxOutputTokens.
- If present, returns the substring after the end marker, trimmed.

#### Public API
- AiCompletionParser() -> void (constructor, no-op)
- parseCompletion(String response) -> String (strips leading thinking block, throws IOException if end marker missing)

#### Dependencies
- java.io.IOException; lombok.ToString.

#### Exceptions / Errors
- IOException thrown when thinking block start marker present but end marker absent (token budget exhausted); null input treated as empty response.

#### Concurrency
- None noted; stateless parsing with no shared mutable state.
