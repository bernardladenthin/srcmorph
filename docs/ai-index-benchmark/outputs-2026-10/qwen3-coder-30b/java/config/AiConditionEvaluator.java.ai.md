### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:06:29Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates AI configuration conditions against file metadata to determine if files match specified criteria.

#### Purpose
- Evaluates AI condition trees against file context data.
- Validates condition tree structures for correctness.

#### Type
Final class extending no types; implements no interfaces; uses generics with @Nullable annotations.

#### Input
- Constructor takes no parameters.
- `matches` method takes `AiCondition` and `AiFileContext`.
- `validate` method takes `AiCondition`.
- `usesLines` method takes `AiCondition`.
- `validateGroup` method takes `AiConditionGroup` and `String`.
- `branchCount` method takes `AiCondition`.
- `matchesExtension` method takes `List<String>` and `String`.
- `globMatches` method takes `String` and `String`.
- `parseEpochMilli` method takes `String`.

#### Output
- `matches` returns `boolean`.
- `validate` returns `void`.
- `usesLines` returns `boolean`.
- `branchCount` returns `int`.
- `matchesExtension` returns `boolean`.
- `globMatches` returns `boolean`.
- `parseEpochMilli` returns `long`.

#### Core logic
- Recursively evaluates condition trees using AND/OR/NOT logic.
- Validates that each condition node has exactly one active branch.
- Checks file extensions against a list of allowed extensions.
- Compares file size and line count against range conditions.
- Compares file modification timestamps against ISO-8601 date strings.
- Matches file paths against glob patterns using `AiSourceExcludeFilter`.
- Parses ISO-8601 timestamps to epoch milliseconds.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean`: Determines if file context satisfies condition.
- `validate(AiCondition) -> void`: Validates condition tree structure.
- `usesLines(AiCondition) -> boolean`: Reports if lines condition is present.
- `validateGroup(AiConditionGroup, String) -> void`: Validates AND/OR groups.
- `branchCount(AiCondition) -> int`: Counts active branches in a condition.
- `matchesExtension(List<String>, String) -> boolean`: Checks file extension match.
- `globMatches(String, String) -> boolean`: Checks glob pattern match.
- `parseEpochMilli(String) -> long`: Parses ISO-8601 timestamp to milliseconds.

#### Dependencies
- java.time.Instant
- java.time.format.DateTimeParseException
- java.util.Collections
- java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- Throws `IllegalStateException` when condition has zero or multiple branches.
- Throws `IllegalArgumentException` for invalid conditions including empty groups, unbounded ranges, unparseable dates, blank globs, and missing extensions.
- Throws `DateTimeParseException` during timestamp parsing, wrapped in `IllegalArgumentException`.

#### Concurrency
- No explicit concurrency handling; class is effectively immutable and stateless.
