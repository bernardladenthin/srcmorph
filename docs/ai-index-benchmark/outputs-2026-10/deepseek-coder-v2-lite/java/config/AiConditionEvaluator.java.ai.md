### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T00:30:34Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.

#### Purpose
- To determine if a file matches a set of conditions defined in an {@link AiCondition} tree.
- To validate the conditions to ensure they are correctly configured.
- To identify if any condition requires line count information.

#### Type
- Class (`public final class AiConditionEvaluator`)
- No modifiers (final)
- Extends no class
- Implements no interfaces

#### Input
- {@link AiCondition} tree (condition tree)
- {@link AiFileContext} (file facts)

#### Output
- Boolean result (`true` or `false`) indicating whether the file matches the conditions.
- Throws `IllegalArgumentException` if the conditions are not valid.
- Boolean indicating if any condition requires line count information.

#### Core logic
- **matches**: Recursively evaluates the condition tree.
  - Handles `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, and `pathGlob` conditions.
  - Uses helper methods to parse dates and check file extensions.
- **validate**: Ensures the condition tree is correctly configured.
  - Checks that each node sets exactly one branch/leaf.
  - Validates `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, and `pathGlob` conditions.
- **usesLines**: Checks if any condition requires line count information.
  - Recursively checks each condition in the tree.

#### Public API
- `matches(AiCondition condition, AiFileContext context) -> boolean`: Evaluates the condition tree against the file context.
- `validate(AiCondition condition) -> void`: Validates the condition tree.
- `usesLines(AiCondition condition) -> boolean`: Checks if any condition requires line count information.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`

#### Exceptions / Errors
- Throws `IllegalStateException` if a node does not set exactly one branch/leaf.
- Throws `IllegalArgumentException` if any node is invalid.
- Throws `IllegalArgumentException` if an `<extensions>` condition does not list at least one extension.
- Throws `IllegalArgumentException` if a `<size>` or `<lines>` condition does not set `<min>` or `<max>`.
- Throws `IllegalArgumentException` if a `<pathGlob>` condition is blank.

#### Concurrency
- The class is not thread-safe.
- The methods are not synchronized.
- The methods do not rely on shared mutable state.
