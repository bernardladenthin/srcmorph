### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:24Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a container for a collection of child AI conditions used as the body for logical AND/OR combinators in configuration.

#### Purpose
- Acts as a wrapper for a list of `AiCondition` instances to support Maven plugin XML binding for `<and>` and `<or>` combinators.
- Ensures defensive copying of the condition list during configuration setting.

#### Type
- `public class AiConditionGroup`
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No extends or implements clauses.

#### Input
- Constructor: `AiConditionGroup()` (no arguments).
- Method `setConditions`: accepts `@Nullable Collection<AiCondition>`.

#### Output
- Method `getConditions`: returns `@Nullable List<AiCondition>`.
- Internal state: `conditions` field (initialized to `null` or a new `ArrayList`).

#### Core logic
- Initializes the `conditions` field to `null` by default.
- `getConditions` returns the current list of child conditions or `null`.
- `setConditions` performs defensive copying by creating a new `ArrayList` from the input collection before assignment.

#### Public API
- `AiConditionGroup() -> void`
- `getConditions() -> List<AiCondition>`
- `setConditions(Collection<AiCondition>) -> void`

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions declared.
- Null handling: `conditions` field and return value are explicitly `@Nullable`.

#### Concurrency
- No concurrency mechanisms or thread-safety guarantees specified.
