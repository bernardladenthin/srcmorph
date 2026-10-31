### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:11:49Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition trees against an AiFileContext to determine whether a source file matches a condition.

#### Purpose
- Validates AiCondition trees for well-formedness before matching.
- Determines whether a file satisfies a condition tree.
- Reports whether any node requires line counting.

#### Type
- final class AiConditionEvaluator
- No superclass or interfaces.
- No generics.
- No annotations on the class.

#### Input
- AiCondition condition (tree root)
- AiFileContext context (file facts)
- Consumed fields: none declared
- Read resources: none declared

#### Output
- boolean from matches()
- void from validate()
- boolean from usesLines()
- Mutated state: none
- Written resources: none
- Side effects: none

#### Core logic
- matches() walks the AiCondition tree recursively:
  - and group: all children must match
  - or group: any child must match
  - not: negate child result
  - extensions: check file extension suffix
  - size: check byte size against range
  - lines: check line count against range
  - modifiedAfter: compare last modified epoch milli greater than parsed instant
  - modifiedBefore: compare last modified epoch milli less than parsed instant
  - pathGlob: delegate to AiSourceExcludeFilter
- validate() fails fast on misconfiguration:
  - branchCount must be exactly one
  - and/or groups must have at least one child
  - extensions list must not be empty
  - size and lines ranges must have a bound
  - modifiedAfter and modifiedBefore must parse as ISO-8601 instants
  - pathGlob must not be blank
- usesLines() reports whether any node is a lines condition, enabling lazy file content reading

#### Public API
- constructor() -> void (creates new evaluator)
- matches(AiCondition, AiFileContext) -> boolean (determines file match)
- validate(AiCondition) -> void (throws on misconfiguration)
- usesLines(AiCondition) -> boolean (reports line count need)

#### Dependencies
- java.time.Instant
- java.time.format.DateTimeParseException
- java.util.Collections
- java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- IllegalArgumentException from validate() on misconfiguration
- IllegalArgumentException from parseEpochMilli() on invalid ISO-8601 instant
- DateTimeParseException caught internally in parseEpochMilli()
- IllegalStateException from matches() if condition is unvalidated or malformed

#### Concurrency
- No threading, synchronization, or immutability notes.
