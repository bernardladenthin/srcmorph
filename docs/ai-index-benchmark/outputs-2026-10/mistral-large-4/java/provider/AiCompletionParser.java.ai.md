### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:37:49Z
- G: 
- A: 0.0.0
- X: file
---
> Strips Gemma-4 chain-of-thought thinking blocks from raw LLM completion text to extract the final answer before persisting it to an AI index file.

#### Purpose
- Parses raw model completion text to isolate the actual answer from internal reasoning
- Removes a leading thinking block delimited by `<|channel>thought` and `<channel|>` so only the final response is stored

#### Type
- Class, `public`, non-final (subclassable/mockable per javadoc)
- Package: `net.ladenthin.srcmorph.provider`
- Annotation: `@ToString` (Lombok)
- No superclass, no interfaces, no generics

#### Input
- Constructor: no arguments
- `parseCompletion(String response)`: raw LLM completion text, may be `null`
- Consumed constants: `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`

#### Output
- `parseCompletion` returns `String`: trimmed answer text following the thinking block, never `null` (`""` for `null` input)
- Throws `IOException` when a thinking block is started but never closed
- Exposes two `public static final String` constants: `THINKING_BLOCK_START_MARKER` (`"<|channel>thought"`) and `THINKING_BLOCK_END_MARKER` (`"<channel|>"`)

#### Core logic
- `null` response returns empty string `""`
- Response is trimmed; if it does not start with `THINKING_BLOCK_START_MARKER`, the trimmed text is returned unchanged (any markers inside are kept)
- If it starts with the start marker, the parser searches for the first `THINKING_BLOCK_END_MARKER` after it
- If the end marker is absent, throws `IOException` signaling token-budget exhaustion, including both markers and the original response length
- Returns the substring after the end marker, trimmed

#### Public API
- `AiCompletionParser()` -> constructor, no-op
- `parseCompletion(String response) -> String` -> strips leading Gemma-4 thinking block; throws `IOException` on unterminated block
- `THINKING_BLOCK_START_MARKER -> String` -> constant opening token of the thinking block
- `THINKING_BLOCK_END_MARKER -> String` -> constant closing token of the thinking block

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` thrown when response begins with `THINKING_BLOCK_START_MARKER` but lacks `THINKING_BLOCK_END_MARKER`; message advises increasing `maxOutputTokens` and reports response length in chars
- `null` input is handled gracefully, returning `""` instead of throwing

#### Concurrency
- Stateless: no instance fields, only immutable `static final` constants; inherently thread-safe
- `@ToString` only generates a string representation, no mutable state
