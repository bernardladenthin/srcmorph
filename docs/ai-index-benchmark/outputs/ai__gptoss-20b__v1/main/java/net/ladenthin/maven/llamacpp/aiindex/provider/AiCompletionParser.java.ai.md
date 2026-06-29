### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:17:43Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Extracts the model's final answer from LLM output by removing Gemma‑4 thinking blocks.  

#### Purpose  
- Parse raw LLM completion text.  
- Remove internal reasoning block.  

#### Type  
- **class** AiCompletionParser, non‑final, annotated `@ToString`.  
- Extends `Object`.  

#### Input  
- `AiCompletionParser()` constructor – no arguments.  
- `parseCompletion(String response)` – raw completion string, may be `null`.  

#### Output  
- Clean answer string, never `null`.  
- Throws `IOException` if thinking block starts but ends missing.  

#### Core logic  
- If `response` is `null`, return empty string.  
- Find last index of `THINKING_BLOCK_END_MARKER`.  
- If present, return substring after it, trimmed.  
- If `THINKING_BLOCK_START_MARKER` exists without end marker, throw `IOException`.  
- Otherwise, return trimmed original response.  

#### Public API  
- `AiCompletionParser()` → `void` – constructor.  
- `String parseCompletion(String response)` → `String` – parse response, remove thinking block.  
- `String THINKING_BLOCK_START_MARKER` → `String` – start marker token.  
- `String THINKING_BLOCK_END_MARKER` → `String` – end marker token.  

#### Dependencies  
- `java.io.IOException`  
- `lombok.ToString`  

#### Exceptions / Errors  
- Throws `IOException` when start marker present but end marker missing.  
- Error message advises increasing `maxOutputTokens`.  

#### Concurrency  
- No explicit synchronization; instance fields are immutable constants.  
- Thread‑safe for shared usage.
