### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:16Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI condition trees against file contexts to determine file inclusion based on criteria like extensions, size, modification time, and path patterns.

#### Purpose
- Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext} to determine file inclusion.

#### Type
- Class: `public final class AiConditionEvaluator`
- Package: `net.ladenthin.srcmorph.config`
- No extends / implements
- Key annotations: none prominent

#### Input
- **Constructor**: no-arg constructor (no-op)
- **`matches(AiCondition, AiFileContext)`**: condition tree, file context (file name, size, line count, last modified epoch, relative path)
- **`validate(AiCondition)`**: condition tree to validate
- **`usesLines(AiCondition)`**: condition tree to check for line-count dependency
- **Private helpers**: `branchCount(AiCondition)`, `matchesExtension(List, String)`, `globMatches(String, String)`, `parseEpochMilli(String)`

#### Output
- **`matches`**: `boolean` — true if the file satisfies the condition tree
- **`validate`**: throws `IllegalArgumentException` on misconfiguration; no normal return
- **`usesLines`**: `boolean` — true if any node requires line count reading
- **Private helpers**: return `boolean` or `long` (epoch milliseconds)

#### Core logic
- `matches`: walks the condition tree (and/or/not combinators) then checks leaf conditions: extensions, size range, line count range, modified-after/before timestamps, path glob matching
- `validate`: fails fast if node does not set exactly one branch/leaf; validates groups (and/or) recursively; validates leaf constraints (non-empty extensions, bounded size/lines, parseable dates, non-blank path glob)
- `usesLines`: recursively checks tree for any `lines` node
- `branchCount`: counts non-null branch/leaf fields (and/or/not/extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob); must be exactly one for valid node

#### Public API
- `matches(AiCondition condition, AiFileContext context) -> boolean` — evaluates condition tree against file context
- `validate(AiCondition condition) -> void` — validates condition tree, throws on misconfiguration
- `usesLines(AiCondition condition) -> boolean` — reports whether any node needs line count

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` if: branch count != 1, empty <and>/<or> group, empty <extensions>, unbounded <size>/<lines>, invalid ISO-8601 instant, blank <pathGlob>
- `IllegalStateException` thrown by `matches` if condition has no branch/leaf set (reaches end of chain)
- `DateTimeParseException` caught and wrapped by `parseEpochMilli`

#### Concurrency
- No threading or synchronization concerns; all methods are stateless and side-effect free
