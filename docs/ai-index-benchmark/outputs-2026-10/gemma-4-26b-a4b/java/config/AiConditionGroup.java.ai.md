### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:28:30Z
- G: 
- A: 0.0.0
- X: file
---
> Groups multiple AI conditions to be used within logical AND/OR combinators.

#### Purpose
* Acts as a container for a collection of `AiCondition` objects.
* Facilitates Maven XML configuration binding by providing a specific field name for collection elements.

#### Type
class; Lombok `@ToString`; suppresses `NullAway.Init` and `initialization.fields.uninitialized`.

#### Input
* `conditions` (Collection<AiCondition>) via `setConditions`.

#### Output
* `conditions` (List<AiCondition>) via `getConditions`.
* Defensive copy of the input collection stored in the `conditions` field.

#### Core logic
* Provides a wrapper for `List<AiCondition>` to satisfy Maven's singular-to-plural XML element naming convention.
* Ensures `conditions` is stored as a new `ArrayList` when set to prevent external mutation.
* Handles nullability for the internal condition list.

#### Public API
* `AiConditionGroup()` -> void: Default constructor.
* `getConditions()` -> List<AiCondition>: Returns child conditions or null.
* `setConditions(Collection<AiCondition>)` -> void: Sets and defensively copies conditions.

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collection`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
* Returns `null` if no conditions are configured or if `null` is passed to the setter.
