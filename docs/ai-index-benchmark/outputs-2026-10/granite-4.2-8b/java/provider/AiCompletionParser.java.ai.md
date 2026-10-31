### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:28:49Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Parses LLM completion text to extract the model's answer by removing a leading Gemma-4 style thinking block delimited by start and end markers.

#### Purpose
- Removes a leading thinking block from raw LLM response text when present.
- Returns only the answer text that follows the closing marker.
- Throws `IOException` if a thinking block starts but does not close within the response.
- Handles `null` input as empty.

#### Type
- Class `AiCompletionParser` in package `net.ladenthin.srcmorph.provider`; not `final`; annotated with `@ToString`.
- No inheritance or implementation; no generics.

#### Input
- Method `parseCompletion(String response)` receives a raw completion string, may be `null`.
- Uses only the class's static marker fields (`THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`).

#### Output
- Returns a `String` containing the cleaned answer text (trimmed), never `null`.
- Throws `IOException` when a thinking block starts but the end marker is missing.

#### Core logic
- If `response` is `null`, return `""`.
- Trim the response.
- If trimmed does not start with `THINKING_BLOCK_START_MARKER`, return trimmed response.
- Otherwise extract the substring after the start marker.
- Find the first occurrence of `THINKING_BLOCK_END_MARKER` in that substring.
- If not found, throw `IOException` with details about token exhaustion.
- Return the substring after the end marker, trimmed.

#### Public API
- `parseCompletion(String response) -> String` – strips leading thinking block or returns trimmed response; throws `IOException` if block unclosed.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` when `THINKING_BLOCK_START_MARKER` is present at start but `THINKING_BLOCK_END_MARKER` is absent in the remainder.
- Message includes marker strings and response length for debugging.
- `null` input is handled safely (treated as empty).

#### Concurrency
- No threading, synchronization, or mutable shared state; stateless method. Thread-safe by default.

#### No other sections apply.
