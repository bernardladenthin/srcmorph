### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:16Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata, determining matches and required line counts.

#### Purpose
- Validates `AiCondition` trees for correct structure and bounds.  
- Evaluates a file context against a condition tree.  
- Detects if any condition requires line counting.

#### Type
`public final class AiConditionEvaluator` – class, final.  

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).  
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).  

#### Output
- `boolean` from `matches` and `usesLines`.  
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on validation or parsing errors.  

#### Core logic
- `matches`: recursively evaluates logical groups (and, or, not) and leaf conditions (extensions, size, lines, dates, glob).  
- `validate`: ensures each node sets exactly one branch/leaf, checks bounds for ranges, parses dates, and validates non‑blank glob.  
- `usesLines`: recursively searches for any `lines` condition to signal the need for line counting.  

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – checks if file satisfies condition.  
- `validate(AiCondition) -> void` – throws on misconfiguration.  
- `usesLines(AiCondition) -> boolean` – indicates line‑count requirement.  

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`  
- `java.util.Collections`, `java.util.List`  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`  
- `org.jspecify.annotations.Nullable`  

#### Exceptions / Errors
- `IllegalArgumentException` for invalid configuration or date parsing.  
- `IllegalStateException` if a node has no branch/leaf.  

#### Concurrency
- Stateless; all methods are thread‑safe.  

#### Private helpers
- `anyUsesLines(List<AiCondition>) -> boolean`  
- `validateGroup(AiConditionGroup, String) -> void`  
- `branchCount(AiCondition) -> int`  
- `matchesExtension(List<String>, String) -> boolean`  
- `globMatches(String, String) -> boolean`  
- `parseEpochMilli(String) -> long`   (parses ISO‑8601 to epoch ms)
