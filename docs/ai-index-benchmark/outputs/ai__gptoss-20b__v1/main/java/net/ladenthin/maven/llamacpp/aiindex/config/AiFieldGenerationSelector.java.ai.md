### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:05:55Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the field‑generation configuration applicable to a source file by file‑extension matching.  

#### Purpose
- Resolve AiFieldGenerationConfig for a given file name.

#### Type
- `final class AiFieldGenerationSelector`  
- Annotated with `@ToString`; no superclass.

#### Input
- `Iterable<AiFieldGenerationConfig> configs` – declaration order; null entries ignored.
- `String fileName` – source file name to match.

#### Output
- `@Nullable AiFieldGenerationConfig` – first extension‑matching config, else first fallback, else `null`.

#### Core logic
- Iterate over `configs`; skip `null`.
- Retrieve `config.getFileExtensions()`; if null/empty store as `fallback`.
- For each `extension` in list, if `fileName.endsWith(extension)` return `config`.
- After loop, return `fallback`.

#### Public API
- `AiFieldGenerationSelector()` – constructor, no initialization.
- `selectForFileName(configs, fileName) -> AiFieldGenerationConfig?` – determine config for file.

#### Dependencies
- `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig`

#### Exceptions / Errors
- None; no checked exceptions thrown.

#### Concurrency
- No synchronization; instance is immutable.
