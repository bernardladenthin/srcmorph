### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:20Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file condition trees against file context to determine matches and validates configuration constraints.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` to determine if files match criteria.
- Validates condition tree structure and constraints to fail fast on misconfiguration.

#### Type
- `public final class AiConditionEvaluator`
- No explicit extends/implements; uses `@Nullable` annotations from `org.jspecify.annotations`.

#### Input
- `AiCondition condition`: The condition tree to evaluate or validate.
- `AiFileContext context`: File facts including name, size, line count, and modification time.
- `List<String> extensions`: List of file extensions for matching.
- `String fileName`, `String relativePath`: File path and name data from context.
- `String isoInstant`: ISO-8601 date string for time-based conditions.

#### Output
- `boolean`: Return value from `matches` (true if file satisfies condition) and `usesLines` (true if line counting is needed).
- `long`: Epoch milliseconds parsed from ISO-8601 strings.
- `IllegalArgumentException`: Thrown on invalid condition structure or data.
- `IllegalStateException`: Thrown if `matches` encounters a node with no valid branch.

#### Core logic
- Recursively traverses `AiCondition` tree using `matches` to evaluate logical operators (`and`, `or`, `not`) and leaf conditions.
- Validates that each node sets exactly one branch/leaf field via `branchCount` check.
- Checks specific constraints: non-empty extension lists, bounded size/lines ranges, non-blank path globs, and valid ISO-8601 dates.
- Determines if `lines` condition exists anywhere in the tree to optimize file reading.
- Matches file extensions using `endsWith` and path globs using `AiSourceExcludeFilter`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean`
- `validate(AiCondition) -> void`
- `usesLines(AiCondition) -> boolean`

#### Dependencies
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.config.AiConditionGroup`
- `net.ladenthin.srcmorph.config.AiFileContext`
- `net.ladenthin.srcmorph.config.AiRangeCondition`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.List`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if a node has zero or multiple branches, empty extension list, unbounded range, blank glob, or invalid date.
- `IllegalStateException`: Thrown if `matches` finds a node with no valid branch set.
- `DateTimeParseException`: Caught internally and wrapped in `IllegalArgumentException` during date parsing.

#### Concurrency
- No threading or synchronization; stateless class with no mutable fields.
