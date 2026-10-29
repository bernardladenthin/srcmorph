### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:11:46Z
- G: 
- A: 0.0.0
- X: file
---
> Determines how to handle source files larger than a model's context window

#### Purpose
- Defines behavior when a source file exceeds the model's context size  
- Configurable per routing rule via `<onOversize>` in build configuration  

#### Type
public enum AiOversizeStrategy extends Enum<AiOversizeStrategy>  
- static final DEFAULT = FAIL  
- private final String configValue  

#### Input
- String value (config input, null or blank)  
- Trimmed, case-insensitive comparison against known strategy tokens  

#### Output
- Strategy instance (FAIL, SAMPLE, MAP_REDUCE, or DETERMINISTIC)  
- Config token (e.g. "sample") for downstream parsing  

#### Core logic
- FAIL: abort build, require larger-context model  
- SAMPLE: trim file to window size and process head  
- MAP_REDUCE: split into line-chunks, summarize each, combine results  
- DETERMINISTIC: emit metadata and head/tail samples without AI processing  
- fromConfig: parses input case-insensitively, returns DEFAULT if invalid  

#### Public API
- AiOversizeStrategy(String) → AiOversizeStrategy (constructor)  
- configValue() → String (returns config token)  
- fromConfig(String) → AiOversizeStrategy (parsing with null/blank fallback)  

#### Dependencies
- org.jspecify.annotations.Nullable  
- java.lang.String  
- java.lang.Enum  

#### Exceptions / Errors
- IllegalArgumentException if non-blank input does not match any known strategy  

#### Concurrency
- Thread-safe due to enum immutability and static final fields  
- No shared state or synchronization needed
