### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:51:39Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Provides exact, whole‑file counts of language constructs for AI summaries.
- Validates fact counter configurations.

#### Type
- `final class AiFactExtractor` (class, final)  
  - Supertypes: `Object`  
  - Annotations: none  
  - Fields:  
    - `public static final String FACTS_HEADER` (String)  
    - `public static final String LABEL_COUNT_SEPARATOR` (String)  
    - `public static final String ENTRY_SEPARATOR` (String)  
    - `public static final String FACTS_SUFFIX` (String)  
  - Methods:  
    - `private AiFactExtractor()` (constructor, private)  
    - `public static String factsBlock(List<AiFactCounter>, String)`  
    - `public static void validate(List<AiFactCounter>)`  
    - `private static int countMatches(String, String)`

#### Input
- `factsBlock`: `counters` (nullable `List<AiFactCounter>`), `source` (`String`)  
- `validate`: `counters` (nullable `List<AiFactCounter>`)  
- `countMatches`: `pattern` (`String`), `source` (`String`)

#### Output
- `factsBlock`: `String` facts block or empty string  
- `validate`: void, throws `IllegalArgumentException` on invalid config  
- `countMatches`: `int` number of non‑overlapping regex matches

#### Core logic
- `factsBlock`  
  - Return empty if `counters` null/empty.  
  - Iterate counters; skip if counter, label, or pattern null.  
  - Append `ENTRY_SEPARATOR` between entries.  
  - Append `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)`.  
  - If no entries, return empty.  
  - Return `FACTS_HEADER + sb + FACTS_SUFFIX`.
- `validate`  
  - Return if `counters` null.  
  - For each counter:  
    - Throw if counter null? (continues).  
    - Throw if label null.  
    - Throw if pattern null.  
    - Compile pattern; catch `PatternSyntaxException` and rethrow as `IllegalArgumentException` with label context.
- `countMatches`  
  - Compile pattern, create matcher on source.  
  - Loop `matcher.find()` incrementing `count`.  
  - Return `count`.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block  
- `validate(List<AiFactCounter>) -> void` – validate counters

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
- All methods are static, stateless, thread‑safe. No shared mutable state.
