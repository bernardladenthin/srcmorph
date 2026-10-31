### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:14:18Z
- G: 
- A: 0.0.0
- X: file
---
> Prepends exact whole-file regex counts to AI summaries for oversized sources.
#### Purpose
- Computes deterministic language-agnostic facts block from AiFactCounters for AI oversize path.
- Validates AiFactCounter label/pattern configuration fail-fast.
#### Type
- final class AiFactExtractor, utility class, not instantiable via private constructor.
#### Input
- counters: @Nullable List<AiFactCounter> in factsBlock, validate; null/empty yields empty/no-op.
- source: full String source text searched by regex in factsBlock, countMatches.
- counter.getLabel, counter.getPattern: String label/pattern per AiFactCounter; null skipped in factsBlock.
#### Output
- factsBlock -> String facts block with FACTS_HEADER + label: count entries + FACTS_SUFFIX, or "" when nothing to report.
- validate -> void, no state mutation, throws on misconfiguration.
- No I/O, pure computation; appends ENTRY_SEPARATOR, LABEL_COUNT_SEPARATOR via StringBuilder.
#### Core logic
- factsBlock iterates AiFactCounter list, skips null counter/label/pattern, counts non-overlapping regex matches over whole source via countMatches.
- countMatches compiles Pattern, loops Matcher.find incrementing count, including zero-width matches once per position.
- Joins results as label: count with ENTRY_SEPARATOR, prefixes FACTS_HEADER marking exact whole-file counts, suffixes blank line FACTS_SUFFIX.
- validate compiles each non-null AiFactCounter pattern with Pattern.compile to reject invalid regex early.
#### Public API
- factsBlock(List<AiFactCounter>, String) -> String : builds exact facts block.
- validate(List<AiFactCounter>) -> void : checks labels and regexes.
#### Dependencies
- java.util.List, java.util.regex.Matcher, java.util.regex.Pattern, java.util.regex.PatternSyntaxException.
- org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiFactCounter.
- java.lang.String, StringBuilder, IllegalArgumentException.
#### Exceptions / Errors
- IllegalArgumentException on null label: A fact counter must have a label.
- IllegalArgumentException on null pattern: A fact counter must have a pattern.
- IllegalArgumentException wrapping PatternSyntaxException on invalid regex with label + message, cause preserved.
- Null-tolerant: null/empty counters returns "" / returns; null counter entry skipped.
#### Concurrency
- Stateless static pure functions, local Matcher/StringBuilder only, thread-safe absent concurrent mutation of input list.
