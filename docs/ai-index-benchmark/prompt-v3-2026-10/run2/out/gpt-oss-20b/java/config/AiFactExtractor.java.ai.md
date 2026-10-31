### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:15:00Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic fact counts from regex counters over source text.

#### Purpose
- Generates exact, whole‑file fact counts for AI summaries.
- Provides a deterministic facts block for oversize AI output.

#### Type
- `final class AiFactExtractor` (public, final)
- Extends `Object`; implements no interfaces.
- Contains 4 public static final String constants and a private constructor.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.
- Consumes `AiFactCounter` fields `label` and `pattern`.

#### Output
- `factsBlock` returns a formatted facts string or empty string.
- `validate` returns void; throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns an `int` count of non‑overlapping matches.

#### Core logic
- `factsBlock`: iterate counters, skip null or missing label/pattern, append label and match count separated by constants, build final string with header and suffix.
- `validate`: iterate counters, ensure non‑null label and pattern, compile pattern to detect syntax errors, throw `IllegalArgumentException` if any issue.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String facts block`
- `validate(List<AiFactCounter>) -> void validate counters`

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing label/pattern or invalid regex.
- `countMatches` may throw `PatternSyntaxException` internally, caught in `validate`.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe by design.
