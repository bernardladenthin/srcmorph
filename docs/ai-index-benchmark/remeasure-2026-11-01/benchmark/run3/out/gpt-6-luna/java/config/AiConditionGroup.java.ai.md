### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:58:43Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI conditions for use as the body of an AND/OR condition.

#### Purpose
- Provides a Maven-bindable `conditions` collection for child `AiCondition` entries.
- Supports absent configuration by allowing the collection to be `null`.

#### Type
- `public class AiConditionGroup` — class; annotated with Lombok `@ToString`.
- Field: nullable `List<AiCondition> conditions`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the configured list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child conditions under the `conditions` field name so Maven binds items as `<conditions><condition>…</condition></conditions>`.
- Copies non-null input collections into a new list; the getter returns the stored list directly.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition` — child condition type.
- `ArrayList`, `Collection`, `List` — collection storage and copying.
- Lombok `ToString`; JSpecify `Nullable`.

#### Exceptions / Errors
- Accepts `null` in `setConditions`; `getConditions()` returns `null` when unset.
- No explicit exception handling.

#### Concurrency
- No synchronization or thread-safety guarantees.
