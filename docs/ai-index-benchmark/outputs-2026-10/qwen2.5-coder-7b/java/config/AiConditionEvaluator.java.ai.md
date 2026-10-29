### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:37Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates `AiCondition` trees against an `AiFileContext`.

#### Purpose
- Evaluates if a file satisfies a given condition tree.
- Validates a condition tree for misconfigurations.
- Determines if any condition in the tree requires line count for evaluation.

#### Type
- `public final class AiConditionEvaluator`
- `@Nullable` annotations from `org.jspecify.annotations`

#### Input
- Constructor: no parameters
- `matches` method: `AiCondition condition`, `AiFileContext context`
- `validate` method: `AiCondition condition`
- `usesLines` method: `AiCondition condition`
- `validateGroup` method: `AiConditionGroup group`, `String label`
- `branchCount` method: `AiCondition c`
- `matchesExtension` method: `List<String> extensions`, `String fileName`
- `globMatches` method: `String glob`, `String relativePath`
- `parseEpochMilli` method: `String isoInstant`

#### Output
- `matches` method: `boolean` - whether the file matches the condition
- `validate` method: `void` - throws `IllegalArgumentException` on misconfiguration
- `usesLines` method: `boolean` - whether any condition requires line count
- `validateGroup` method: `void` - throws `IllegalArgumentException` on misconfiguration
- `branchCount` method: `int` - number of non-null branch/leaf fields
- `matchesExtension` method: `boolean` - whether the file name ends with any of the extensions
- `globMatches` method: `boolean` - whether the relative path matches the glob pattern
- `parseEpochMilli` method: `long` - epoch milliseconds of the ISO-8601 instant

#### Core logic
- `matches`: Recursively evaluates conditions using `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, and `pathGlob`.
- `validate`: Ensures each condition node sets exactly one branch/leaf field and validates individual conditions.
- `usesLines`: Recursively checks if any condition in the tree is a `lines` condition.
- `validateGroup`: Validates `and` and `or` groups to ensure they have at least one child and recursively validate each child.
- `branchCount`: Counts the number of non-null branch/leaf fields in a condition node.
- `matchesExtension`: Checks if the file name ends with any of the specified extensions.
- `globMatches`: Uses `AiSourceExcludeFilter` to determine if the relative path matches the glob pattern.
- `parseEpochMilli`: Parses an ISO-8601 instant to epoch milliseconds, throwing `IllegalArgumentException` on invalid input.

#### Public API
- `AiConditionEvaluator() -> void` - Creates a new `AiConditionEvaluator`.
- `matches(AiCondition condition, AiFileContext context) -> boolean` - Evaluates if the file satisfies the condition.
- `validate(AiCondition condition) -> void` - Validates the condition tree.
- `usesLines(AiCondition condition) -> boolean` - Determines if any condition requires line count.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` - Thrown when a condition node is misconfigured or an ISO-8601 instant is invalid.

#### Concurrency
- No explicit concurrency handling.
