### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:48:09Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata.

#### Purpose
- Validates AI condition trees.
- Determines if a file matches a condition tree.
- Detects if line counting is required.

#### Type
- `final class` `AiConditionEvaluator` (no generics).

#### Input
- `AiCondition` tree (constructor, `matches`, `validate`, `usesLines`).
- `AiFileContext` for file facts.
- `List<String>` extensions, `AiRangeCondition` size/lines, date strings, glob patterns.

#### Output
- `boolean` from `matches`, `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid trees or parsing errors.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, extensions, size, lines, modified dates, glob.
- `validate`: ensures exactly one branch/leaf per node, checks bounds, parses dates, non‑blank glob.
- `usesLines`: recursively checks for any `lines` condition to decide if file contents must be read.
- `branchCount`: counts non‑null branch/leaf fields.
- `matchesExtension`: checks file name suffixes.
- `globMatches`: uses `AiSourceExcludeFilter` to test path glob.
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` → constructor, no-op.
- `matches(AiCondition, AiFileContext) -> boolean` → evaluate condition tree.
- `validate(AiCondition) -> void` → throw if tree invalid.
- `usesLines(AiCondition) -> boolean` → detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for misconfigured nodes, missing bounds, blank glob, or invalid date.
- `IllegalStateException` if a node sets no branch/leaf.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
