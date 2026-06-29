### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:51:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Extracts the model's final answer from raw LLM completion text by removing internal reasoning blocks

#### Purpose
- Strips internal chain-of-thought reasoning from LLM responses  
- Ensures only the final model output is stored in AI index files  

#### Type
class, public, non-final, @ToString  
implements none, extends none  
generics: none  

#### Input
- raw completion text from an LLM (String), may be null  

#### Output
- cleaned final answer text (String), never null  

#### Core logic
- If input is null, return empty string  
- Find position of thinking block end marker  
- If end marker found, return text after it, trimmed  
- If start marker present but end marker absent, throw IOException with actionable message  
- Otherwise, return trimmed original input  

#### Public API
- parseCompletion(String) -> String: extracts final model answer by removing internal reasoning  

#### Dependencies
- java.io.IOException  
- lombok.ToString  

#### Exceptions / Errors
- Throws IOException if thinking block start is found without corresponding end marker  
- Handles null input by treating it as empty string  

#### Concurrency
- No thread safety concerns; stateless, immutable operations
