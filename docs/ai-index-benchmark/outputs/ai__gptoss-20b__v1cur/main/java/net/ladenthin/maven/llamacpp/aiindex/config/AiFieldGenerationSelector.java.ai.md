### AiFieldGenerationSelector.java
- H: 1.0
- C: 4A116EC6
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:54:18Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 1

> Determines which AI field‑generation rule applies to a file and validates rule sets.

#### Purpose
- Chooses a matching `AiFieldGenerationConfig` for a file.
- Validates rule configurations for correctness.

#### Type
- Final class `AiFieldGenerationSelector` (no inheritance).  
- Field: `private final AiConditionEvaluator conditionEvaluator`.

#### Input
- `Iterable<AiFieldGenerationConfig> configs` (declared order, may contain `null`).  
- `AiFileContext context` (file facts).  
- `AiFieldGenerationConfig` objects provide `isFallback()`, `isSkip()`, `getCondition()`, `getPriority()`, `getOversizeStrategy()`, `getFacts()`, `getPromptKey()`, `getAiDefinitionKey()`.

#### Output
- `select` returns an `AiFieldGenerationConfig` (or `null`).  
- `validate` throws `IllegalArgumentException` on misconfiguration.

#### Core logic
- **select**  
  - Iterate configs in declaration order.  
  - Skip `null`.  
  - Store first fallback rule.  
  - For non‑fallback rules: if condition matches (via `AiConditionEvaluator`) and priority higher than current best, update best.  
  - Return best if found; otherwise fallback; otherwise `null`.  
- **validate**  
  - Count fallbacks; enforce at most one.  
  - For each rule:  
    - `getOversizeStrategy()` and `AiFactExtractor.validate()` to trigger early failures.  
    - If fallback: must not be skip, must have no condition, must have prompt & AI definition keys.  
    - If non‑fallback: must have a condition; validate the condition; if not skip, require prompt & AI definition keys.  
  - Throw detailed `IllegalArgumentException` on any violation.

#### Public API
- `AiFieldGenerationSelector() -> no-op` – constructs selector.  
- `select(Iterable<AiFieldGenerationConfig>, AiFileContext) -> AiFieldGenerationConfig` – chooses rule.  
- `validate(Iterable<AiFieldGenerationConfig>) -> void` – validates rules.

#### Dependencies
- `AiConditionEvaluator`  
- `AiFieldGenerationConfig`  
- `AiFileContext`  
- `AiCondition`  
- `AiFactExtractor`  
- Lombok `@ToString`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for:
  - Multiple fallbacks.
  - Fallback being a skip or having a condition.
  - Non‑fallback lacking a condition.
  - Missing prompt or AI definition keys.
  - Invalid oversize strategy or facts extraction.

#### Concurrency
- No explicit synchronization; immutable after construction.  
- Thread‑safe if `AiConditionEvaluator` and `AiFieldGenerationConfig` are thread‑safe.
