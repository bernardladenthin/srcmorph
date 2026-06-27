### AiCompletionParser.java
- H: 1.0
- C: 78BEBEB3
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:20:53Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Extracts the final AI model answer from raw completion text by stripping internal reasoning blocks

#### Purpose
- Strips internal model thinking blocks to isolate the final answer  
- Validates token budget exhaustion in chain-of-thought reasoning  

#### Type
class @ToString public non-final

#### Input
- response: raw model completion text (may be null)

#### Output
- cleaned, trimmed final answer text (never null)

#### Core logic
- If input is null, return empty string  
- Find position of closing thinking block marker  
- If found, return text after the marker (trimmed)  
- If start marker present but end marker missing, throw IOException with actionable message  
- Otherwise return trimmed input  

#### Public API
parseCompletion(String response) -> String: Extracts final answer from raw model output  

#### Dependencies
THINKING_BLOCK_START_MARKER, THINKING_BLOCK_END_MARKER

#### Exceptions / Errors
- IOException if thinking block start found but end not found (with diagnostic message)

#### Concurrency
none
