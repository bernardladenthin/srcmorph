### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:10:12Z
- G: 
- A: 0.0.0
- X: file
---
> Represents a group of AI conditions combined with AND or OR logic in configuration

#### Purpose
- Groups multiple AI conditions for logical combination in configuration  
- Enables Maven plugin to parse and bind child conditions via XML structure

#### Type
class, public, final (not final), with @ToString, @Nullable, and suppress warnings

#### Input
- Constructor: no parameters  
- Method: setConditions(Collection<AiCondition>) — collection of AI conditions to store

#### Output
- getConditions() → returns List<AiCondition> or null  
- setConditions() → stores a defensive copy of the input collection

#### Core logic
- Stores a list of child AI conditions  
- Defensively copies input collection to prevent external modification  
- Uses field name "conditions" to enable Maven XML binding via singular form

#### Public API
- AiConditionGroup() → constructor (creates empty group)  
- getConditions() → returns conditions list or null  
- setConditions(Collection<AiCondition>) → sets conditions with defensive copy  

#### Dependencies
AiCondition, Collection, List, lombok.ToString

#### Exceptions / Errors
- No exceptions declared or thrown  
- Null-handling via @Nullable annotations and defensive copy

#### Concurrency
- Immutable after construction (no mutation after setConditions)  
- Thread-safe only via defensive copying; no synchronization needed
