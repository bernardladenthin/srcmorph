### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:33Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Generates a facts block summarizing exact counts of language constructs.
- Validates fact counter configurations.

#### Type
- `final class AiFactExtractor` (public, final, no supertype).  
  - Fields: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX` (public static final String).  
  - Private constructor.  
  - Methods: `factsBlock(List<AiFactCounter>, String) -> String`, `validate(List<AiFactCounter>) -> void`, `countMatches(String, String) -> int` (private).

#### Input
- `factsBlock`: list of `AiFactCounter` (may be null/empty), source `String`.  
- `validate`: list of `AiFactCounter` (may be null/empty).  
- `countMatches`: regex `String`, source `String`.

#### Output
- `factsBlock`: formatted facts string or empty string.  
- `validate`: throws `IllegalArgumentException` on invalid counters.  
- `countMatches`: integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip null or missing label/pattern, append `label: count` separated by `ENTRY_SEPARATOR`. Prepend `FACTS_HEADER`, append `FACTS_SUFFIX`. Return empty if no valid counters.
- `validate`: for each counter, ensure non‑null label/pattern; compile pattern to detect syntax errors; throw `IllegalArgumentException` with message.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block from counters and source.  
- `validate(List<AiFactCounter>) -> void` – validate counter list.

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.  
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; no shared mutable state.
