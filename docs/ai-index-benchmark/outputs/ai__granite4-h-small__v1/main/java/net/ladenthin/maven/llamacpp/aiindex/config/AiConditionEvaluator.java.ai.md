### AiConditionEvaluator.java
- H: 1.0
- C: 66F4DB55
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T21:40:00Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 5; TODO/FIXME: 0; @Override: 0; methods (approx): 10; constructors: 1; field declarations (w/ modifier): 1

> Evaluates and validates file‑based conditions for an AI indexer, determining matches, validation, and line‑usage needs.  

#### Purpose  
- Validate condition trees for correctness.  
- Determine if a file context satisfies a condition tree.  
- Detect if line counting is required.  

#### Type  
`class` (final). No extends/implements.  

#### Input  
- `AiCondition` tree (constructor of evaluator does nothing).  
- `AiFileContext` facts for `matches`.  
- `AiCondition` for `validate` and `usesLines`.  

#### Output  
- `boolean` from `matches`, `usesLines`.  
- `IllegalArgumentException` for invalid conditions.  

#### Core logic  
- `matches`: recursively walk `and`, `or`, `not`, leafs (`extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).  
- `validate`: enforce single branch/leaf per node, check non‑empty lists, bounds, parse dates, non‑blank glob.  
- `usesLines`: recursively search for a `lines` node.  
- `branchCount`: count non‑null branch/leaf fields.  
- `matchesExtension`: check file name suffixes.  
- `globMatches`: use `AiSourceExcludeFilter` to test path.  
- `parseEpochMilli`: parse ISO‑8601 to epoch ms.  

#### Public API  
- `matches(AiCondition, AiFileContext) -> boolean` – check file matches condition tree.  
- `validate(AiCondition) -> void` – ensure tree is well‑formed.  
- `usesLines(AiCondition) -> boolean` – detect if line counting is needed.  

#### Dependencies  
- `net.ladenthin.maven.llamacpp.aiindex.support.AiSourceExcludeFilter`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`  
- `java.time.Instant`, `java.time.format.DateTimeParseException`  
- `java.util.Collections`, `java.util.List`  
- `org.jspecify.annotations.Nullable`  

#### Exceptions / Errors  
- `IllegalArgumentException` for invalid condition nodes.  
- `IllegalStateException` if a node has no branch/leaf.  
- `DateTimeParseException` wrapped in `IllegalArgumentException` for malformed dates.  

#### Concurrency  
- Immutable evaluator; no shared mutable state. Thread‑safe.
