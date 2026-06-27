### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:54:38Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping Gemma-4 internal thinking blocks before storing in an AI index file.

#### Purpose
*   Strips internal reasoning from Gemma-4 chain-of-thought responses.
*   Returns clean model answers for AI index persistence.

#### Type
Class; extends Object; uses @ToString annotation.

#### Input
String response (raw completion text, may be null).

#### Output
String cleaned answer text; throws IOException on incomplete thinking blocks.

#### Core logic
*   Checks if input response is null and returns empty string.
*   Searches for THINKING_BLOCK_END_MARKER to locate end of internal reasoning.
*   If end marker found, returns substring after it trimmed.
*   If start marker exists but end marker is missing, throws IOException.
*   If no markers found, returns trimmed original response.

#### Public API
parseCompletion(String) -> String <=6-word purpose clause: Strips thinking block and returns clean answer.

#### Dependencies
java.io.IOException, net.ladenthin.maven.llamacpp.aiindex.provider.AiCompletionParser.

#### Exceptions / Errors
IOException thrown if THINKING_BLOCK_START_MARKER appears without THINKING_BLOCK_END_MARKER due to token budget exhaustion.

#### Concurrency
Non-final class designed for subclassing and mocking; logic is stateless and thread-safe.
