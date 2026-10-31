### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:07:44Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block summarizing exact regex‑based counts for a source file.

#### Purpose
- Provides exact, whole‑file counts for AI‑generated summaries.  
- Supplies a header‑prefixed facts line for oversize AI paths.

#### Type
- `final class AiFactExtractor` (public).  
- No superclass or interfaces.  
- Contains 5 public static final constants, a private constructor, and 3 static methods.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters and source text.  
- `validate(List<AiFactCounter> counters)` – list of counters.  
- `countMatches(String pattern, String source)` – regex pattern and source text.

#### Output
- `factsBlock` returns a formatted facts string or empty string.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `countMatches` returns an `int` match count.

#### Core logic
- `factsBlock`:  
  - Skip null counters or those with null label/pattern.  
  - Append `label: count` entries separated by `; `.  
  - Prefix with `FACTS_HEADER` and suffix with `FACTS_SUFFIX`.  
- `validate`:  
  - Ensure non‑null label and pattern.  
  - Compile pattern to detect syntax errors; wrap in `IllegalArgumentException`.  
- `countMatches`:  
  - Compile pattern, iterate `matcher.find()` to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.  
- `validate(List<AiFactCounter>) -> void` – validate counter list.  

#### Dependencies
- `java.util.List`  
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` on null label/pattern or invalid regex.  
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- All methods are static and stateless; thread‑safe.
