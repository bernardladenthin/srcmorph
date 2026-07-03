### AiFieldGenerationConfig.java
- H: 1.0
- C: 6D5D78F0
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:51:49Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 25; TODO/FIXME: 0; @Override: 0; methods (approx): 19; constructors: 1; field declarations (w/ modifier): 6

> A mutable configuration bean that defines a routing rule for AI‑based field generation, including file selection, priority, action, oversize handling, and optional deterministic fact extraction.

#### Purpose
- Holds a single routing rule for the `generate` goal.  
- Determines file matching, action (route, skip, fallback), and strategy for oversized files.

#### Type
- `class` (mutable JavaBean).  
- Modifiers: none.  
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.

#### Input
- Constructor: no arguments.  
- Fields set via setters: `id`, `promptKey`, `aiDefinitionKey`, `condition`, `priority`, `fallback`, `skip`, `onOversize`, `maxChunks`, `facts`, `factsKey`.  
- Dependencies: `AiCondition`, `AiOversizeStrategy`, `AiFactCounter`, `AiFactDefinitionSupport`.

#### Output
- Getter methods expose the configured values.  
- `getOversizeStrategy()` parses `onOversize` into an `AiOversizeStrategy` enum.  
- No external resources written.

#### Core logic
- `getOversizeStrategy()` delegates to `AiOversizeStrategy.fromConfig(onOversize)`.  
- All other methods are simple property accessors.

#### Public API
- `String getId() -> return optional rule id`  
- `void setId(String) -> set rule id`  
- `String getPromptKey() -> return prompt key`  
- `void setPromptKey(String) -> set prompt key`  
- `String getAiDefinitionKey() -> return model key`  
- `void setAiDefinitionKey(String) -> set model key`  
- `AiCondition getCondition() -> return file condition`  
- `void setCondition(AiCondition) -> set file condition`  
- `int getPriority() -> return selection priority`  
- `void setPriority(int) -> set priority`  
- `boolean isFallback() -> check fallback flag`  
- `void setFallback(boolean) -> set fallback`  
- `boolean isSkip() -> check skip flag`  
- `void setSkip(boolean) -> set skip`  
- `String getOnOversize() -> return oversize token`  
- `void setOnOversize(String) -> set oversize token`  
- `AiOversizeStrategy getOversizeStrategy() -> parse oversize strategy`  
- `int getMaxChunks() -> return chunk cap`  
- `void setMaxChunks(int) -> set chunk cap`  
- `List<AiFactCounter> getFacts() -> return fact counters`  
- `void setFacts(List<AiFactCounter>) -> set fact counters`  
- `String getFactsKey() -> return facts key`  
- `void setFactsKey(String) -> set facts key`

#### Dependencies
- `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiOversizeStrategy`  
- `net.ladenthin.maven.llamacpp.aiindex.fact.AiFactCounter`  
- `net.ladenthin.maven.llamacpp.aiindex.fact.AiFactDefinitionSupport`

#### Exceptions / Errors
- `getOversizeStrategy()` throws `IllegalArgumentException` if `onOversize` is non‑blank and does not match a strategy.

#### Concurrency
- No thread‑safety guarantees; intended for single‑threaded configuration building.
