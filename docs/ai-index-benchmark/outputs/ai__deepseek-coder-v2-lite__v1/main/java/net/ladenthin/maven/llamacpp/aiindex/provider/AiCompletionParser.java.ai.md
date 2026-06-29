### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:15:00Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- To clean and extract the actual model answer from raw LLM completion text by removing internal reasoning blocks.

#### Type
- Class (`public final class AiCompletionParser`)
- Modifiers: none
- Extends: `Object`
- Implements: none
- Key generics or type bounds: none
- Notable annotations: `@ToString` from Lombok

#### Input
- Constructor parameters: none
- Method parameters: `response` (the raw completion text, may be `null`)
- Injected dependencies: none
- Consumed fields: none
- Read resources: none

#### Output
- Return type: `String` (the cleaned answer text, never `null`)
- Produced state: none
- Mutated fields: none
- Written resources: none
- Side effects: Throws an `IOException` if a thinking block is started but the token budget is exhausted before the closing marker is emitted.

#### Core logic
- **If `response` is `null`, return an empty string.**
- **Find the position of `THINKING_BLOCK_END_MARKER` in `response`.**
  - If found, return the substring from this position to the end, trimmed.
- **Check if `THINKING_BLOCK_START_MARKER` is present without a corresponding `THINKING_BLOCK_END_MARKER`.**
  - If true, throw an `IOException` with a message indicating the token budget was exhausted.
- **If neither marker is present, return the trimmed `response`.**

#### Public API
- `parseCompletion(String response) -> String`: Strips any Gemma-4 thinking block from the response and returns the clean answer text, handling null input and potential exceptions.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a thinking block is started but the token budget is exhausted before the closing marker is emitted.
- Treats `null` input as an empty response.

#### Concurrency
- The class and its methods are not explicitly designed for concurrency, but they handle null inputs and exceptions safely in a single-threaded context.
