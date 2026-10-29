### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:11:13Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI‑condition trees against file metadata, determining matches, validating structure, and detecting line‑count usage.

#### Purpose
- Evaluate `AiCondition` trees against `AiFileContext`.
- Validate condition trees for correct configuration.
- Detect if any condition requires line counting.

#### Type
- `final class AiConditionEvaluator`  
  No generics, no annotations.

#### Input
- `AiCondition` trees (branches: and/or/not, leaves: extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).  
- `AiFileContext` providing file name, size, line count, last modified epoch, relative path.

#### Output
- Boolean match results.  
- Validation throws `IllegalArgumentException`.  
- Line‑usage flag.

#### Core logic
- `matches`: recursively walk tree; evaluate logical combinators and leaf conditions.  
- `validate`: ensure exactly one branch/leaf per node; check non‑empty lists, bounds, parseable dates, non‑blank glob.  
- `usesLines`: recursively search for any `lines` leaf to decide if file contents must be read.  
- Helper methods perform extension matching, glob exclusion via `AiSourceExcludeFilter`, and ISO‑8601 parsing.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate tree.  
- `validate(AiCondition) -> void` – throw on misconfiguration.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` for validation failures or invalid ISO‑8601 dates.  
- `IllegalStateException` if a node has no branch/leaf.

#### Concurrency
- Stateless; all methods are thread‑safe.

#### Counts
- Public methods: 3  
- Private methods: 6  
- Fields: 1 (ERROR_ONE_BRANCH)
