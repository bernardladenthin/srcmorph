### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:43:29Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI conditions for and/or rule composition.

#### Purpose
- Stores the child conditions that make up an `AiCondition` combinator body.
- Preserves nullable configuration semantics for Maven XML binding of `<conditions><condition>…</condition></conditions>`.

#### Type
- Class: `public class AiConditionGroup`
- Modifiers: none beyond `public`
- No `extends` / `implements`
- Field: `private @Nullable List<AiCondition> conditions`
- Annotation: `@ToString`
- Suppression: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- Constructor: `AiConditionGroup()`; no parameters
- `setConditions(final @Nullable Collection<AiCondition> conditions)`
  - Consumes nullable collection input
  - Copies collection contents into a new `ArrayList`
  - Stores `null` when input is `null`
- Field read: `conditions`, initialized as nullable list

#### Output
- `getConditions()` returns `@Nullable List<AiCondition>`
- `setConditions(...)` mutates instance field `conditions`
- Defensive copy ensures the stored list is independent of caller collection
- No external file, stream, or database writes

#### Core logic
- `AiConditionGroup` holds a nullable `List<AiCondition>` named `conditions`.
- Constructor leaves the field uninitialized and effectively empty.
- `getConditions()` returns the current child list or `null` when none were configured.
- `setConditions(...)` checks for `null`; if non-null, creates a new `ArrayList<>(conditions)` and assigns it.
- If the passed collection is `null`, it sets `conditions` to `null`.
- This pattern supports Maven XML binding while avoiding aliasing between caller state and stored state.

#### Public API
- `AiConditionGroup() -> void` — creates an empty group
- `getConditions() -> @Nullable List<AiCondition>` — returns configured child conditions
- `setConditions(final @Nullable Collection<AiCondition> conditions) -> void` — stores a defensive copy

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked or declared exceptions
- Null-handling: `getConditions()` may return `null`; `setConditions(null)` stores `null`
- No explicit validation beyond defensive copy
- No error conditions or catches

#### Concurrency
- No synchronization, locks, or thread-safety guards
- `conditions` is mutable state and replaced as a whole; no immutability guarantee
- Not explicitly thread-safe by design in this file
