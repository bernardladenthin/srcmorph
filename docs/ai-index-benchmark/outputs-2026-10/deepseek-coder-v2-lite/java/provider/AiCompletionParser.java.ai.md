### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:36:06Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- To extract the model's actual answer from raw completion text by stripping internal reasoning blocks.

#### Type
- Class (`public final class AiCompletionParser`)

#### Input
- `String response`: the raw completion text from the model, may be `null`.

#### Output
- `String`: the cleaned answer text, never `null`.

#### Core logic
- **Trim the input response** to remove leading and trailing whitespace.
- **Check if the response starts with the thinking block start marker**.
  - If not, return the trimmed response.
- **Locate the first occurrence of the thinking block end marker** after the start marker.
  - If the end marker is not found, throw an `IOException` indicating a token budget exhaustion.
- **Extract the text following the end marker** and return it, including any markers it contains.

#### Public API
- `parseCompletion(String response) -> String`: Strips a leading Gemma-4 thinking block from the response and returns the clean answer text.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if a thinking block is started but the token budget is exhausted before the closing marker is emitted.

#### Concurrency
- The class is not thread-safe as it does not modify any shared state.

#### Concurrency
- The class is not thread-safe as it does not modify any shared state.
