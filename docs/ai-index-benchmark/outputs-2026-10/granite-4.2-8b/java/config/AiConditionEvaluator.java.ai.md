### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:06:17Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Evaluates {@link AiCondition} trees against {@link AiFileContext} to determine file inclusion, validates condition structure, and checks if line counting is required.

#### Purpose
- Walks the condition tree (and/or/not branches) to test whether a file matches.
- Validates that exactly one branch/leaf is set per node and that required fields (bounds, non-empty glob) are present.
- Determines whether any node uses a {@code lines} condition to decide if file contents need to be read.

#### Type
Kind: class; modifiers: final.  
Implements: none.  
Key generics: none.  
Annotations: none.  
Extends: none.

#### Input
- `condition` (AiCondition): the condition tree to evaluate or validate.
- `context` (AiFileContext): file facts (fileName, relativePath, sizeBytes, lineCount, lastModifiedEpochMilli).
- `isoInstant` (String): ISO-8601 instant string for parsing in `parseEpochMilli`.
- `children` (List<AiCondition>): list of child conditions in groups (passed to helper methods).
- `glob` (String) and `relativePath` (String): for `globMatches`.
- `extensions` (List<String>): list of file extensions.

#### Output
- `boolean`: `matches` returns true if file satisfies condition; `usesLines` returns true if any node uses lines condition.
- `void`: `validate` throws on misconfiguration; `anyUsesLines` returns boolean internally.
- `long`: `parseEpochMilli` returns epoch milliseconds.
- Side effects: `validate` throws `IllegalArgumentException`; `globMatches` uses `AiSourceExcludeFilter`.

#### Core logic
- `matches`: recursively checks `and`, `or`, `not`, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob; throws `IllegalStateException` if no branch set.
- `validate`: counts branches via `branchCount`; ensures exactly one branch; validates groups have children; checks extensions non-empty; size/lines have bound; modified dates parse successfully; pathGlob not blank.
- `usesLines`: recursively checks current node and children for `lines` condition; helper `anyUsesLines` returns true if any child uses lines.
- `branchCount`: counts non-null among and/or/not/extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob.
- `matchesExtension`: returns true if fileName ends with any non-null extension.
- `globMatches`: delegates to `AiSourceExcludeFilter.isExcluded`.
- `parseEpochMilli`: parses ISO-8601 instant to epoch milliseconds; catches `DateTimeParseException` and rethrows `IllegalArgumentException` with message.

#### Public API
- `matches(final AiCondition condition, final AiFileContext context) -> boolean`: returns true if file matches condition (assumes validation passed).
- `validate(final AiCondition condition) -> void`: throws `IllegalArgumentException` on misconfiguration.
- `usesLines(final AiCondition condition) -> boolean`: returns true if any node in tree uses a lines condition.
- `anyUsesLines(final @Nullable List<AiCondition> children) -> boolean`: private helper; not part of public API.
- `branchCount(final AiCondition c) -> int`: private helper; not part of public API.
- `matchesExtension(final List<String> extensions, final String fileName) -> boolean`: private helper; not part of public API.
- `globMatches(final String glob, final String relativePath) -> boolean`: private helper; not part of public API.
- `parseEpochMilli(final String isoInstant) -> long`: private helper; not part of public API.
- `AiConditionEvaluator() -> void`: public no-op constructor.

> Total public/protected members listed: 5 (matches, validate, usesLines, constructor, and three private helpers excluded). Index range: `matches..usesLines` (3 methods plus constructor considered public API; constructor is public no-op).

#### Dependencies
- `AiCondition`
- `AiConditionGroup`
- `AiFileContext`
- `AiRangeCondition`
- `AiSourceExcludeFilter`
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: thrown by `validate` on misconfiguration (empty extensions, missing bounds, blank glob, etc.); thrown by `parseEpochMilli` on invalid ISO-8601.
- `IllegalStateException`: thrown by `matches` if no branch is set (exactly one branch required).
- `DateTimeParseException`: caught internally in `parseEpochMilli` and rethrown as `IllegalArgumentException`.

#### Concurrency
- No threading, synchronization, or mutable shared state. All methods are stateless aside from local variables. Safe for concurrent use.

> Note: Private helper methods (`anyUsesLines`, `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`) are not included in Public API listing per instruction to omit private members from Public API description. Only public/protected members are enumerated.
