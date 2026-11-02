### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:00:59Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Evaluates and validates file‑selection conditions for the source‑morph indexer.

#### Purpose
- Checks if a file satisfies an `AiCondition` tree.
- Validates condition trees for correctness.
- Detects if line counts are required.

#### Type
`final class AiConditionEvaluator` – public, no generics, no inheritance.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` providing fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath.

#### Output
- `boolean` from `matches`, `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid trees or parsing errors.

#### Core logic
- `matches`: recursively evaluates logical groups; leaf checks use helper methods.
- `validate`: ensures exactly one branch/leaf per node, checks bounds, parses dates, trims globs.
- `usesLines`: traverses tree to find any `lines` condition.
- Helper methods perform extension matching, glob exclusion via `AiSourceExcludeFilter`, and ISO‑8601 parsing.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – throw on misconfiguration.
- `usesLines(AiCondition) -> boolean` – detect line‑count usage.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for invalid branches, empty lists, missing bounds, bad dates, blank globs.
- `IllegalStateException` if a node has no branch/leaf.

#### Concurrency
- Stateless; all methods are thread‑safe. No shared mutable state.
