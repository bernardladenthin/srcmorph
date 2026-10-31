### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:47:12Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts the model answer from raw LLM completion text by stripping Gemma-4 style thinking blocks before persisting to an AI index.

#### Purpose
- Parses raw LLM completion text to isolate the model answer.
- Strips internal thinking blocks before storage.

#### Type
- public class, non-final, no inheritance.
- public static final constants: THINKING_BLOCK_START_MARKER, THINKING_BLOCK_END_MARKER.
- Annotated with @ToString (Lombok).

#### Input
- Constructor: no parameters.
- Method parseCompletion(String response) - raw completion text, may be null.

#### Output
- Returns cleaned answer String, never null.
- Throws IOException when thinking block is unclosed.

#### Core logic
- Null input treated as empty string.
- Trim response; if no start marker, return trimmed text.
- If start marker present, locate end marker.
- If end marker missing, throw IOException with actionable message.
- Otherwise return text after end marker, trimmed.

#### Public API
- public AiCompletionParser() - creates a new parser instance.
- public String parseCompletion(String response) - strips thinking block and returns answer.

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException thrown when thinking block started but no closing marker.
- Null input handled gracefully, no exception.

#### Concurrency
- Stateless, no shared mutable state; no synchronization required.
