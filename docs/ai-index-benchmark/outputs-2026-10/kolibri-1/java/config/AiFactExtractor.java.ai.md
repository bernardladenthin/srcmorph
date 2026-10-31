### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:17:20Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic whole-file regex counts for AI fact extraction

#### Purpose
- Produces exact, model-free counts of structural elements (rows, tables, functions) from source text
- Prepends these counts to AI-generated summaries on oversize paths

#### Type
- Final class `AiFactExtractor` in package `net.ladenthin.srcmorph.config`
- Private constructor; all members static
- No annotations on class; uses `@Nullable` from `org.jspecify.annotations`

#### Input
- `counters`: `List<AiFactCounter>` (nullable); each counter has `label` and `pattern`
- `source`: `String` full source text to search
- Constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`

#### Output
- `String`: facts block formatted as `label: count; label: count` prefixed by header and suffixed by blank line
- Empty string when no counters or all counters skipped
- No I/O; pure function

#### Core logic
- Validates counters have non-null label and pattern via `validate()`
- For each valid counter, counts non-overlapping regex matches in source using `Matcher.find()`
- Concatenates `label: count` entries separated by `; `
- Wraps result with `FACTS_HEADER` and `FACTS_SUFFIX`

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String`: builds facts block or empty string
- `validate(List<AiFactCounter>) -> void`: throws `IllegalArgumentException` on invalid regex or missing label/pattern
- `countMatches(String, String) -> int`: private; counts non-overlapping matches

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter` (referenced type)

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate()` for null label/pattern or invalid regex
- `PatternSyntaxException` caught and rethrown as `IllegalArgumentException`
- Null counters skipped in `factsBlock()`

#### Concurrency
- Stateless; no shared mutable state; thread-safe
