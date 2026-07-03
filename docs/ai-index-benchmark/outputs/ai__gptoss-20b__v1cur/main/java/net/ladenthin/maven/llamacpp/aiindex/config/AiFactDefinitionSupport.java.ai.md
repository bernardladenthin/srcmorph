### AiFactDefinitionSupport.java
- H: 1.0
- C: D98D3BA4
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:49:36Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Resolves shared fact counter groups and applies them to routing rules, ensuring each rule references the correct counters.

#### Purpose
- Provides lookup of fact counter groups by key.
- Applies referenced groups to rules, replacing inline facts.

#### Type
- Final class `AiFactDefinitionSupport` (no generics, implements nothing).

#### Input
- Constructor receives `List<AiFactDefinition>` (may be null/empty).
- `resolveFactsKeys` consumes `Iterable<AiFieldGenerationConfig>` (may contain null entries).

#### Output
- `facts(String)` returns `List<AiFactCounter>` (never null).
- `resolveFactsKeys` mutates each rule’s `facts` field via `setFacts`.

#### Core logic
- Constructor:
  1. If `definitions` null → empty map.
  2. For each definition:
     - Enforce non‑null key.
     - Map key to its `facts` list (or empty list if null).
- `facts(key)`:
  - Retrieve list; throw `IllegalArgumentException` if key missing.
- `resolveFactsKeys(rules)`:
  - Iterate rules; skip nulls.
  - If rule has `factsKey`, replace its facts with the group from `facts(factsKey)`.

#### Public API
- `AiFactDefinitionSupport(List<AiFactDefinition>) -> void` (constructor)
- `facts(String) -> List<AiFactCounter>` (lookup counters)
- `resolveFactsKeys(Iterable<AiFieldGenerationConfig>) -> void` (apply group to rules)

#### Dependencies
- `java.util.*`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `NullPointerException` if any definition’s key is null.
- `IllegalArgumentException` if `facts(String)` key missing or rule’s `factsKey` unresolved.

#### Concurrency
- No explicit synchronization; instances are immutable after construction except for the mutable map used only during construction.
