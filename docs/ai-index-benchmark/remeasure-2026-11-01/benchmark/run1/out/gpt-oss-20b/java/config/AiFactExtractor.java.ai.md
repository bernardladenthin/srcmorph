### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:24:38Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from regex counters for a source file.

#### Purpose
- Provides a deterministic facts block for AI summaries.
- Counts non‑overlapping regex matches across the entire source.

#### Type
`public final class AiFactExtractor` – utility class, no instances.

#### Input
- `factsBlock(List<AiFactCounter>, String)` – list of counters, source text.
- `validate(List<AiFactCounter>)` – list of counters.
- `countMatches(String, String)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty.
- `validate` throws `IllegalArgumentException` on misconfiguration.
- `countMatches` returns integer match count.

#### Core logic
- Skip null counters or those with null label/pattern.
- For each valid counter, append `label: count` to a `StringBuilder`.
- Separate entries with `"; "`; prepend header and suffix newline.
- `validate` ensures non‑null label/pattern and compiles regex.
- `countMatches` uses `Matcher.find()` to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` if a counter lacks label/pattern or regex fails to compile.

#### Concurrency
- Stateless; thread‑safe.
