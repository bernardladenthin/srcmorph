### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:49:11Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract and clean model answers by removing internal reasoning blocks.

#### Purpose
- Strips Gemma-4 thinking blocks from model responses.
- Provides cleaned answer text for AI index storage.

#### Type
Class, non-final. Implements no interfaces. Uses @ToString annotation.

#### Input
- Constructor takes no parameters.
- Method parseCompletion(String) consumes raw completion text (may be null).

#### Output
- Returns cleaned string with internal reasoning removed.
- Throws IOException when token budget is exhausted inside a thinking block.

#### Core logic
- Checks for presence of THINKING_BLOCK_END_MARKER in input.
- If found, removes everything up to and including the end marker.
- If THINKING_BLOCK_START_MARKER exists without END_MARKER, throws IOException.
- Otherwise returns trimmed input string.

#### Public API
parseCompletion(response) -> String Cleans model response by stripping reasoning blocks.

#### Dependencies
java.io.IOException, lombok.ToString

#### Exceptions / Errors
Throws IOException when THINKING_BLOCK_START_MARKER is present but END_MARKER is missing due to token limit exhaustion.

#### Concurrency
No concurrency concerns; stateless class with immutable constants.
