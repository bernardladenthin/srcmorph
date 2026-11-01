### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:02:48Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI‑condition trees against file metadata, determining matches and required line counts.

#### Purpose
- Validate AI condition trees for correctness.
- Evaluate file context against conditions.
- Detect if line counting is needed.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).

#### Output
- `boolean` from `matches` and `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid configuration.

#### Core logic
- `matches`: recursively evaluates and/or/not groups; checks leaf conditions (extensions, size, lines, dates, glob).
- `validate`: ensures exactly one branch/leaf per node; validates group children, bounds, and format of dates/globs.
- `usesLines`: traverses tree to detect any `lines` condition.
- Helper methods: `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – throw if node misconfigured.
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for invalid node configuration, bounds, or date format.
- `IllegalStateException` if a node has no or multiple branches/leafs.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
