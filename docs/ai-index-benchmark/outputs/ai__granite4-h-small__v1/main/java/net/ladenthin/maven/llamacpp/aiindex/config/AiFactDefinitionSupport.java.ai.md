### AiFactDefinitionSupport.java
- H: 1.0
- C: D98D3BA4
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:43:55Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides shared fact counter groups for routing rules and injects them into rule configurations.

#### Purpose
- Holds reusable fact counter groups.
- Resolves rule references to these groups.

#### Type
- `final class AiFactDefinitionSupport`  
  - Extends `Object`.  
  - No interfaces.  
  - Uses Lombok `@ToString`.

#### Input
- Constructor: `List<AiFactDefinition> definitions` (nullable).  
- `resolveFactsKeys`: `Iterable<AiFieldGenerationConfig> rules` (may contain nulls).

#### Output
- `facts(String key)` returns `List<AiFactCounter>` (never null).  
- `resolveFactsKeys` mutates each non‑null rule’s `facts` list.

#### Core logic
- **Constructor**  
  1. If `definitions` null → empty map.  
  2. Else iterate definitions.  
  3. Validate each `definition.getKey()` not null.  
  4. Map key → `definition.getFacts()` or empty list.  
- **facts(key)**  
  1. Retrieve from map.  
  2. Throw `IllegalArgumentException` if missing.  
- **resolveFactsKeys(rules)**  
  1. For each non‑null rule.  
  2. If `factsKey` present, replace rule’s facts with `facts(factsKey)`.

#### Public API
- `AiFactDefinitionSupport(List<AiFactDefinition> definitions)` → constructs support.  
- `List<AiFactCounter> facts(String key)` → get counters for key.  
- `void resolveFactsKeys(Iterable<AiFieldGenerationConfig> rules)` → inject counters into rules.

#### Dependencies
- `java.util.*` (Map, List, HashMap, Collections, Objects).  
- `lombok.ToString`.  
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFactDefinition`.  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFactCounter`.  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig`.

#### Exceptions / Errors
- Constructor: `NullPointerException` if any definition key null.  
- `facts`: `IllegalArgumentException` if key missing.  
- `resolveFactsKeys`: same as `facts` for missing key.

#### Concurrency
- Immutable after construction; thread‑safe read operations.  
- `resolveFactsKeys` mutates rule objects; caller responsible for synchronization.
