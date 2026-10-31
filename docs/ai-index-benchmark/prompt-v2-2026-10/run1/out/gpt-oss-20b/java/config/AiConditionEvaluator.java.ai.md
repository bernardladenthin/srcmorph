### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:35:03Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates complex file‑selection conditions against file metadata.

#### Purpose
- Evaluate `AiCondition` trees for file inclusion.
- Validate condition syntax and semantics.
- Detect if line counting is required.

#### Type
`public final class AiConditionEvaluator` – class, final, no generics.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).

#### Output
- Boolean match results.
- Validation exceptions (`IllegalArgumentException`).
- Line‑usage flag.

#### Core logic
- `matches`: recursive traversal of condition tree; logical operators (`and`, `or`, `not`) and leaf checks (extensions, size, lines, dates, glob).
- `validate`: ensures exactly one branch/leaf per node, required bounds, non‑empty lists, parsable dates, non‑blank glob.
- `usesLines`: depth‑first search for any `lines` leaf to decide if file contents must be read.
- Helper methods: `branchCount`, `validateGroup`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `AiConditionEvaluator()` – constructor.
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition.
- `validate(AiCondition) -> void` – validate condition tree.
- `usesLines(AiCondition) -> boolean` – check for line‑count dependency.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException` if a node has no branch/leaf.
- `IllegalArgumentException` for:
  - Multiple or zero branches/leafs.
  - Empty `<extensions>`.
  - Unbounded `<size>` or `<lines>`.
  - Unparseable ISO‑8601 dates.
  - Blank `<pathGlob>`.

#### Concurrency
- Stateless; thread‑safe. No mutable shared state.
