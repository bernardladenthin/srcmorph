### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:32Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Generates a facts block summarizing exact counts of language constructs.
- Validates fact counter configurations.

#### Type
- `final class AiFactExtractor` (public, final, no supertype).  
  - Annotations: none.  
  - Constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.  
  - Private constructor.  
  - Static methods: `factsBlock`, `validate`, `countMatches`.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`  
  - `counters`: list of counters (may be null/empty).  
  - `source`: full source text.  
- `validate(List<AiFactCounter> counters)`  
  - `counters`: list of counters (may be null/empty).  
- `countMatches(String pattern, String source)`  
  - `pattern`: regex string.  
  - `source`: text to search.

#### Output
- `factsBlock` returns a string: `"**Facts (exact, whole file):** label1: count1; label2: count2\n\n"` or `""`.  
- `validate` throws `IllegalArgumentException` on missing label/pattern or invalid regex.  
- `countMatches` returns an `int` count of non‑overlapping matches.

#### Core logic
- `factsBlock`:  
  1. Return `""` if counters null/empty.  
  2. Iterate counters; skip null or missing label/pattern.  
  3. Append `ENTRY_SEPARATOR` between entries.  
  4. Append `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)`.  
  5. If result empty, return `""`; else prepend `FACTS_HEADER` and append `FACTS_SUFFIX`.  
- `validate`:  
  1. Return if counters null.  
  2. For each counter:  
     - Throw if label null.  
     - Throw if pattern null.  
     - Compile pattern; on `PatternSyntaxException`, wrap in `IllegalArgumentException`.  
- `countMatches`:  
  1. Compile pattern, create matcher on source.  
  2. Loop `matcher.find()` incrementing count.  
  3. Return count.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` – build facts block.  
- `validate(List<AiFactCounter> counters) -> void` – validate counters.

#### Dependencies
- `java.util.List`  
- `java.util.regex.Matcher`  
- `java.util.regex.Pattern`  
- `java.util.regex.PatternSyntaxException`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.  
- `PatternSyntaxException` caught and rethrown as `IllegalArgumentException`.

#### Concurrency
- All methods are static and stateless; thread‑safe.
