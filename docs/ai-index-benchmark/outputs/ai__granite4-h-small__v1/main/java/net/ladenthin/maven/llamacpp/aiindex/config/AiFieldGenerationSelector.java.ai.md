### AiFieldGenerationSelector.java
- H: 1.0
- C: 4A116EC6
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:48:39Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 1

> Determines which AI generation rule applies to a file based on its metadata and rule priority.

#### Purpose
- Chooses the applicable `AiFieldGenerationConfig` for a given file.
- Validates a set of generation rules for consistency.

#### Type
- final class `AiFieldGenerationSelector`
- No inheritance; contains a single private final field.

#### Input
- Constructor: no parameters.
- `select(configs, context)`:
  - `Iterable<AiFieldGenerationConfig> configs` – rule set in declaration order; `null` entries ignored.
  - `AiFileContext context` – facts about the file to evaluate conditions.
- `validate(configs)`:
  - `Iterable<AiFieldGenerationConfig> configs` – rule set to check.

#### Output
- `select` returns the winning `AiFieldGenerationConfig` or `null` if no rule applies.
- `validate` throws `IllegalArgumentException` on misconfiguration; otherwise no output.

#### Core logic
- Iterate through `configs`:
  - Skip `null` entries.
  - If rule is a fallback, store as `fallback` (first encountered).
  - For non‑fallback rules:
    - Evaluate `config.getCondition()` with `AiConditionEvaluator`.
    - If matches and priority higher than current best, set as `best`.
- Return `best` if found; else return `fallback`.
- `validate`:
  - Count fallbacks; enforce at most one.
  - For every rule:
    - Validate `oversizeStrategy` and `facts` via helper methods.
    - If fallback: ensure no `condition`, not a skip, and has route keys.
    - If non‑fallback: ensure a condition and that it validates; if not a skip, require route keys.
  - Throw `IllegalArgumentException` with descriptive message on any violation.

#### Public API
- `AiFieldGenerationSelector()` – constructs selector.
- `AiFieldGenerationConfig select(Iterable<AiFieldGenerationConfig>, AiFileContext) -> AiFieldGenerationConfig` – resolves rule.
- `void validate(Iterable<AiFieldGenerationConfig>) -> void` – checks rule set consistency.

#### Dependencies
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiConditionEvaluator`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFileContext`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFactExtractor`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for:
  - Multiple fallbacks.
  - Fallback being a skip or having a condition.
  - Missing prompt or AI definition keys.
  - Invalid oversize strategy or facts.
- `IllegalArgumentException` from `conditionEvaluator.validate` if condition is malformed.

#### Concurrency
- Stateless except for immutable `conditionEvaluator`; thread‑safe for concurrent reads.
