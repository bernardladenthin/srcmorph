### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:00Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block summarizing exact regex match counts for a source file.

#### Purpose
- Computes exact, language‑agnostic fact counts from configured regex counters.  
- Prepends a facts header to AI summaries on oversize paths.

#### Type
`public final class AiFactExtractor` – final class, no inheritance, no interfaces.

#### Input
- `factsBlock(List<AiFactCounter>, String)` – list of counters, source text.  
- `validate(List<AiFactCounter>)` – list of counters.  
- `countMatches(String, String)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty string.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `countMatches` returns integer match count.

#### Core logic
- Iterate counters; skip null or missing label/pattern.  
- Append `label: count` entries separated by `; `.  
- Prepend `FACTS_HEADER`, append `FACTS_SUFFIX`.  
- `validate` checks non‑null label/pattern and compiles pattern, throwing on failure.  
- `countMatches` uses `Pattern.compile` and `Matcher.find()` to count non‑overlapping matches.

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
- `IllegalArgumentException` from `validate` for null label/pattern or invalid regex.  
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless utility class; thread‑safe.
