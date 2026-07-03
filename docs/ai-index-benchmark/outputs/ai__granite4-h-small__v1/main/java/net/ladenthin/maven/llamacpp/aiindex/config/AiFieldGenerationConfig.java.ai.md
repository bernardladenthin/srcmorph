### AiFieldGenerationConfig.java
- H: 1.0
- C: 6D5D78F0
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:46:08Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 25; TODO/FIXME: 0; @Override: 0; methods (approx): 19; constructors: 1; field declarations (w/ modifier): 6

> Defines a routing rule for AI‑generated field indexing, specifying file selection, model, prompt, priority, and oversize handling.

#### Purpose
- Encapsulates one routing rule for the `generate` goal.
- Determines file routing, skipping, or fallback behavior.

#### Type
- Class, mutable JavaBean, Lombok `@ToString`, no explicit modifiers.

#### Input
- Constructor: no arguments.
- Setter methods: `setId`, `setPromptKey`, `setAiDefinitionKey`, `setCondition`, `setPriority`, `setFallback`, `setSkip`, `setOnOversize`, `setMaxChunks`, `setFacts`, `setFactsKey`.
- Fields: `id`, `promptKey`, `aiDefinitionKey`, `condition`, `priority`, `fallback`, `skip`, `onOversize`, `maxChunks`, `facts`, `factsKey`.

#### Output
- Getter methods: `getId`, `getPromptKey`, `getAiDefinitionKey`, `getCondition`, `getPriority`, `isFallback`, `isSkip`, `getOnOversize`, `getOversizeStrategy`, `getMaxChunks`, `getFacts`, `getFactsKey`.
- Mutated fields via setters; no other side effects.

#### Core logic
- Stores configuration for a routing rule.
- Parses `onOversize` token into `AiOversizeStrategy` via `getOversizeStrategy`.
- Exposes all properties through getters and setters for reflection-based population.

#### Public API
- `getId() -> String?` – returns rule label.
- `setId(String?)` – set rule label.
- `getPromptKey() -> String` – prompt template key.
- `setPromptKey(String)` – set prompt key.
- `getAiDefinitionKey() -> String` – model definition key.
- `setAiDefinitionKey(String)` – set model key.
- `getCondition() -> AiCondition?` – file match condition.
- `setCondition(AiCondition?)` – set condition.
- `getPriority() -> int` – rule priority.
- `setPriority(int)` – set priority.
- `isFallback() -> boolean` – fallback flag.
- `setFallback(boolean)` – set fallback.
- `isSkip() -> boolean` – skip flag.
- `setSkip(boolean)` – set skip.
- `getOnOversize() -> String?` – raw oversize token.
- `setOnOversize(String?)` – set oversize token.
- `getOversizeStrategy() -> AiOversizeStrategy` – parsed strategy.
- `getMaxChunks() -> int` – max map‑reduce chunks.
- `setMaxChunks(int)` – set max chunks.
- `getFacts() -> List<AiFactCounter>?` – deterministic counters.
- `setFacts(List<AiFactCounter>?)` – set counters.
- `getFactsKey() -> String?` – shared facts reference key.
- `setFactsKey(String?)` – set facts key.

#### Dependencies
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiOversizeStrategy`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFactCounter`

#### Exceptions / Errors
- `getOversizeStrategy` throws `IllegalArgumentException` if `onOversize` is non‑blank and does not match a known strategy.

#### Concurrency
- No synchronization; instance is mutable and not thread‑safe.
