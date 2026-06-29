### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:43:39Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the AI field generation config based on file extension, using a priority order of matching rules and fallback

#### Purpose
- Determines which AI prompt configuration to apply to a source file by file extension  
- Supports fallback logic for files without matching extensions  

#### Type
- final class with public constructor  
- implements no interfaces  
- extends no classes  
- key generics: Iterable<AiFieldGenerationConfig>  
- annotations: @ToString, @Nullable (on return type)  

#### Input
- configs: Iterable of AiFieldGenerationConfig entries in declaration order; null entries are skipped  
- fileName: source file name (e.g. "Foo.java")  

#### Output
- AiFieldGenerationConfig instance matching the file extension, or null if no match and no fallback  

#### Core logic
- Iterates over configured AI generation rules in order  
- Skips null config entries  
- For each config, checks if its non-empty extensions list matches the file name via endsWith  
- Returns first matching config immediately  
- If no match, retains first config with empty or absent extension list as fallback  
- Returns null if no valid config and no fallback is defined  

#### Public API
- selectForFileName(Iterable<AiFieldGenerationConfig>, String) → AiFieldGenerationConfig (returns matching config or fallback, or null)  

#### Dependencies
- java.util.List  
- java.lang.Iterable  
- lombok.ToString  
- org.jspecify.annotations.Nullable  

#### Exceptions / Errors
- No exceptions thrown; handles null inputs gracefully by skipping null configs  
- Returns null when no match and no fallback exists  

#### Concurrency
- Thread-safe by design due to immutable state and pure logic (no shared mutable state)  
- No synchronization or concurrent access required
