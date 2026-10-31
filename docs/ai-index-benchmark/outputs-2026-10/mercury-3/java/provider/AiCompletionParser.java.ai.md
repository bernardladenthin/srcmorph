### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:27Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma-4 style model-internal thinking blocks from raw LLM completion text so only the clean model answer is indexed.

#### Purpose
- Parses raw LLM completion responses before they are stored in an AI index file.
- Removes the model's chain-of-thought reasoning block (delimited by thinking markers) and returns only the final answer.

#### Type
- Class, non-final, annotated with @ToString (Lombok).
- No explicit extends/implements; implicitly extends Object.

#### Input
- `parseCompletion(String response)`: raw completion text from the LLM, may be null.
- Constant markers: `THINKING_BLOCK_START_MARKER = "<|channel>thought"` and `THINKING_BLOCK_END_MARKER = "<channel|>"`.

#### Output
- Returns cleaned answer text as String, never null (null input becomes empty string).
- Throws IOException when a leading thinking block is unclosed.

#### Core logic
- If response is null, returns empty string.
- Trims response; only acts if it starts with `THINKING_BLOCK_START_MARKER`.
- If start marker absent, returns the trimmed response unchanged (markers inside kept as text).
- If start marker present: cuts off the marker prefix, then searches for the first occurrence of `THINKING_BLOCK_END_MARKER`.
- If end marker is missing (index < 0), throws IOException with an explanatory message about token budget exhaustion.
- If end marker found, returns the text after it, trimmed; includes any markers quoted in the answer itself.

#### Public API
- `AiCompletionParser()` -> constructor; no-op init.
- `parseCompletion(String response) -> String`; strips leading thinking block, throws IOException on unclosed block.
- `THINKING_BLOCK_START_MARKER` -> static final String; opens Gemma-4 thinking block.
- `THINKING_BLOCK_END_MARKER` -> static final String; closes Gemma-4 thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown if a leading thinking block starts but the end marker is absent (simulated token budget exhaustion).
- Null input handled gracefully via early return of empty string.

#### Concurrency
- Effectively immutable: all fields are static final; no mutable state; thread-safe by design.
