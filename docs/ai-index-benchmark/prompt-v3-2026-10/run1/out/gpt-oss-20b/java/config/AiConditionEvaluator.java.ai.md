### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:23Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata, determining matches and required line counts.

#### Purpose
- Evaluate `AiCondition` trees against `AiFileContext`.
- Validate condition trees for correctness.
- Detect if any condition requires line counting.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).

#### Output
- `boolean` from `matches` and `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on validation or state errors.

#### Core logic
- `matches`: recursively evaluates logical groups; checks leaf conditions (extensions, size, lines, dates, glob).
- `validate`: ensures each node has exactly one branch/leaf; validates group children, bounds, and formats.
- `usesLines`: recursively scans tree for any `lines` condition.
- Helper methods perform branch counting, extension matching, glob exclusion, and ISO‑8601 parsing.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – determine if file satisfies condition.
- `validate(AiCondition) -> void` – throw if condition tree invalid.
- `usesLines(AiCondition) -> boolean` – true if any `lines` condition present.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for invalid configuration or parsing errors.
- `IllegalStateException` if a node sets no or multiple branches/leafs.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
