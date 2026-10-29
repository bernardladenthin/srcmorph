### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:55:42Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a container for a collection of child AI conditions used as the body for logical AND/OR combinators in Maven configuration.

#### Purpose
- Acts as a wrapper for a list of `AiCondition` instances to enable Maven plugin binding for `<and>` and `<or>` combinator elements.
- Provides a mutable container for configuration data where the field name `conditions` dictates the XML element structure.

#### Type
- `public class AiConditionGroup`
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No explicit `extends` or `implements` clauses.

#### Input
- Constructor: `AiConditionGroup()` (no arguments).
- Method `setConditions(Collection<AiCondition>)`: accepts a `Collection` of `AiCondition` objects.
- Field `conditions`: initialized to `null` by default.

#### Output
- Method `getConditions()`: returns `List<AiCondition>` or `null`.
- Internal state: `conditions` field holds a defensive `ArrayList` copy of the input collection.

#### Core logic
- Initializes the `conditions` list to `null` upon construction.
- `setConditions` performs a defensive copy: creates a new `ArrayList` from the provided `Collection` if non-null, otherwise sets field to `null`.
- `getConditions` exposes the internal list reference directly without copying.
- Field naming `conditions` is specifically chosen to allow Maven to derive the child element name `<condition>` from the singular form.

#### Public API
- `AiConditionGroup() -> void`
- `getConditions() -> List<AiCondition>` Returns child conditions or null.
- `setConditions(Collection<AiCondition>) -> void` Sets child conditions with defensive copy.

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
- `setConditions` handles null input by setting the field to null.

#### Concurrency
- No synchronization or thread-safety guarantees provided.
- `setConditions` creates a new `ArrayList` instance, preventing external modification of the internal list if the caller retains the original collection reference, but the returned list from `getConditions` is mutable.
