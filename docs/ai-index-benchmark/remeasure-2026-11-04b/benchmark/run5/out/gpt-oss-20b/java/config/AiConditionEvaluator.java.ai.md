### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:24Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates and validates file‑selection conditions against file metadata.

#### Purpose
- Checks if a file satisfies a tree of `AiCondition` nodes.
- Validates condition trees for correct structure and bounds.
- Detects whether line counting is required.

#### Type
- `final class AiConditionEvaluator` (class)  
  - Extends `Object`.  
  - No annotations.

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file facts.  
- `validate(AiCondition)` – condition tree.  
- `usesLines(AiCondition)` – condition tree.  
- Internal helpers: `branchCount(AiCondition)`, `matchesExtension(List<String>, String)`, `globMatches(String, String)`, `parseEpochMilli(String)`.

#### Output
- `matches` returns `boolean`.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `usesLines` returns `boolean`.  
- Helpers return booleans or `long`.

#### Core logic
- `matches`  
  - If `and` group: all children must match.  
  - If `or` group: any child must match.  
  - If `not` group: negates child match.  
  - `extensions`: file name ends with any listed extension.  
  - `size`: `AiRangeCondition.contains(sizeBytes)`.  
  - `lines`: `AiRangeCondition.contains(lineCount)`.  
  - `modifiedAfter`: file last modified > parsed epoch.  
  - `modifiedBefore`: file last modified < parsed epoch.  
  - `pathGlob`: uses `AiSourceExcludeFilter.isExcluded(relativePath)`.  
  - Otherwise throw `IllegalStateException(ERROR_ONE_BRANCH + condition)`.

- `validate`  
  - Ensure exactly one branch/leaf via `branchCount`.  
  - For groups: call `validateGroup`.  
  - For `extensions`: non‑empty list.  
  - For `size`/`lines`: must have a bound.  
  - For `modifiedAfter`/`modifiedBefore`: parse ISO‑8601.  
  - For `pathGlob`: non‑blank.

- `usesLines`  
  - Recursively checks for any `lines` node in the tree.

- `validateGroup`  
  - Requires at least one child; validates each child.

- `branchCount`  
  - Counts non‑null branch/leaf fields.

- `matchesExtension`  
  - Returns true if file name ends with any non‑null extension.

- `globMatches`  
  - Delegates to `AiSourceExcludeFilter.isExcluded`.

- `parseEpochMilli`  
  - Parses ISO‑8601 instant to epoch milliseconds; throws `IllegalArgumentException` on failure.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – validate condition tree.  
- `usesLines(AiCondition) -> boolean` – check for line‑count usage.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`.

#### Exceptions / Errors
- `IllegalArgumentException` for invalid condition configuration or ISO‑8601 parsing.  
- `IllegalStateException` if a node has no branch/leaf.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe.
