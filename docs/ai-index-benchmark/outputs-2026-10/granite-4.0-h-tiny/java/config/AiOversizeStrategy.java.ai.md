### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:08:10Z
- G: 
- A: 0.0.0
- X: file
---
Here is a markdown summary of the `AiOversizeStrategy.java` source file:

```markdown
## Purpose
Calculates VAT for invoices

## Type
enum

## Input
- configValue: String

## Output
- Configured oversize strategy for handling oversized files

## Core Logic
- Defines strategies for handling oversized files:
  - FAIL: Abort build (default)
  - SAMPLE: Feed head of file in one call
  - MAP_REDUCE: Chunk file at line boundaries, summarize each chunk
  - DETERMINISTIC: Emit deterministic body

## Public API
- FAIL("fail")
- SAMPLE("sample")
- MAP_REDUCE("mapReduce")
- DETERMINISTIC("deterministic")

## Dependencies
- org.jspecify.annotations.Nullable

## Exceptions / Errors
- IllegalArgumentException if unknown strategy is provided

## Concurrency
- Thread-safe (enum)
```

This summary captures the key aspects of the `AiOversizeStrategy` enum, including its purpose, type, input and output, core logic, public API, dependencies, exceptions/errors, and thread-safety.
