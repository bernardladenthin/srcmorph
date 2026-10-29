### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:58:45Z
- G: 
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.

#### Purpose
- Extracts the final answer from LLM completion text
- Strips internal reasoning/thinking blocks emitted by Gemma 4 models
- Provides clean answer text for storage in AI index files

#### Type
- Class: `AiCompletionParser`
- Public, non-final

#### Input
- `response` (String): raw completion text from the model, may be null

#### Output
- Cleaned answer text (String): trimmed, with internal thinking block removed

#### Core logic
- Trims input response
- If response starts with `THINKING_BLOCK_START_MARKER`:
  - Finds first occurrence of `THINKING_BLOCK_END_MARKER`
  - If end marker not found, throws IOException
  - Otherwise, returns text after end marker, trimmed
- If response does not start with start marker, returns trimmed response

#### Public API
- `AiCompletionParser()`: constructor
- `parseCompletion(String response) throws IOException`: main method to parse and clean response

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException`: thrown if thinking block started but end marker not found

#### Concurrency
- Not applicable (no concurrency considerations)
