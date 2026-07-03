### AiCondition.java
- H: 1.0
- C: 15F73517
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T21:38:21Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 20; TODO/FIXME: 0; @Override: 0; methods (approx): 10; constructors: 1; field declarations (w/ modifier): 0

> A configurable boolean tree for matching files in a Maven plugin.

#### Purpose
- Holds a file‑matching rule for routing.
- Supports combinators (`and`, `or`, `not`) and leaf conditions (extension, size, lines, modified dates, path glob).

#### Type
- Class, public, annotated with `@ToString`.  
- Fields are nullable; no modifiers.

#### Input
- Constructor: no parameters.  
- Setter methods accept `AiConditionGroup`, `Collection<String>`, `AiRangeCondition`, `String`.  
- Dependencies: `AiConditionGroup`, `AiRangeCondition`.

#### Output
- Getter methods expose current field values.  
- Internal state mutated by setters.

#### Core logic
- No business logic; serves as a data holder for the evaluator.

#### Public API
- `AiCondition()` → constructor.  
- `getAnd() -> AiConditionGroup` 1.  
- `setAnd(AiConditionGroup) -> void` 2.  
- `getOr() -> AiConditionGroup` 3.  
- `setOr(AiConditionGroup) -> void` 4.  
- `getNot() -> AiCondition` 5.  
- `setNot(AiCondition) -> void` 6.  
- `getExtensions() -> List<String>` 7.  
- `setExtensions(Collection<String>) -> void` 8.  
- `getSize() -> AiRangeCondition` 9.  
- `setSize(AiRangeCondition) -> void` 10.  
- `getLines() -> AiRangeCondition` 11.  
- `setLines(AiRangeCondition) -> void` 12.  
- `getModifiedAfter() -> String` 13.  
- `setModifiedAfter(String) -> void` 14.  
- `getModifiedBefore() -> String` 15.  
- `setModifiedBefore(String) -> void` 16.  
- `getPathGlob() -> String` 17.  
- `setPathGlob(String) -> void` 18.

#### Dependencies
- `AiConditionGroup`  
- `AiRangeCondition`  
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions thrown.

#### Concurrency
- No synchronization; instance is mutable and not thread‑safe.
