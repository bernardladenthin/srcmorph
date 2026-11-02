### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:42Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Removes leading model-internal reasoning from AI completion text so only the answer is stored.

#### Purpose
- Cleans raw Gemma-compatible model completions before they are persisted.
- Preserves marker text when it appears outside a leading thinking block.

#### Type
- `public class AiCompletionParser` — class; non-final for subclassing and test mocking.
- Annotated with Lombok `@ToString`.

#### Input
- `parseCompletion(String response)` accepts raw completion text; `null` is allowed.
- No injected dependencies or external resources.

#### Output
- Returns cleaned answer text; never `null`.
- Does not mutate fields or write resources.

#### Core logic
- Returns `""` for `null`; otherwise trims the response.
- If trimmed text does not start with `THINKING_BLOCK_START_MARKER`, returns it unchanged apart from trimming.
- For a leading thinking block, finds the first end marker after the start marker and returns the trimmed text following it.
- Throws `IOException` if the leading block has no end marker, with guidance to increase `maxOutputTokens` and the response length.

#### Public API
- `AiCompletionParser()` — no-op constructor.
- `THINKING_BLOCK_START_MARKER` (`String`) — Gemma thinking-block opener.
- `THINKING_BLOCK_END_MARKER` (`String`) — Gemma thinking-block closer.
- `parseCompletion(String)` -> `String` — strips a leading thinking block.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `parseCompletion` throws `IOException` when a leading thinking block is unterminated.
- `null` input is treated as an empty response.

#### Concurrency
- No mutable instance state; no synchronization.
