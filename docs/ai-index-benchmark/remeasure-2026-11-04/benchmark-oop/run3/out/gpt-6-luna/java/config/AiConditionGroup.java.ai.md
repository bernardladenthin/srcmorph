### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:50:32Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Groups child AI conditions for Maven-configured `and`/`or` combinators.

#### Purpose
- Stores the child `AiCondition` items bound from Maven `<conditions><condition>…</condition></conditions>` configuration.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`; no supertypes declared.

#### Input
- `setConditions(Collection<AiCondition> conditions)` accepts child conditions, including `null`.
- Private nullable `conditions: List<AiCondition>` stores the configured children.

#### Output
- `getConditions()` returns the stored list or `null`.
- `setConditions(...)` copies non-null input into a new `ArrayList`; does not retain the input collection.

#### Core logic
- Holds child conditions under the `conditions` field name for Maven collection-item binding.
- Converts assigned collections to a mutable `ArrayList`; preserves `null` when no children are configured.

#### Public API
- `AiConditionGroup()` — creates an empty group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>) -> void` — replaces children with a defensive copy.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable input and field; `null` input sets `conditions` to `null`.
