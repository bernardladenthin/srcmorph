### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T22:56:51Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition trees against an AiFileContext to determine file inclusion matches.
#### Purpose
- Evaluates and validates AiCondition trees against file context
- Supports combinators (and/or/not), range checks, glob, and date matching for file inclusion
#### Type
- public final class AiConditionEvaluator
- No extends or implements; no generics or annotations on the class itself
#### Input
- AiCondition and AiFileContext parameters passed to matches, validate, and usesLines
- No injected dependencies; relies on context objects for file facts
#### Output
- matches returns boolean indicating file match
- validate returns void, throws IllegalArgumentException on misconfiguration
- usesLines returns boolean indicating whether file contents must be read for line count
#### Core logic
- Walks AiCondition tree (and/or/not + leaves) to evaluate file match against context
- Validates exactly one branch/leaf per node, throws IllegalArgumentException on misconfiguration
- Checks extensions, size range, line count range, modified dates (epoch ms), and path glob using AiSourceExcludeFilter
- Parses ISO-8601 instants to epoch milliseconds for modifiedAfter/modifiedBefore comparisons
#### Public API
- matches(AiCondition, AiFileContext) -> boolean evaluates condition tree for file match
- validate(AiCondition) -> void validates condition tree for misconfiguration
- usesLines(AiCondition) -> boolean reports line count dependency
#### Dependencies
- java.time.Instant
- java.time.format.DateTimeParseException
- java.util.Collections
- java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecify.annotations.Nullable
- AiCondition
- AiFileContext
- AiConditionGroup
- AiRangeCondition
#### Exceptions / Errors
- IllegalArgumentException thrown by validate and parseEpochMilli for misconfiguration
- IllegalStateException thrown by matches when condition has no exactly one branch
- DateTimeParseException caught and rethrown as IllegalArgumentException in parseEpochMilli
#### Concurrency
- No instance state; stateless methods with thread-safe design
- public final class with immutable structure; safe for concurrent use
