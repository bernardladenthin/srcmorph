### AiModelDefinitionSupport.java
- H: 1.0
- C: 05A1DA33
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:02:16Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 3; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Builds a lookup table that maps AI model definition keys to ready‑to‑use generation configurations for Maven LlamaCPP plugin.

#### Purpose
- Provides configuration lookup for AI model definitions.  
- Validates definitions at construction time.

#### Type
- `final class AiModelDefinitionSupport`  
- Implements no interfaces or inheritance.  
- Annotated with `@ToString`.  

#### Input
- Constructor receives `List<AiModelDefinition> definitions` (may be `null`).  
- Reads each `AiModelDefinition`’s `key` and other fields.  
- Uses `Java8CompatibilityHelper` to pre‑size internal `HashMap`.

#### Output
- Stores `Map<String, AiGenerationConfig> configs`.  
- `getConfig(key)` returns the corresponding `AiGenerationConfig`.  

#### Core logic
- If `definitions` is `null` → create empty `configs`.  
- Else:  
  - Pre‑size `configs` based on list size.  
  - Iterate list, enforce non‑null `key` with `Objects.requireNonNull`.  
  - Convert each definition to an `AiGenerationConfig` via `toConfig` and put into `configs`.  
- `getConfig(key)` retrieves config or throws `IllegalArgumentException` with message prefix.  
- `toConfig` copies all fields from `AiModelDefinition` to a new `AiGenerationConfig`.  

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition>) -> AiModelDefinitionSupport`  
  *Constructs definition lookup table.*  
- `AiGenerationConfig getConfig(String) -> AiGenerationConfig`  
  *Retrieves config by key.*  

#### Dependencies
- `java.util.HashMap`, `java.util.List`, `java.util.Map`, `java.util.Objects`.  
- `lombok.ToString`.  
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`.  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition`.  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`.  

#### Exceptions / Errors
- `NullPointerException` if any definition has a `null` key.  
- `IllegalArgumentException` if `getConfig` receives an unknown key.  

#### Concurrency
- Instance is immutable after construction; thread‑safe for concurrent reads.
