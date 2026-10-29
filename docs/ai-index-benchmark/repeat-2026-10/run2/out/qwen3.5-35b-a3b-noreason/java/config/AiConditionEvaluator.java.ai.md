### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:15Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file condition trees against file context to determine matches and validates configuration constraints.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` to determine if files match specific criteria.
- Validates condition tree structure to ensure correct configuration before evaluation.

#### Type
- `public final class AiConditionEvaluator`
- No explicit inheritance or interfaces.
- Key dependencies: `AiCondition`, `AiFileContext`, `AiConditionGroup`, `AiRangeCondition`, `AiSourceExcludeFilter`.

#### Input
- `AiCondition condition`: The root of the condition tree to evaluate or validate.
- `AiFileContext context`: File facts including name, size, line count, modification time, and path.
- `List<String> extensions`: List of file extensions for matching.
- `String fileName`, `long sizeBytes`, `int lineCount`, `long lastModifiedEpochMilli`, `String relativePath`: Data extracted from `AiFileContext`.
- `String isoInstant`: ISO-8601 formatted date string for time comparisons.
- `String glob`: Path pattern string for glob matching.

#### Output
- `boolean`: Return value from `matches`, `usesLines`, `matchesExtension`, and `globMatches` indicating success or failure.
- `long`: Epoch milliseconds returned by `parseEpochMilli`.
- `void`: `validate` throws exceptions on failure or returns normally on success.
- Side effects: None (pure evaluation logic).

#### Core logic
- **Recursive Evaluation (`matches`)**:
  - Traverses `AiCondition` tree:
    - `and` group: Returns `true` only if all children match.
    - `or` group: Returns `true` if any child matches.
    - `not` group: Returns negation of child match.
    - `extensions`: Checks if file name ends with any listed extension.
    - `size`/`lines`: Checks if file size or line count falls within `AiRangeCondition` bounds.
    - `modifiedAfter`/`modifiedBefore`: Compares file modification time against parsed epoch milliseconds.
    - `pathGlob`: Delegates to `AiSourceExcludeFilter` for path pattern matching.
  - Throws `IllegalStateException` if no branch is set.
- **Validation (`validate`)**:
  - Ensures exactly one branch/leaf is set per node via `branchCount`.
  - Validates `and`/`or` groups have at least one child.
  - Validates `extensions` list is not empty.
  - Validates `size`/`lines` ranges have at least one bound.
  - Validates `modifiedAfter`/`modifiedBefore` strings are valid ISO-8601 dates.
  - Validates `pathGlob` is not blank.
- **Optimization (`usesLines`)**:
  - Recursively checks if any node in the tree is a `lines` condition to determine if file content reading is required.
- **Helper Logic**:
  - `branchCount`: Counts non-null fields in `AiCondition` to enforce single-branch rule.
  - `parseEpochMilli`: Parses ISO-8601 strings to epoch milliseconds, throwing `IllegalArgumentException` on failure.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean`
  - Determines if file context satisfies condition tree.
- `validate(AiCondition) -> void`
  - Validates condition tree structure and values.
- `usesLines(AiCondition) -> boolean`
  - Checks if tree requires line count data.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.config.AiConditionGroup`
- `net.ladenthin.srcmorph.config.AiRangeCondition`
- `net.ladenthin.srcmorph.config.AiFileContext`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.List`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException`: Thrown by `matches` if no branch is set in a node.
- `IllegalArgumentException`: Thrown by `validate` for:
  - Nodes with zero or multiple branches set.
  - Empty `extensions` lists.
  - `size`/`lines` ranges with no bounds.
  - Empty `pathGlob`.
  - Invalid ISO-8601 date strings.
- `DateTimeParseException`: Caught internally by `parseEpochMilli` and wrapped in `IllegalArgumentException`.

#### Concurrency
- No threading, synchronization, or immutability concerns noted; stateless evaluator.
