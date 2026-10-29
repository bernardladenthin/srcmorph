### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:55:38Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file conditions against context to determine matches, validates configuration, and checks for line-count dependencies.

#### Purpose
- Evaluates and validates `AiCondition` trees against `AiFileContext` to determine file matches.
- Determines if a condition tree requires reading file contents to count lines.

#### Type
- `public final class AiConditionEvaluator`
- No annotations; no generic parameters.

#### Input
- `AiCondition condition`: The condition tree to evaluate or validate.
- `AiFileContext context`: File facts including name, size, line count, and modification time.
- `List<String> extensions`: List of file extensions.
- `String fileName`, `String relativePath`: File path and name strings.
- `long sizeBytes`, `long lineCount`: Numeric file attributes.
- `long lastModifiedEpochMilli`: Modification timestamp.
- `String isoInstant`: ISO-8601 formatted date strings.

#### Output
- `boolean`: Return value indicating match status (`matches`, `usesLines`) or exclusion status (`globMatches`).
- `long`: Parsed epoch milliseconds from ISO-8601 strings.
- `IllegalArgumentException`: Thrown on misconfigured condition nodes (e.g., missing branches, empty groups, invalid dates).
- `IllegalStateException`: Thrown if a condition node sets multiple branches.

#### Core logic
- Recursively traverses `AiCondition` tree to evaluate logical operators (`and`, `or`, `not`).
- Validates that each node sets exactly one branch or leaf field.
- Checks file extensions using `endsWith` against a list.
- Validates `AiRangeCondition` bounds for `size` and `lines` fields.
- Parses ISO-8601 dates to epoch milliseconds for `modifiedAfter` and `modifiedBefore` checks.
- Uses `AiSourceExcludeFilter` to evaluate `pathGlob` patterns against relative paths.
- Detects presence of `lines` conditions to optimize file reading.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` returns true if file satisfies condition.
- `validate(AiCondition) -> void` throws on misconfiguration.
- `usesLines(AiCondition) -> boolean` returns true if line count is needed.

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
- `IllegalArgumentException`: Thrown for invalid configurations (empty groups, missing bounds, blank globs, invalid dates).
- `IllegalStateException`: Thrown if a node sets multiple branches.
- `DateTimeParseException`: Caught internally and wrapped in `IllegalArgumentException` during date parsing.
- Null checks performed on `List` children and extension strings.

#### Concurrency
- No threading or synchronization mechanisms present.
- Stateless class; instance methods are thread-safe.
