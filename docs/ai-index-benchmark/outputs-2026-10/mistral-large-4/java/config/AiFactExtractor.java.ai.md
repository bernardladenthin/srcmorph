### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:34:59Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic facts block of exact whole-file regex match counts from AiFactCounters to prepend to AI-generated summaries on oversize source files.

#### Purpose
- Generates model-free, exact counts (rows, tables, boolean fields, functions) by matching configured regex patterns across entire source text
- Produces a formatted facts string for prepending to AI body content on the oversize path

#### Type
- final class (utility, non-instantiable via private constructor)
- package net.ladenthin.srcmorph.config
- no extends/implements
- parameters annotated with org.jspecify.annotations.Nullable

#### Input
- `factsBlock`: @Nullable List<AiFactCounter> counters, String source (full source text)
- `validate`: @Nullable List<AiFactCounter> counters
- AiFactCounter exposes getLabel() (String) and getPattern() (regex String)

#### Output
- `factsBlock` returns String: FACTS_HEADER + entries joined by ENTRY_SEPARATOR + FACTS_SUFFIX, or "" when no valid counters
- `validate` returns void; throws on misconfiguration
- Pure functions; no I/O or side effects

#### Core logic
- factsBlock: return "" if counters null/empty; iterate counters skipping null elements or null label/pattern; append `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)` with ENTRY_SEPARATOR between entries; if any entries exist, wrap with FACTS_HEADER prefix and FACTS_SUFFIX else return ""
- validate: return if counters null; for each non-null counter throw IllegalArgumentException if label null or pattern null; attempt Pattern.compile(pattern) and wrap PatternSyntaxException in IllegalArgumentException with label context
- countMatches: compile pattern, create Matcher over source, increment counter for each matcher.find() (non-overlapping matches, zero-width counted per position)

#### Public API
- `FACTS_HEADER` -> String constant: header marking exact whole-file counts
- `LABEL_COUNT_SEPARATOR` -> String constant: ": " between label and count
- `ENTRY_SEPARATOR` -> String constant: "; " between consecutive entries
- `FACTS_SUFFIX` -> String constant: "\n\n" trailing separator before AI body
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String`: builds facts block or empty string
- `validate(@Nullable List<AiFactCounter>) -> void`: fails fast on null label/pattern or invalid regex

#### Dependencies
- java.util.List
- java.util.regex.Matcher
- java.util.regex.Pattern
- java.util.regex.PatternSyntaxException
- org.jspecify.annotations.Nullable
- AiFactCounter

#### Exceptions / Errors
- IllegalArgumentException thrown by validate for missing label, missing pattern, or invalid regex (PatternSyntaxException wrapped with label context)
- Null handling: null counters list yields "" (factsBlock) or no-op (validate); null counter elements skipped in both methods; null label/pattern skipped in factsBlock but rejected in validate

#### Concurrency
- Stateless utility class with only immutable static String constants
- All methods static and pure with no shared mutable state; thread-safe
