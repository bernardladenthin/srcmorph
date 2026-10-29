### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:07:37Z
- G: 
- A: 0.0.0
- X: file
---
```markdown
## Purpose
Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.

## Type
Class, final, no extends/implements, no generics.

## Input
- Constructor: none
- Methods: `matches(AiCondition, AiFileContext)`, `validate(AiCondition)`, `usesLines(AiCondition)`, `branchCount(AiCondition)`, `matchesExtension(List<String>, String)`, `globMatches(String, String)`, `parseEpochMilli(String)`.

## Output
- Returns `boolean` from `matches`.
- Throws `IllegalArgumentException` from `validate`.
- Returns `boolean` from `usesLines`.
- Returns `int` from `branchCount`.
- Returns `boolean` from `matchesExtension`.
- Returns `boolean` from `globMatches`.
- Parses `String` to `long` from `parseEpochMilli`.

## Core Logic
- `matches` walks the tree, handling `and`, `or`, `not`, extensions, size, lines, and glob conditions.
- `validate` ensures each node has exactly one branch/leaf and checks for valid configurations.
- `usesLines` checks if any node requires line count reading.
- `branchCount` counts non-null branch/leaf fields.
- `matchesExtension` checks file extension match.
- `globMatches` checks file path against glob pattern.
- `parseEpochMilli` parses ISO-8601 instant to epoch milliseconds.

## Public API
- `matches(AiCondition, AiFileContext) -> boolean`: Evaluates condition against file context.
- `validate(AiCondition) -> void`: Validates condition tree.
- `usesLines(AiCondition) -> boolean`: Checks for lines condition.
- `branchCount(AiCondition) -> int`: Counts branch/leaf fields.
- `matchesExtension(List<String>, String) -> boolean`: Checks extension match.
- `globMatches(String, String) -> boolean`: Checks glob match.
- `parseEpochMilli(String) -> long`: Parses ISO-8601 instant.

## Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

## Exceptions / Errors
- `IllegalArgumentException` for invalid condition nodes.

## Concurrency
No explicit threading, synchronization, immutability, or thread-safety notes.
```
