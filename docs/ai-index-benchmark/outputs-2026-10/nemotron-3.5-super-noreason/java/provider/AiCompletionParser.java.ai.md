### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:01:46Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Extracts the clean model answer from raw LLM completion text.
- Strips leading Gemma-4 thinking blocks while preserving the actual response.

#### Type
- Class `AiCompletionParser`, non-final, public.
- Annotated with `@ToString` from Lombok.
- No superclass, no interfaces.

#### Input
- Constructor takes no parameters.
- `parseCompletion(String response)` accepts raw completion text, may be `null`.
- Constants `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER` define the delimiters.

#### Output
- Returns cleaned answer text as `String`, never `null`.
- Returns empty string for `null` input.
- Throws `IOException` when a leading thinking block lacks the closing end marker.
- Does not mutate any fields; stateless parser.

#### Core logic
- `AiCompletionParser.parseCompletion` treats `null` input as an empty response.
- Trims the response and checks if it starts with `THINKING_BLOCK_START_MARKER`.
- If no leading start marker, returns the trimmed response unchanged.
- If the start marker is present, searches for the first `THINKING_BLOCK_END_MARKer` after it.
- If the end marker is absent, throws `IOException` with an actionable message about token budget exhaustion.
- If the end marker is found, returns the text after it, trimmed.

#### Public API
- `AiCompletionParser()` -> creates a new parser instance.
- `parseCompletion(String response) -> String` strips a leading thinking block and returns the clean answer.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when `THINKING_BLOCK_START_MARKER` is present at the start but `THINKING_BLOCK_END_MARKER` is missing.
- `null` input is handled gracefully and returns an empty string.

#### Concurrency
- Stateless class with no mutable fields; safe for concurrent use.
