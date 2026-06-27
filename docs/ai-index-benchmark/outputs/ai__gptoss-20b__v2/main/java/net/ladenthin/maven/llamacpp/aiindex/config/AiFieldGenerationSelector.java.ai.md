### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:55:45Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the AI field generation configuration for a file by matching its extension, returning the first applicable entry or a fallback.

#### Purpose
- Determines the `AiFieldGenerationConfig` that applies to a source file.
- Provides a language‑specific configuration or a generic fallback.

#### Type
class, final; Lombok `@ToString`; in package `net.ladenthin.maven.llamacpp.aiindex.config`.

#### Input
- `Iterable<AiFieldGenerationConfig> configs` (may contain `null` entries)  
- `String fileName` (e.g., `"Foo.java"`)

#### Output
- `@Nullable AiFieldGenerationConfig` – first matching entry, fallback, or `null`.

#### Core logic
- Initialize `fallback = null`.  
- Iterate over `configs`; skip if `config == null`.  
- Retrieve `config.getFileExtensions()`.  
- If the extension list is `null` or empty, assign as `fallback` if unset.  
- Else iterate extensions; if `fileName.endsWith(extension)` return `config`.  
- After loop, return `fallback`.

#### Public API
- `selectForFileName(Iterable<AiFieldGenerationConfig>, String) -> @Nullable AiFieldGenerationConfig` returns the matching or fallback config.

#### Dependencies
- `AiFieldGenerationConfig`  
- `java.util.List`  
- `lombok.ToString`

#### Exceptions / Errors
- None thrown; handles `null` config entries gracefully.

#### Concurrency
- Thread‑safe: only local variables are used, no shared mutable state.
