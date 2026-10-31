### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:48:45Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a container for child AiCondition objects used in logical combinators.

#### Purpose
- Holds child `AiCondition` objects for `<and>` / `<or>` combinators.

#### Type
- `public class AiConditionGroup` (class, public).  
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.  
- Extends: none.  
- Implements: none.  
- Field: `private @Nullable List<AiCondition> conditions`.

#### Input
- Constructor: `AiConditionGroup()` – no parameters.  
- `setConditions(Collection<AiCondition> conditions)` – accepts a nullable collection.

#### Output
- `getConditions()` – returns the internal `List<AiCondition>` (nullable).  
- `setConditions()` – assigns a defensive copy of the input collection to `conditions`.

#### Core logic
- Defensive copy: `new ArrayList<>(conditions)` when `conditions` is non‑null.  
- Null handling: sets `conditions` to `null` if input is `null`.

#### Public API
- `AiConditionGroup()` – constructs an empty group.  
- `getConditions()` – returns child conditions.  
- `setConditions(Collection<AiCondition>)` – sets child conditions.

#### Dependencies
- `java.util.ArrayList`  
- `java.util.Collection`  
- `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No exceptions thrown.  
- Accepts and handles `null` inputs gracefully.

#### Concurrency
- No synchronization; not thread‑safe.
