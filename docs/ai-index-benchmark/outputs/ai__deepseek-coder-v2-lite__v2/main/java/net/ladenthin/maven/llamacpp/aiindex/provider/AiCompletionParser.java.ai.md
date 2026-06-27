### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:46:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- To strip internal reasoning from a model's completion text and return only the actual answer.

#### Type
```java
public class AiCompletionParser
```
extends `Object`

#### Input
- `response`: the raw completion text from the model, may be `null`.

#### Output
- Returns the cleaned answer text, never `null`.
- Throws an `IOException` if a thinking block was started but the token budget was exhausted before the closing marker was emitted.

#### Core logic
1. Check if `response` is `null`. If so, return an empty string.
2. Find the position of `THINKING_BLOCK_END_MARKER` in the response.
3. If found, return the substring from the end of this marker to the end of the string, trimmed.
4. If `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is absent, throw an `IOException`.
5. If neither marker is present, return the response trimmed.

#### Public API
```java
parseCompletion(response) -> String
```
- Strips any Gemma-4 thinking block from `response` and returns the clean answer text.

#### Dependencies
- `IOException`

#### Exceptions / Errors
- Throws `IOException` if `THINKING_BLOCK_START_MARKER` is present but `THINKING_BLOCK_END_MARKER` is absent, indicating a token budget exhaustion inside the thinking block.

#### Concurrency
- The class and its method do not inherently handle threading or concurrency issues. However, the method's behavior is stateless and thread-safe in its current implementation.
