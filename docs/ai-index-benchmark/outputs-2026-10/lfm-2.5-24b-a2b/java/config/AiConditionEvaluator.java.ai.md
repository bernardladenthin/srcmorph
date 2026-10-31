### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:27:19Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition trees against an AiFileContext, walking the tree and reporting line-count requirements.

#### Purpose
- Evaluates and validates AiCondition trees against an AiFileContext.
- Determines file match status and whether line counting is needed.

#### Type
- Class: public final class AiConditionEvaluator
- No inheritance; final; no subclasses.

#### Input
- Constructor: no parameters.
- Methods: matches(AiCondition, AiFileContext), validate(AiCondition), usesLines(AiCondition).
- Dependencies: AiCondition, AiFileContext, AiConditionGroup, AiSourceExcludeFilter, DateTimeParseException, Collections, List, Null.

#### Output
- matches returns boolean indicating file match.
- validate throws IllegalArgumentException on misconfiguration.
- usesLines returns boolean for lines condition usage.

#### Core logic
- matches recursively walks tree: and (all), or (any), not (negation), extensions (filename), size (range), lines (line count), modifiedAfter/Before (epoch compare), pathGlob (glob).
- validate checks branchCount == 1, recurses, enforces bounds, parses dates, rejects blank glob.
- usesLines recursively checks for lines condition.
- branchCount counts non-null branch/leaf fields.
- parseEpochMilli converts ISO-8601 to epoch milliseconds.

#### Public API
- matches(AiCondition, AiFileContext) -> boolean: evaluates condition against context.
- validate(AiCondition) -> void: validates condition tree.
- usesLines(AiCondition) -> boolean: reports lines condition need.

#### Dependencies
- java.time.Instant, java.time.format.DateTimeParseException
- java.util.Collections, java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecific.annotations.Nullable
- net.ladenthin.srcmorph.config.AiCondition, AiFileContext, AiConditionGroup

#### Exceptions / Errors
- IllegalArgumentException from validate and parseEpochMilli on invalid configuration or date.
- IllegalStateException from matches when no branch is set.
- IllegalArgumentException from parseEpochMilli on malformed ISO-8601.

#### Concurrency
- Stateless final class; no synchronization needed; thread-safe by immutability.
