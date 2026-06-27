### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:45:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before storing in an AI index file.

#### Purpose
- Extracts and cleans model answers from LLM completion texts
- Handles Gemma-4 specific thinking block markers

#### Type
class public final @ToString

#### Input
- String response: raw completion text from the model, may be null

#### Output
- String: cleaned answer text, never null
- IOException: if token budget exhausted inside a thinking block

#### Core logic
1. Check for null input
2. Look for end marker to extract answer
3. Handle case where start marker present but end marker absent
4. Return trimmed response if no markers found

#### Public API
```
parseCompletion(String response) -> String throws IOException
```

#### Dependencies
- java.io.IOException
- lombok.ToString

#### Exceptions / Errors
- IOException: when token budget exhausted inside a thinking block
