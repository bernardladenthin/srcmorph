### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:15Z
- G: 
- A: 0.0.0
- X: file
---
> Counts exact whole-file regex matches to build a deterministic "facts" block prepended to AI summaries.

#### Purpose
- Generates a facts block from AiFactCounter regexes over full source text for exact, model-free counts (rows, tables, functions, etc.).
- Provides fail-fast configuration validation for counters in a `&facts>` list.

#### Type
- Final utility class; private no-arg constructor; not instantiable.
- All members are static; no extensions or implementations.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`: counters with label/pattern pairs; full source text to scan.
- `validate(List<AiFactCounter> counters)`: list of counters to check for null label/pattern and regex compilability.
- Skips null counters or counters with null label or pattern during facts building.

#### Output
- `factsBlock` returns a String: `FACTS_HEADER` + `label: count` entries joined by `ENTRY_SEPARATOR`, ending with `FACTS_SUFFIX`; returns `""` for null/empty counters or when all counters are skipped.
- `validate` returns void; throws IllegalArgumentException on misconfiguration; no side effects.

#### Core logic
- factsBlock: if counters null/empty, return `""`; iterate counters, skip null or null-label/null-pattern; append `ENTRY_SEPARATOR` between entries; append label + `LABEL_COUNT_SEPARATOR` + `countMatches(pattern, source)`. if no entries accumulated, return `""`; else prepend header and append suffix.
- validate: if counters null, return; for each counter, skip null; throw if label null, throw if pattern null; compile pattern via `Pattern.compile`, wrapping PatternSyntaxException in IllegalArgumentException with label context.
- countMatches: compile pattern, create Matcher over source, loop `matcher.find()` incrementing count; returns total non-overlapping matches (zero-width matches count once per position).

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` — Builds exact facts block.
- `validate(List<AiFactCounter> counters) -> void` — Validates label, pattern presence, compilability.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter` (same package, referenced via getLabel/getPattern)

#### Exceptions / Errors
- `IllegalArgumentException` thrown by validate when a counter lacks a label or pattern, or when its regex fails to compile.
- PatternSyntaxException caught inside validate and rethrown as IllegalArgumentException with label context.

#### Concurrency
- Stateless static methods; no shared mutable state; thread-safe by virtue of using only local variables and immutable inputs.
