### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:33:56Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI condition trees against file contexts to determine file inclusion.

#### Purpose
- Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext} to determine file inclusion.
- Walks the tree (and/or/not + leaves) and fails fast on misconfigured nodes.

#### Type
- Class: `public final class AiConditionEvaluator`
- No extends / implements
- Key methods: `matches`, `validate`, `usesLines`

#### Input
- Constructor: no-arg, no-op.
- `matches(AiCondition condition, AiFileContext context)`: condition tree, file facts (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).
- `validate(AiCondition condition)`: condition tree to check for misconfiguration.
- `usesLines(AiCondition condition)`: condition tree to check for line-count dependency.
- Private helpers: `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Output
- `matches`: returns `boolean` - `true` if the file matches the condition tree.
- `validate`: throws `IllegalArgumentException` on invalid nodes; no normal return.
- `usesLines`: returns `boolean` - `true` if any node needs the line count.
- `parseEpochMilli`: returns `long` epoch milliseconds; throws `IllegalArgumentException` on bad ISO-8601.

#### Core logic
- `matches`: recursively walks the condition tree:
  - `and`: all children must match; returns `true` if children exist (empty list passes).
  - `or`: any child matches; returns `false` if children exist but none match.
  - `not`: negates the child match result.
  - `extensions`: checks if `fileName` ends with any listed extension.
  - `size`: checks if `context.sizeBytes()` falls within the range.
  - `lines`: checks if `context.lineCount()` falls within the range.
  - `modifiedAfter`: compares `context.lastModifiedEpochMilli()` against parsed epoch.
  - `modifiedBefore`: compares `context.lastModifiedEpochMilli()` against parsed epoch.
  - `pathGlob`: uses `AiSourceExcludeFilter` to test exclusion.
  - Throws `IllegalStateException` if no branch/leaf is set.
- `validate`: fails fast on misconfiguration:
  - Enforces exactly one branch/leaf via `branchCount`.
  - Validates `<and>`/`<or>` groups have at least one child.
  - Validates `<not>` recursively.
  - Validates `<extensions>` is non-empty.
  - Validates `<size>` and `<lines>` have a bound (min or max).
  - Validates `<modifiedAfter>`/`<modifiedBefore>` are parseable ISO-8601.
  - Validates `<pathGlob>` is not blank.
- `usesLines`: returns `true` if any node in the tree is a `lines` condition, recursively checking `and`/`or`/`not` subtrees.
- `branchCount`: counts non-null branch/leaf fields (`and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`); must be exactly one for a valid node.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean`
- `validate(AiCondition) -> void`
- `usesLines(AiCondition) -> boolean`

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate`: throws `IllegalArgumentException` if branch count != 1, `<and>`/`<or>` empty, `<extensions>` empty, `<size>`/`<lines>` unbounded, dates unparseable, or `<pathGlob>` blank.
- `matches`: throws `IllegalStateException` if no branch/leaf field is set on the condition.
- `parseEpochMilli`: throws `IllegalArgumentException` if the ISO-8601 instant is invalid.

#### Concurrency
- No threading or synchronization concerns; all methods are pure functions with no shared mutable state.
