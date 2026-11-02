### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:39:22Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates logical file‑selection conditions against file metadata.

#### Purpose
- Evaluate `AiCondition` trees for file inclusion.
- Validate condition syntax and bounds.
- Detect if line counting is required.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (branches: and/or/not, leaves: extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` providing file name, size, line count, last modified time, relative path.

#### Output
- Boolean match result.
- Validation exceptions for malformed trees.
- Boolean indicating line‑count usage.

#### Core logic
- `matches`: recursively walk the tree; logical operators (`and`, `or`, `not`) combine child results; leaf nodes compare file facts to condition values.
- `validate`: ensure each node sets exactly one branch/leaf; check non‑empty lists, bounds, parseable dates, non‑blank globs; recursively validate sub‑groups.
- `usesLines`: detect presence of a `lines` condition anywhere in the tree to decide if file contents must be read.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – throw if tree is malformed.
- `usesLines(AiCondition) -> boolean` – true if any `lines` condition exists.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for invalid condition nodes, empty lists, missing bounds, unparsable dates, blank globs.
- `IllegalStateException` if a node sets no branch/leaf.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.

#### Counts
- Methods: 9 (1 constructor, 8 public/private).
- Fields: 1 (`ERROR_ONE_BRANCH`).
