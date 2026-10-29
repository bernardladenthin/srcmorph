### AiCompletionParser.java
- H: 1.0
- C: 9766C36B
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:12:14Z
- G: 
- A: 0.0.0
- X: file
---
> Strips internal reasoning from LLM model responses to extract the final answer

#### Purpose
- Extracts the final model answer from raw completion text by removing internal thinking blocks  
- Ensures only the model's actual response is stored in AI index files

#### Type
class, public, non-final, @ToString  
implements: none  
extends: none  
static fields: THINKING_BLOCK_START_MARKER, THINKING_BLOCK_END_MARKER  

#### Input
- `response`: raw completion text from an LLM model, may be null (treated as empty)

#### Output
- Cleaned answer text (never null), with internal reasoning stripped

#### Core logic
- Trims input to remove leading whitespace  
- Checks if response starts with the thinking block marker  
- If not, returns trimmed input as-is  
- If yes, extracts content after the start marker  
- Finds the first occurrence of the end marker  
- Throws IOException if end marker is missing (token budget exhausted)  
- Returns the text after the end marker, trimmed  

#### Public API
parseCompletion(String response) → String  
Extracts final answer by stripping leading thinking block  

#### Dependencies
- java.io.IOException  
- lombok.ToString  

#### Exceptions / Errors
- Throws IOException if thinking block starts but end marker is missing  
- Handles null input by treating it as empty string  

#### Concurrency
- Not thread-safe; no synchronization or shared state  
- Immutable input handling; no side effects on input or state
