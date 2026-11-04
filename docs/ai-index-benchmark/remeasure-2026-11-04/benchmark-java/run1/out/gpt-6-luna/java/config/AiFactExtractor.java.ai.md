### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:12Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Produces exact whole-source counts for configured regex-based code facts, ready to prepend to an AI-generated summary.

#### Purpose
- `AiFactExtractor` formats configured fact counts as a deterministic facts block.
- `AiFactExtractor` validates labels and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — utility class; not instantiable.
- No superclass or implemented interfaces.
- Uses `@Nullable` on counter-list parameters.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes counters and full source text.
- `validate(List<AiFactCounter>)` consumes configured counters.
- Each `AiFactCounter` supplies a label and regex pattern.

#### Output
- `factsBlock` returns a formatted string, or `""` when no entries are reportable.
- `validate` returns no value; throws for invalid counter configuration.
- Public constants define the header, separators, and trailing blank line.

#### Core logic
- `factsBlock` returns `""` for a null or empty list; skips null counters and counters with null labels or patterns.
- For each remaining counter, compiles its regex and counts non-overlapping matches across the full source using `Matcher.find()`.
- Formats entries as `label: count`, joins them with `; `, and wraps them with `**Facts (exact, whole file):** ` and `\n\n`.
- `validate` skips null lists and null counters; rejects counters with missing labels or patterns.
- `validate` compiles each pattern to detect invalid regex syntax.

#### Public API
- `FACTS_HEADER` — exact-count facts header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entries separator.
- `FACTS_SUFFIX` — trailing blank line.
- `factsBlock(List<AiFactCounter>, String) -> String` — build facts block.
- `validate(List<AiFactCounter>) -> void` — validate fact counters.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax; wraps `PatternSyntaxException` as the cause.
- `factsBlock` does not validate patterns; invalid regex syntax propagates as `PatternSyntaxException`.
- A null source passed to `factsBlock` can cause `NullPointerException` during matching.

#### Concurrency
- Stateless and uses only local variables; no shared mutable state.
