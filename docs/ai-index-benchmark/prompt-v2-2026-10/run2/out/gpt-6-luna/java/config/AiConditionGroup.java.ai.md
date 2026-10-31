### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:44:25Z
- G: 
- A: 0.0.0
- X: file
---
> Groups configured child conditions so `<and>` and `<or>` rules can bind and evaluate their children.

#### Purpose
- Represents the child-condition list used as the body of an `AiCondition` `<and>` or `<or>` combinator.
- Names the collection `conditions` to support Maven’s collection-item XML binding.

#### Type
- `public class AiConditionGroup` — class; annotated with Lombok `@ToString`.
- Holds a nullable `List<AiCondition>`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts configured child conditions or `null`.

#### Output
- `getConditions()` returns the stored list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy of non-null input; preserves `null`.

#### Core logic
- Stores child conditions for an `AiCondition` combinator.
- Copies input collections on assignment, preventing later changes to the input collection from changing the stored list.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions or error handling.

#### Concurrency
- Mutable, unsynchronized condition list; no thread-safety guarantees.
