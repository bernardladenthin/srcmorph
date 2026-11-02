### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:09Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Produces exact whole-source counts for configured regex-based facts, formatted for inclusion with AI-generated source summaries.

#### Purpose
- Builds a deterministic facts block from configured `AiFactCounter` labels and regex patterns.
- Validates fact-counter configuration and regex syntax before use.

#### Type
- `public final class AiFactExtractor` — utility class; not instantiable.

#### Input
- `factsBlock(List<AiFactCounter>, String)` — nullable counter list and full source text.
- `validate(List<AiFactCounter>)` — nullable counter list.
- Reads each counter’s label and regex pattern.

#### Output
- `factsBlock` returns a formatted string ending in a blank line, or `""` if there are no reportable counters.
- Public format constants define the header, label/count separator, entry separator, and trailing suffix.
- `validate` returns no value; throws for missing counter fields or invalid regex syntax.

#### Core logic
- `factsBlock` returns `""` for a null or empty counter list.
- Skips null counters and counters with null labels or patterns.
- Counts non-overlapping regex matches across the full source using `Matcher.find()`.
- Formats each entry as `label: count`, joins entries with `; `, and wraps them with the facts header and blank-line suffix.
- `validate` skips null lists and null counters; rejects missing labels or patterns and compiles each pattern to check syntax.

#### Public API
- `FACTS_HEADER` — `String`; exact whole-file facts header.
- `LABEL_COUNT_SEPARATOR` — `String`; separates label from count.
- `ENTRY_SEPARATOR` — `String`; separates fact entries.
- `FACTS_SUFFIX` — `String`; separates facts from following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats exact source counts.
- `validate(List<AiFactCounter>) -> void` — checks fact-counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; invalid-regex errors retain the `PatternSyntaxException` cause.
- `factsBlock` skips null counters, labels, and patterns.
- `countMatches` compiles patterns without catching syntax errors; null `source` is not handled explicitly.

#### Concurrency
- Stateless utility with no mutable instance state or I/O.
