### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:48:29Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates logical file‑selection conditions against file metadata.

#### Purpose
- Validate `AiCondition` trees for correct structure and bounds.  
- Evaluate a condition tree against an `AiFileContext`.  
- Detect if a tree requires line counting.

#### Type
`public final class AiConditionEvaluator` – class, final, no generics, no annotations.

#### Input
- `AiCondition` trees (branches: and, or, not; leaves: extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).  
- `AiFileContext` providing file name, size, line count, last modified epoch, relative path.  
- `AiSourceExcludeFilter` for glob matching.

#### Output
- Boolean results from `matches`.  
- Validation exceptions (`IllegalArgumentException`).  
- Boolean from `usesLines`.  
- Internal state unchanged; no side effects.

#### Core logic
- `matches`: recursively walk the tree; evaluate leaf conditions via helper methods.  
- `validate`: ensure exactly one branch/leaf per node; check bounds, non‑empty lists, parse dates, non‑blank glob.  
- `usesLines`: recursively search for a `lines` leaf to decide if file contents must be read.  
- Helper methods: `branchCount`, `validateGroup`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – throw if tree misconfigured.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` for validation failures (missing bounds, empty lists, invalid dates, blank glob).  
- `IllegalStateException` if a node has no branch/leaf.  
- `DateTimeParseException` wrapped in `IllegalArgumentException` during date parsing.

#### Concurrency
- Stateless; all methods are thread‑safe. No mutable shared state.
