### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:17:49Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates complex file selection conditions for source‑code indexing.

#### Purpose
- Validate `AiCondition` trees for correctness.  
- Evaluate whether a file satisfies a condition tree.  
- Detect if a condition requires line counting.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (constructor, `matches`, `validate`, `usesLines`).  
- `AiFileContext` (file facts) in `matches`.  
- `List<String>` extensions, `AiRangeCondition` size/lines, date strings, glob patterns.  
- `AiSourceExcludeFilter` for glob matching.

#### Output
- `boolean` from `matches`, `usesLines`.  
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid configuration.  
- No state mutation.

#### Core logic
- **matches**: recursively walk condition tree; handle `and`, `or`, `not`, leaf conditions (extensions, size, lines, dates, glob).  
- **validate**: ensure each node sets exactly one branch/leaf; check non‑empty lists, bounds, parse dates, non‑blank glob.  
- **usesLines**: recursively search for any `lines` condition to decide if file contents must be read.  
- Helper methods: `branchCount`, `validateGroup`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate file against condition.  
- `validate(AiCondition) -> void` – throw if condition tree invalid.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`  
- `java.util.Collections`, `java.util.List`  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`

#### Exceptions / Errors
- `IllegalArgumentException` for misconfigured nodes, empty lists, missing bounds, invalid dates, blank glob.  
- `IllegalStateException` if a node has no branch/leaf set during matching.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
