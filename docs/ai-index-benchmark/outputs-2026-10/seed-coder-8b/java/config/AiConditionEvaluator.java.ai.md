### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:10:55Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI condition trees against file contexts

#### Purpose
Evaluates conditions for AI files
Validates condition structures

#### Type
public final class
Implements core logic for condition evaluation and validation

#### Input
AiCondition objects, AiFileContext objects
Constructor parameters: none
Method parameters: AiCondition, AiFileContext

#### Output
boolean (matches, usesLines)
void (validate)
Throws exceptions for invalid conditions

#### Core logic
- Traverses condition trees (AND, OR, NOT)
- Validates condition structures
- Checks file attributes against conditions
- Determines if line counting is needed

#### Public API
9 methods:
- matches(AiCondition, AiFileContext) -> boolean
- validate(AiCondition) -> void
- usesLines(AiCondition) -> boolean
- 6 private helper methods (branchCount, matchesExtension, globMatches, parseEpochMilli, validateGroup, anyUsesLines)

#### Dependencies
- java.time.Instant
- java.time.format.DateTimeParseException
- java.util.Collections
- java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- IllegalArgumentException for invalid conditions
- IllegalStateException for misconfigured nodes
- DateTimeParseException for invalid date strings

#### Concurrency
Thread-safe due to immutable input objects
