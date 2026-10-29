### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:51:40Z
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
- `final class AiConditionEvaluator`  
  No generics, no annotations.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).  
- `AiFileContext` providing file name, size, line count, last modified epoch, relative path.

#### Output
- `boolean` from `matches` and `usesLines`.  
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid trees or parsing errors.

#### Core logic
- `matches`: recursively walk the tree; for each node, evaluate the single set branch/leaf.  
- `validate`: ensure exactly one branch/leaf per node; check non‑empty lists, bounds, parse dates, non‑blank glob.  
- `usesLines`: recursively search for a `lines` condition to decide if file contents must be read.  
- Helper methods perform branch counting, extension matching, glob matching via `AiSourceExcludeFilter`, and ISO‑8601 instant parsing.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – throw on misconfiguration.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` for invalid nodes, empty lists, missing bounds, bad dates, blank glob.  
- `IllegalStateException` if a node has no or multiple branches/leafs.  
- `DateTimeParseException` wrapped in `IllegalArgumentException` during instant parsing.

#### Concurrency
- Stateless; all methods are thread‑safe. No shared mutable state.

#### Counts
- Methods: 9 (1 public, 8 private).  
- Fields: 1 (`ERROR_ONE_BRANCH`).
