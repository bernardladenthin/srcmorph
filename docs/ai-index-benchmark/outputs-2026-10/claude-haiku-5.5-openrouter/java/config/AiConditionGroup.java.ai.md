### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:08Z
- G: 
- A: 0.0.0
- X: file
---
> Holds the child AiCondition list used as the body of and/or combinators, with Maven-friendly naming for XML binding.

#### Purpose
- Container for child `AiCondition` items inside `<and>`/`<or>` combinators in `AiCondition`.
- Exists so Maven plugin parameter binding produces `<conditions><condition>…</condition></conditions>`.

#### Type
- Class, public, non-final, no superclass or interfaces.
- Annotations: `@ToString` (Lombok); `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Field annotated with jspecify `@Nullable`.

#### Input
- No-arg constructor.
- `setConditions(Collection<AiCondition>)` input, nullable.

#### Output
- `getConditions()` returns the stored list or null.
- Mutates field `conditions` (private `List<AiCondition>`, nullable).

#### Core logic
- Stores a defensive copy of the supplied collection as a new `ArrayList`.
- Stores null when the supplied collection is null.
- Returns the internal list reference directly from the getter (no copy on read).
- Constructor body is empty (no-op).
- Naming rule: field `conditions` drives Maven item element name `condition`; the group wrapper exists because a field named `and`/`or` would force `<and>`/`<or>` item tags.

#### Public API
- `AiConditionGroup()` -> constructor; creates empty group.
- `getConditions()` -> `List<AiCondition>` (nullable); returns child conditions or null.
- `setConditions(Collection<AiCondition>)` -> void; copies children defensively, null allowed.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCondition` (same package `net.ladenthin.srcmorph.config`)

#### Exceptions / Errors
- No exceptions thrown or caught.
- Null-safe: null input to setter stores null; getter may return null.
- Null elements inside the collection are copied as-is, not filtered.

#### Concurrency
- No synchronization; plain mutable state.
- Not thread-safe.
