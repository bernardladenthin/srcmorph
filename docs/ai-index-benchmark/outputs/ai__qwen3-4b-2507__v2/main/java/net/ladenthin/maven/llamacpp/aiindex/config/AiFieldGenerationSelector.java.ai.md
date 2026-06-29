### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:14:27Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects AI field generation config by file extension to enable language-specific prompts

#### Purpose
- Chooses AI configuration based on file extension match  
- Provides fallback for files without matching extensions  

#### Type
class @ToString final public in net.ladenthin.maven.llamacpp.aiindex.config

#### Input
- configs: Iterable<AiFieldGenerationConfig> (in declaration order, null entries skipped)  
- fileName: String (e.g. "Foo.java")  

#### Output
- AiFieldGenerationConfig (first match by extension, fallback if none, null if no config matches)  

#### Core logic
- Iterates over configs in order  
- Skips null entries  
- For each config, checks if its extensions list is empty: if so, records it as fallback  
- Otherwise, checks if file name ends with any extension in the list; returns first match  
- Returns fallback if no extension match found and fallback was set  
- Returns null if no config matches and no fallback exists  

#### Public API
selectForFileName(Iterable<AiFieldGenerationConfig>, String) -> AiFieldGenerationConfig: selects config by file extension or fallback  

#### Dependencies
AiFieldGenerationConfig

#### Exceptions / Errors
None thrown; handles null inputs gracefully via null checks  

#### Concurrency
Immutable state; thread-safe due to final class and no shared mutable state
