### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:20:45Z
- G: 
- A: 0.0.0
- X: file
---
> Counts exact whole-file regex facts to prepend to AI-generated summaries.
#### Purpose
- Builds deterministic `Facts (exact, whole file)` block from `AiFactCounter` regexes over full source.
- Validates `<facts>` configuration fail-fast before build.
#### Type
- final class `AiFactExtractor`, utility class, not instantiable, no extends/implements.
#### Input
- `counters: List<AiFactCounter>` nullable/empty allowed, each with `label` and `pattern` regex.
- `source: String` full source text searched for non-overlapping regex matches.
#### Output
- `String` facts block as `FACTS_HEADER + label: count; ... + FACTS_SUFFIX`, or `""` when nothing to report.
- No I/O, no mutated state; pure `StringBuilder` construction.
#### Core logic
- `factsBlock` skips null counters and counters with null `label`/`pattern`, joins `label: countMatches` with `ENTRY_SEPARATOR`.
- `countMatches` compiles `pattern` via `Pattern.compile`, iterates `Matcher.find()` counting non-overlapping matches including zero-width matches.
- `validate` allows null/empty list and null entries, otherwise requires non-null `label`/`pattern` and compilable regex.
- Constants define `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX` formatting.
#### Public API
- `FACTS_HEADER -> String` — exact whole-file facts header prefix.
- `LABEL_COUNT_SEPARATOR -> String` — label-to-count separator.
- `ENTRY_SEPARATOR -> String` — consecutive entry separator.
- `FACTS_SUFFIX -> String` — blank-line separator before AI body.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds facts block or empty.
- `validate(List<AiFactCounter>) -> void` — checks labels and compilable patterns.
#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`.
- `org.jspecify.annotations.Nullable`, `net.ladenthin.srcmorph.config.AiFactCounter`.
#### Exceptions / Errors
- Throws `IllegalArgumentException` on missing `label`, missing `pattern`, invalid regex with `PatternSyntaxException` cause.
- Null-tolerant: null/empty `counters` returns `""` in `factsBlock`, no-op in `validate`; null entries skipped.
#### Concurrency
- Stateless static pure functions, no shared mutable state, thread-safe.
