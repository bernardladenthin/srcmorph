### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:50:28Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI condition trees against file context to determine file inclusion.

#### Purpose
Evaluates and validates AI condition trees against an {@link AiFileContext} to determine file inclusion; walks and/or/not combinators and leaf conditions (extensions, size, lines, date, glob); validates configuration fails fast on misconfigured nodes; reports whether any node needs line count so the indexer only reads file contents when a {@code lines} condition is actually used.

#### Type
final class {@code AiConditionEvaluator}; no extends / implements; no notable annotations.

#### Input
Constructor: no-arg. Method {@code matches(AiCondition, AiFileContext)}: condition tree and file context. Method {@code validate(AiCondition)}: condition tree to validate. Method {@code usesLines(AiCondition)}: condition tree. Private helpers receive child lists, labels, ISO instant strings, extension lists, glob patterns, and relative paths.

#### Output
Method {@code matches}: returns {@code boolean} whether the file matches the condition tree. Method {@code validate}: throws {@link IllegalArgumentException} on misconfiguration; no normal return. Method {@code usesLines}: returns {@code boolean} if any node needs line count. Private helpers return {@code boolean} or {@code int} count.

#### Core logic
- Walks condition tree: {@code matches} checks {@code and} (all children must match), {@code or} (any child matches), {@code not} (negate child match).
- Leaf conditions: extensions matched via {@code fileName.endsWith}, size via {@code AiRangeCondition.contains}, lines via {@code AiRangeCondition.contains}, modifiedAfter/modifiedBefore via parsed epoch millisecond comparison, pathGlob via {@link AiSourceExcludeFilter}.
- {@code validate} fails fast: ensures exactly one branch/leaf field is set per node; validates group children recursively; checks extensions non-empty; requires size/lines bounds; parses date strings; rejects blank pathGlob.
- {@code usesLines} recurses the tree to detect any {@code lines} condition.
- {@code branchCount} counts non-null branch/leaf fields ({@code and}, {@code or}, {@code not}, {@code extensions}, {@code size}, {@code lines}, {@code modifiedAfter}, {@code modifiedBefore}, {@code pathGlob}) requiring exactly one.

#### Public API
- `matches(AiCondition condition, AiFileContext context) -> boolean` - evaluates condition tree against file context
- `validate(AiCondition condition) -> void` - validates condition tree, throws on misconfiguration
- `usesLines(AiCondition condition) -> boolean` - reports whether any node needs line count

#### Dependencies
Imports: {@code java.time.Instant}, {@code java.time.format.DateTimeParseException}, {@code java.util.Collections}, {@code java.util.List}, {@code net.ladenthin.srcmorph.support.AiSourceExcludeFilter}, {@code org.jspecify.annotations.Nullable}.

Referenced types: {@link AiCondition}, {@link AiFileContext}, {@link AiConditionGroup}, {@link AiRangeCondition}, {@link AiSourceExcludeFilter}.

#### Exceptions / Errors
Throws {@link IllegalArgumentException} when: node does not set exactly one branch/leaf; {@code <and>}/{@code <or>} group has no children; {@code <extensions>} is empty; {@code <size>} lacks {@code <min>} or {@code <max>}; {@code <lines>} lacks {@code <min>} or {@code <max>}; ISO-8601 instant is unparseable; {@code <pathGlob>} is blank.

#### Concurrency
No shared mutable state; all methods are stateless and thread-safe.
