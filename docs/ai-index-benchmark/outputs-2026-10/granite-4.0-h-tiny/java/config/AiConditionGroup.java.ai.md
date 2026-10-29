### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:07:53Z
- G: 
- A: 0.0.0
- X: file
---
```markdown
## Purpose
Calculates VAT for invoices

## Type
Class, final

## Input
- Constructor: none
- Method parameters: none
- Consumed fields: none

## Output
- Return types: none
- Produced state: none
- Mutated fields: none
- Written resources: none
- Side effects: none

## Core logic
- Constructs an empty group
- Provides getter for conditions
- Provides setter for conditions with defensive copy

## Public API
- AiConditionGroup() -> AiConditionGroup
- getConditions() -> List<AiCondition> (returns child conditions)
- setConditions(Collection<AiCondition>) -> void (sets child conditions)

## Dependencies
- lombok.ToString
- java.util.ArrayList
- java.util.Collection
- java.util.List
- lombok.Nullable
- org.jspecify.annotations.Nullable

## Exceptions / Errors
- None

## Concurrency
- Not thread-safe
```
