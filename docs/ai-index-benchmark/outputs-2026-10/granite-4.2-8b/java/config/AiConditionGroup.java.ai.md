### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:08:46Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Represents a container for a list of {@link AiCondition}s used in {@code <and>} or {@code <or>} combinators within configuration.

#### Purpose
- Holds child {@link AiCondition} elements for logical grouping.
- Provides getter and setter for the conditions list with defensive copying.

#### Type
- Class `AiConditionGroup` (public), not `abstract`, `final`, or `sealed`.
- Uses Lombok `@ToString`.
- Imports: `java.util.ArrayList`, `java.util.Collection`, `java.util.List`; annotations: `@Nullable`, `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- Method `setConditions`: parameter `final @Nullable Collection<AiCondition> conditions`.

#### Output
- Method `getConditions`: returns `@Nullable List<AiCondition>`.
- Field `conditions`: `@Nullable List<AiCondition>`.

#### Core logic
- Initializes `conditions` as `null` in constructor.
- `getConditions` returns the current `conditions` list (may be `null`).
- `setConditions` copies the input collection into a new `ArrayList` if not `null`, otherwise sets `conditions` to `null`; uses defensive copying.

#### Public API
- `AiConditionGroup()` -> `void` (constructor, no-op initialization).
- `getConditions()` -> `@Nullable List<AiCondition>` (returns child conditions, may be `null`).
- `setConditions(final @Nullable Collection<AiCondition> conditions)` -> `void` (sets conditions with defensive copy).

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `net.ladenthin.srcmorph.config.AiCondition`
- `org.jspecify.annotations.Nullable`
- Lombok `@ToString`

#### Exceptions / Errors
- No exceptions thrown.
- Handles `null` input in `setConditions` safely; returns `null` from `getConditions` when no conditions configured.

#### Concurrency
- No concurrency features; field is mutable but not accessed concurrently without external synchronization.

> Note: The class contains exactly 3 public/protected members: constructor `AiConditionGroup()`, method `getConditions()`, method `setConditions(...)`. No other public API members exist.
