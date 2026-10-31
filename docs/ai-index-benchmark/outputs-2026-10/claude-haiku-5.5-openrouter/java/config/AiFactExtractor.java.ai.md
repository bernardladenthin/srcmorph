### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:12Z
- G: 
- A: 0.0.0
- X: file
---
> Builds a deterministic, whole-file regex-based fact count line (label: count entries) and validates fact counter configuration.

#### Purpose
- Produces exact, model-free counts of regex matches over full source text, prepended to AI summaries on the oversize path.
- Fails fast on misconfigured fact counter lists via validation.

#### Type
- Class, `public final`, utility (private constructor, not instantiable).
- Annotations: `@Nullable` (org.jspecify) on parameters.

#### Input
- `factsBlock`: `counters` (`@Nullable List<AiFactCounter>`), `source` (String).
- `validate`: `counters` (`@Nullable List<AiFactCounter>`).
- `countMatches`: `pattern` (String regex), `source` (String).
- Reads `AiFactCounter.getLabel()` and `getPattern()`.

#### Output
- `factsBlock` returns `String`: `FACTS_HEADER` + `label: count` entries joined by `; ` + `\n\n`, or `""`.
- `validate` returns void; throws on invalid config.
- `countMatches` returns `int` match count.
- No I/O, no mutable state.

#### Core logic
- `factsBlock`: returns `""` when counters null or empty.
- Iterates counters; skips null counter, null label, or null pattern.
- Appends `ENTRY_SEPARATOR` between entries (checked by builder length).
- Appends `label`, `LABEL_COUNT_SEPARATOR`, and `countMatches(pattern, source)`.
- Returns `""` if no entry was appended; otherwise header + entries + suffix.
- `validate`: returns on null list; skips null counters.
- Throws `IllegalArgumentException` for null label, null pattern, or `PatternSyntaxException` from `Pattern.compile` (wraps cause).
- `countMatches`: compiles pattern per call, loops `Matcher.find()` counting non-overlapping matches; zero-width matches count once per position.

#### Public API
- `factsBlock(@Nullable List<AiFactCounter> counters, String source) -> String` - builds facts line block.
- `validate(@Nullable List<AiFactCounter> counters) -> void` - checks label and regex validity.
- Constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX` (public static final String).

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter` (same package)

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` on missing label, missing pattern, or invalid regex (cause chained).
- Null list or null counter tolerated in both methods (empty result or skip).
- Invalid pattern in `factsBlock` not checked; `Pattern.compile` may throw unhandled `PatternSyntaxException` if validate not called first.

#### Concurrency
- Stateless static methods; thread-safe.
- Pattern compiled per counter call; no shared mutable state.
