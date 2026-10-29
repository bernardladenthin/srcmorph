### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:19:28Z
- G: 
- A: 0.0.0
- X: file
---
> Groups multiple AI conditions to serve as the body for logical AND/OR combinators in a Maven configuration.

#### Purpose
* Acts as a wrapper for a collection of `AiCondition` objects.
* Enables Maven plugin binding by providing a named `conditions` field for XML schema mapping.

#### Type
class; public.

#### Input
* `conditions` (Collection<AiCondition>): Passed via `setConditions` method.

#### Output
* `List<AiCondition>`: Returned via `getConditions` method.
* `ArrayList<AiCondition>`: Produced as a defensive copy in `setConditions`.

#### Core logic
* Maintains a list of `AiCondition` objects.
* Performs a defensive copy when setting the conditions collection to ensure internal state integrity.
* Allows for null values to represent unconfigured states.

#### Public API
* `AiConditionGroup()` -> void: Default constructor.
* `getConditions() -> List<AiCondition>`: Retrieves the list of child conditions.
* `setConditions(Collection<AiCondition>) -> void`: Sets the conditions list with a defensive copy.

#### Dependencies
* net.ladenthin.srcmorph.config.AiCondition
* java.util.ArrayList
* java.util.Collection
* java.util.List
* lombok.ToString
* org.jspecify.annotations.Nullable

#### Concurrency
* Not thread-safe; standard POJO behavior.
