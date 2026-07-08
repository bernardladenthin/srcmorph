### AiConditionEvaluator.java
- H: 1.0
- C: 66F4DB55
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T22:45:43Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 5; TODO/FIXME: 0; @Override: 0; methods (approx): 10; constructors: 1; field declarations (w/ modifier): 1

> Evaluates and validates file‑matching conditions for an indexer, supporting logical combinations, size, line count, modification dates, and glob patterns.

#### Purpose
- Validates `AiCondition` trees for correctness.  
- Matches a file context against a condition tree.  
- Determines if line counting is required.

#### Type
final class `AiConditionEvaluator` (no generics).  

#### Input
- `AiCondition` trees (nodes with `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).  
- `AiFileContext` (file facts: name, size, lines, last modified, relative path).  

#### Output
- Boolean match results.  
- Exceptions on validation or parsing errors.  

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, and leaf conditions.  
- `validate`: ensures exactly one branch/leaf per node, checks bounds, parses dates, verifies non‑blank glob.  
- `usesLines`: recursive search for any `lines` condition to avoid unnecessary file reads.  
- Helper methods: `validateGroup`, `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`.  

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition against file.  
- `validate(AiCondition) -> void` – throw if misconfigured.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.  

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`.  
- `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.maven.llamacpp.aiindex.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.  

#### Exceptions / Errors
- `IllegalStateException` if node has no branch/leaf.  
- `IllegalArgumentException` for validation failures or invalid ISO‑8601 dates.  

#### Concurrency
- Stateless; thread‑safe for concurrent use.  

#### Fields
1. `private static final String ERROR_ONE_BRANCH` – error message template.
