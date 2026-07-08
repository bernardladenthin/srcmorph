### AiConditionGroup.java
- H: 1.0
- C: B4457D08
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T22:47:24Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 0

> Holds a list of child conditions for an <and>/<or> combinator in the AI index configuration.

#### Purpose
- Represents the body of an <and> or <or> element in the AI index configuration.

#### Type
- Class AiConditionGroup  
  - Modifiers: public, final (implicit)  
  - Annotations: @ToString, @SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})  
  - Extends: none

#### Input
- `Collection<AiCondition> conditions` passed to `setConditions` (nullable).  
- No constructor parameters; default constructor does nothing.

#### Output
- `List<AiCondition> getConditions()` returns the stored list or `null`.  
- `setConditions` assigns a defensive copy of the provided collection or `null`.

#### Core logic
- Defensive copy of input collection into an `ArrayList` to prevent external mutation.  
- `null` handling: if input is `null`, internal list is set to `null`.  
- Simple getter returns the internal list reference (may be `null`).

#### Public API
- `AiConditionGroup()` → constructs an empty group.  
- `List<AiCondition> getConditions()` → returns child conditions or `null`.  
- `void setConditions(Collection<AiCondition> conditions)` → sets child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Defensive copy may throw `NullPointerException` if `conditions` is non‑null but contains `null` elements.

#### Concurrency
- Not thread‑safe; mutable state is not synchronized.
