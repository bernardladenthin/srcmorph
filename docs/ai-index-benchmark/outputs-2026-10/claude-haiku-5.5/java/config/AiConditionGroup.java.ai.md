### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:05Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AiConditions as the body of AND/OR combinators in a Maven plugin configuration.

#### Purpose
- Holds a nullable list of child `AiCondition` items for `<and>` / `<or>` combinators.
- Wraps items in a `conditions` field so Maven binds them as `<conditions><condition>` elements.

#### Type
- Class, public, non-final, non-abstract.
- No `extends` or `implements`.
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Uses `org.jspecify.annotations.Nullable` (type-use) on the field, getter return, and setter parameter.

#### Input
- No-arg constructor.
- `setConditions(Collection<AiCondition>)`, nullable, copied into an `ArrayList`.
- Field `conditions` of type `List<AiCondition>`, nullable.

#### Output
- `getConditions()` returns the stored `List<AiCondition>` or `null`.
- Mutates field `conditions`.
- No I/O or other side effects.

#### Core logic
- Stores children in a single nullable `List<AiCondition>` field.
- `setConditions`: if the argument is non-null, stores `new ArrayList<>(conditions)` (defensive copy). If null, stores `null`.
- Getter returns the internal list reference directly (no copy).
- Field name `conditions` determines Maven element naming; the group exists so combinator children use the `condition` item name.

#### Public API
- `AiConditionGroup() -> AiConditionGroup`: creates empty group, no-op.
- `getConditions() -> List<AiCondition>`: returns child conditions or null.
- `setConditions(Collection<AiCondition>) -> void`: replaces children with defensive copy.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCondition` (package `net.ladenthin.srcmorph.config`)

#### Exceptions / Errors
- Null-tolerant: `null` accepted by setter, returned by getter when unset.
- No checked or runtime exceptions thrown.
- Null elements inside the collection are copied as-is (not validated).

#### Concurrency
- Not thread-safe. Mutable state, no synchronization.
- Getter exposes the internal list, so callers can mutate it.
