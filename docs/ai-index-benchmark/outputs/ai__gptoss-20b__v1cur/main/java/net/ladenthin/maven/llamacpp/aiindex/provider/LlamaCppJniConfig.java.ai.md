### LlamaCppJniConfig.java
- H: 1.0
- C: C7A880F8
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T23:16:08Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 28; TODO/FIXME: 0; @Override: 0; methods (approx): 27; constructors: 1; field declarations (w/ modifier): 26

> Immutable configuration holder for llama.cpp JNI provider.

#### Purpose
- Stores runtime parameters for llama.cpp JNI execution.

#### Type
- Final class `LlamaCppJniConfig`; extends `Object`; implements no interfaces; annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- Constructor parameters: 26 values (`String libraryPath`, `String modelPath`, `int contextSize`, `int maxOutputTokens`, `float temperature`, `int threads`, `float topP`, `int topK`, `float minP`, `float topNSigma`, `float repeatPenalty`, `boolean chatTemplateEnableThinking`, `boolean cachePrompt`, `boolean swaFull`, `int cacheReuse`, `int gpuLayers`, `int mainGpu`, `String devices`, `String reasoningEffort`, `int reasoningBudgetTokens`, `float dryMultiplier`, `float dryBase`, `int dryAllowedLength`, `int dryPenaltyLastN`, `List<String> drySequenceBreakers`, `List<String> stopStrings`).

#### Output
- Public accessors return stored values; `drySequenceBreakers()` and `stopStrings()` return unmodifiable lists.

#### Core logic
- Constructor validates non‑null `modelPath` and assigns all fields, defaulting null list parameters to empty lists.
- Accessors simply expose field values; list accessors wrap with `Collections.unmodifiableList`.

#### Public API
- `LlamaCppJniConfig(...) -> LlamaCppJniConfig` constructor (initialization)
- `libraryPath() -> String` returns native lib path
- `modelPath() -> String` returns GGUF model path
- `contextSize() -> int` context window size
- `maxOutputTokens() -> int` max output tokens
- `temperature() -> float` sampling temperature
- `threads() -> int` CPU thread count
- `topP() -> float` nucleus sampling threshold
- `topK() -> int` top‑k limit
- `minP() -> float` min‑p threshold
- `topNSigma() -> float` top‑n‑sigma threshold
- `repeatPenalty() -> float` repetition penalty
- `chatTemplateEnableThinking() -> boolean` chat‑template mode flag
- `cachePrompt() -> boolean` prompt caching flag
- `swaFull() -> boolean` full‑size SWA flag
- `cacheReuse() -> int` KV prefix‑reuse chunk size
- `gpuLayers() -> int` GPU layers offload
- `mainGpu() -> int` primary GPU index
- `devices() -> String` device selection
- `reasoningEffort() -> String` reasoning‑effort value
- `reasoningBudgetTokens() -> int` reasoning token budget
- `dryMultiplier() -> float` DRY multiplier
- `dryBase() -> float` DRY base
- `dryAllowedLength() -> int` DRY allowed length
- `dryPenaltyLastN() -> int` DRY penalty look‑back
- `drySequenceBreakers() -> List<String>` unmodifiable DRY breakers
- `stopStrings() -> List<String>` unmodifiable stop strings

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok: `@EqualsAndHashCode`, `@ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- `NullPointerException` thrown by `Objects.requireNonNull` if `modelPath` is null.

#### Concurrency
- All fields are `final`; class is immutable and thread‑safe.
