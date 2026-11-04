### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:14:24Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Evaluates and validates file‑selection conditions for the source‑morph indexer.

#### Purpose
- Checks if a file satisfies a complex condition tree.
- Validates condition trees for correctness.
- Detects if line counting is required.

#### Type
`final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` tree (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).

#### Output
- `boolean` from `matches`, `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid trees or parsing errors.

#### Core logic
- `matches`: recursively evaluates and/or/not groups; leaf checks for extensions, size, lines, modification dates, path glob.
- `validate`: ensures exactly one branch/leaf per node; checks non‑empty lists, bounds, parsable dates, non‑blank glob.
- `usesLines`: traverses tree to find any `lines` condition.
- `branchCount`: counts non‑null branch/leaf fields.
- `matchesExtension`: checks file name suffix against list.
- `globMatches`: uses `AiSourceExcludeFilter` to test path glob.
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` – constructor, no-op.
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – validate tree structure.
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for misconfigured nodes, empty lists, unbounded ranges, blank glob, or invalid ISO‑8601 instant.
- `IllegalStateException` if a node sets more than one branch/leaf.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
