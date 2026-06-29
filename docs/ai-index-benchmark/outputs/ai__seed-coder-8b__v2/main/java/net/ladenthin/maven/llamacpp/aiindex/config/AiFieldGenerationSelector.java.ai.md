### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:35:32Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the appropriate AI field generation configuration based on source file extensions.

#### Purpose
- Selects `AiFieldGenerationConfig` for a given source file.
- Determines applicable config using file extensions.

#### Type
class final, Lombok @ToString annotation

#### Input
- Iterable of `AiFieldGenerationConfig` objects
- Source file name as String

#### Output
- Matching `AiFieldGenerationConfig` or null

#### Core logic
1. Iterate through configs
2. Check for matching extensions
3. Return first match or fallback
4. Return null if no match and no fallback

#### Public API
```java
selectForFileName(Iterable<AiFieldGenerationConfig>, String) -> @Nullable AiFieldGenerationConfig
```

#### Dependencies
- `AiFieldGenerationConfig`
- `List`
- `Nullable` annotation

#### Exceptions / Errors
- Throws exceptions from `AiFieldGenerationConfig.getFileExtensions()`
- Handles null configs and extensions gracefully
