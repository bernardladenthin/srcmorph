### AiFactDefinition.java
- H: 1.0
- C: F74A4B9F
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:43:10Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 6; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 1; field declarations (w/ modifier): 1

> Defines a named group of reusable AI fact counters for Maven plugin configuration.

#### Purpose
- Stores a lookup key for reference in routing rules.
- Holds an optional list of `AiFactCounter` objects.

#### Type
Class, mutable JavaBean, Lombok `@ToString`, annotated with `@SuppressWarnings`.

#### Input
- Constructor: none (default).
- `setKey(String key)` – sets the lookup key.
- `setFacts(List<AiFactCounter> facts)` – assigns the fact counter list.

#### Output
- `getKey()` – returns the lookup key.
- `getFacts()` – returns the list of `AiFactCounter` or `null`.

#### Core logic
- Simple field assignments and retrievals; no additional logic.

#### Public API
- `AiFactDefinition()` → constructs a new instance  
- `String getKey()` → get the lookup key  
- `void setKey(String key)` → set the lookup key  
- `List<AiFactCounter> getFacts()` → get configured fact counters  
- `void setFacts(List<AiFactCounter> facts)` → set fact counters  

#### Dependencies
- `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFactCounter`

#### Exceptions / Errors
- No checked or unchecked exceptions thrown.  
- Null handling: `facts` may be `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Intended for single‑threaded configuration usage.
