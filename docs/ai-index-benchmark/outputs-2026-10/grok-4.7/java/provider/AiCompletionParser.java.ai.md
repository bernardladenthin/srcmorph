### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:40:50Z
- G: 
- A: 0.0.0
- X: file
---
I will index the provided Java source file according to the specified format instructions.

The file contains a public class `AiCompletionParser` in package `net.ladenthin.srcmorph.provider`. It uses the `@ToString` annotation. The class defines two public static final string constants for thinking block markers. It has a default constructor and a public method named `parseCompletion` that accepts a `String` and throws `IOException`.

The public API is fully listed. Dependencies are `java.io.IOException` and `lombok.ToString`. The class is stateless and immutable. It is non-final for subclassing or mocking. The `parseCompletion` method throws `IOException` when a thinking block starts but its end marker is missing.> Strips a leading Gemma-4 thinking block from raw LLM completion text so only the model answer is stored in an AI index file.

#### Purpose
- Parses raw LLM completion text and removes a leading model-internal thinking block before the answer is persisted.
- Non-final so tests can subclass and mock `AiCompletionParser`.

#### Type
- Class, public, non-final, non-abstract. No extends, no implements.
- Annotated `@ToString` (Lombok).

#### Input
- `parseCompletion(String response)`: raw completion text; `null` treated as empty.
- Consumes constants `THINKING_BLOCK_START_MARKER` (`<|channel>thought`) and `THINKING_BLOCK_END_MARKER` (`<channel|>`).
- No injected dependencies, files, or config.

#### Output
- Returns cleaned answer `String`, never `null` (empty string for `null` input).
- No mutated fields or written resources.
- Throws `IOException` when a leading thinking block has no closing marker.

#### Core logic
- Trim `response`; `null` becomes `""`.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, return it unchanged (later markers stay as answer text).
- If it starts with the start marker, search only after that marker for the first `THINKING_BLOCK_END_MARKER`.
- If the end marker is missing, throw `IOException` (token budget exhausted inside the thinking block; message includes response length and asks to increase `maxOutputTokens`).
- Otherwise drop the block through the end marker and return the remaining text trimmed.

#### Public API
- `AiCompletionParser() -> AiCompletionParser` — no-op constructor.
- `THINKING_BLOCK_START_MARKER` — Gemma-4 thought open token.
- `THINKING_BLOCK_END_MARKER` — Gemma-4 thought close token.
- `parseCompletion(String response) -> String` — strip leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException` if response starts with `THINKING_BLOCK_START_MARKER` but `THINKING_BLOCK_END_MARKER` is absent.
- `null` response is not an error; returned as `""`.
- End marker index `< 0` is the exhausted-budget condition.

#### Concurrency
- Stateless after construction; only immutable `public static final` marker strings. No synchronization.
