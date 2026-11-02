### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:14:37Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates complex file‑selection conditions for the source‑morph indexer.

#### Purpose
- Evaluate `AiCondition` trees against `AiFileContext`.
- Validate condition trees for correctness.
- Detect if a condition requires line counting.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (file facts: name, size, line count, last modified, relative path).

#### Output
- Boolean results from `matches`.
- Validation exceptions from `validate`.
- Boolean flag from `usesLines`.

#### Core logic
- `matches`: recursively walk the tree; evaluate logical combinators and leaf conditions.
- `validate`: ensure each node has exactly one branch/leaf; check non‑empty lists, bounds, and parseable dates.
- `usesLines`: detect presence of a `lines` condition anywhere in the tree.
- Helper methods: `branchCount`, `validateGroup`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – throw if misconfigured.
- `usesLines(AiCondition) -> boolean` – true if any `lines` condition present.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException` when a node has no branch/leaf.
- `IllegalArgumentException` for invalid configuration (missing bounds, empty lists, bad dates, blank glob).

#### Concurrency
- Stateless; thread‑safe. No mutable shared state.
