### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:15Z
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
- `final class AiFactExtractor` (class, final)  
  - No supertypes.  
  - Annotations: none.

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
- `factsBlock`: `String` facts block or empty string.  
- `validate`: void; throws `IllegalArgumentException` on invalid counters.  
- `countMatches`: `int` number of non‑overlapping matches.

#### Core logic
- `factsBlock`:  
  - Return empty if counters null/empty.  
  - Iterate counters; skip null or missing label/pattern.  
  - Append `ENTRY_SEPARATOR` between entries.  
  - Append `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)`.  
  - If no entries, return empty.  
  - Return `FACTS_HEADER + sb + FACTS_SUFFIX`.
- `validate`:  
  - Return if counters null.  
  - For each counter:  
    - Throw if counter null.  
    - Throw if label null.  
    - Throw if pattern null.  
    - Compile pattern; catch `PatternSyntaxException` and rethrow as `IllegalArgumentException` with label context.
- `countMatches`:  
  - Compile pattern, create matcher on source.  
  - Loop `matcher.find()` incrementing count.  
  - Return count.

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
- `IllegalArgumentException` thrown by `validate` for missing label/pattern or invalid regex.  
- `PatternSyntaxException` caught and wrapped.

#### Concurrency
- All methods are static and stateless; thread‑safe.
