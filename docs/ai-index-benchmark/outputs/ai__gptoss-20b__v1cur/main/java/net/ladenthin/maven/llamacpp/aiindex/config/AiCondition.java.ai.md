### AiCondition.java
- H: 1.0
- C: 15F73517
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T22:44:06Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 20; TODO/FIXME: 0; @Override: 0; methods (approx): 10; constructors: 1; field declarations (w/ modifier): 0

> A recursive boolean tree for matching files in a Maven routing rule.

#### Purpose
- Represents a composable file‑matching condition.
- Validated by `AiConditionEvaluator#validate`.

#### Type
- Class, `public`, annotated `@ToString`.
- Fields: `AiConditionGroup and, or, AiCondition not; List<String> extensions; AiRangeCondition size, lines; String modifiedAfter, modifiedBefore, pathGlob`.

#### Input
- Constructor: no‑arg.
- Setters receive `AiConditionGroup`, `Collection<String>`, `AiRangeCondition`, or `String` values.
- Fields store configuration for evaluation.

#### Output
- Getters return stored values or `null`.
- Setters clone collections to prevent external mutation.

#### Core logic
- No internal logic; serves as a mutable JavaBean for configuration.

#### Public API
- `getAnd() -> AiConditionGroup` – retrieve AND group.  
- `setAnd(AiConditionGroup) -> void` – set AND group.  
- `getOr() -> AiConditionGroup` – retrieve OR group.  
- `setOr(AiConditionGroup) -> void` – set OR group.  
- `getNot() -> AiCondition` – retrieve NOT child.  
- `setNot(AiCondition) -> void` – set NOT child.  
- `getExtensions() -> List<String>` – retrieve extensions list.  
- `setExtensions(Collection<String>) -> void` – set extensions.  
- `getSize() -> AiRangeCondition` – retrieve size range.  
- `setSize(AiRangeCondition) -> void` – set size range.  
- `getLines() -> AiRangeCondition` – retrieve lines range.  
- `setLines(AiRangeCondition) -> void` – set lines range.  
- `getModifiedAfter() -> String` – retrieve modified‑after timestamp.  
- `setModifiedAfter(String) -> void` – set modified‑after timestamp.  
- `getModifiedBefore() -> String` – retrieve modified‑before timestamp.  
- `setModifiedBefore(String) -> void` – set modified‑before timestamp.  
- `getPathGlob() -> String` – retrieve path glob.  
- `setPathGlob(String) -> void` – set path glob.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.maven.llamacpp.aiindex.config.AiConditionGroup`.
- `net.ladenthin.maven.llamacpp.aiindex.config.AiRangeCondition`.

#### Exceptions / Errors
- No exceptions thrown; setters accept `null`.

#### Concurrency
- Not thread‑safe; mutable state.
