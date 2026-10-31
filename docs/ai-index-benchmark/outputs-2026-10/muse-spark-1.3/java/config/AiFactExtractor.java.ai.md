### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:13:58Z
- G: 
- A: 0.0.0
- X: file
---
> Counts exact whole-file regex facts to prepend to AI summaries on oversize files.

#### Purpose
- Computes deterministic `label: count` facts block from `AiFactCounter` regexes over full source.
- Validates `<facts>` configuration fail-fast before build.

#### Type
- final class `AiFactExtractor`, utility class with private constructor, no extends/implements.

#### Input
- `counters: List<AiFactCounter>` nullable/empty allowed in `factsBlock`, `validate`.
- `source: String` full source text searched by regexes.
- `counter.getLabel()`, `counter.getPattern()` strings per counter.
- `pattern: String` regex compiled per `countMatches` call.

#### Output
- `factsBlock` returns `String` facts block `FACTS_HEADER + entries + FACTS_SUFFIX` or `""`.
- `validate` returns void, no state mutation.
- No I/O, no fields mutated; pure `StringBuilder` local accumulation.

#### Core logic
- `factsBlock` returns `""` when counters null/empty or all entries skipped.
- Skips null counter or null label/pattern entries.
- Joins entries as `label: count` with `ENTRY_SEPARATOR="; "`, `LABEL_COUNT_SEPARATOR=": "`.
- Prefixes `FACTS_HEADER="**Facts (exact, whole file):** "` and suffix `FACTS_SUFFIX="\n\n"`.
- `countMatches` compiles `Pattern`, iterates `Matcher.find()` counting non-overlapping matches including zero-width.
- `validate` iterates counters, skips null elements, checks label/pattern non-null and `Pattern.compile` validity.

#### Public API
- `FACTS_HEADER() -> String` — marks counts as exact whole-file.
- `LABEL_COUNT_SEPARATOR() -> String` — separates label and count.
- `ENTRY_SEPARATOR() -> String` — separates consecutive fact entries.
- `FACTS_SUFFIX() -> String` — separates facts line and AI body.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds facts block or empty.
- `validate(List<AiFactCounter>) -> void` — checks labels and compilable patterns.

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`.
- `org.jspecify.annotations.Nullable`, `net.ladenthin.srcmorph.config.AiFactCounter`.

#### Exceptions / Errors
- `IllegalArgumentException` on missing label, missing pattern, invalid regex in `validate`.
- Wraps `PatternSyntaxException` as cause with label and regex message.
- `factsBlock` never throws for bad counters; silently skips null label/pattern.

#### Concurrency
- Stateless static pure methods, no shared mutable state, thread-safe.
