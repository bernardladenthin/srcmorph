### LlamaCppJniConfig.java
- H: 1.0
- C: C7A880F8
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T22:11:02Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 28; TODO/FIXME: 0; @Override: 0; methods (approx): 27; constructors: 1; field declarations (w/ modifier): 26

> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime options as record‑style accessors.

#### Purpose
- Stores llama.cpp native library and model configuration.
- Provides immutable, value‑semantics access to all runtime parameters.

#### Type
- `final class LlamaCppJniConfig` (no inheritance).
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- 26 private final fields: 20 primitives, 6 `List<String>`.

#### Input
- Constructor parameters: 26 values covering library path, model path, token limits, sampling parameters, thread count, GPU options, reasoning settings, DRY settings, and stop strings.
- `modelPath` required (`Objects.requireNonNull`).
- `drySequenceBreakers` and `stopStrings` default to empty lists if `null`.

#### Output
- 26 accessor methods (`libraryPath()`, `modelPath()`, …, `stopStrings()`) returning primitive or immutable list views.
- Immutable state: fields never change after construction.

#### Core logic
- Constructor validates non‑null `modelPath`.
- Assigns all parameters to fields.
- Normalizes nullable list parameters to empty immutable lists.
- Accessors simply return stored values; list getters wrap with `Collections.unmodifiableList`.

#### Public API
- `LlamaCppJniConfig( String, String, int, int, float, int, float, int, float, float, float, boolean, boolean, boolean, int, int, int, int, String, String, int, float, float, int, int, List<String>, List<String> ) -> void` (constructor)
- `libraryPath() -> String` (return library path)
- `modelPath() -> String` (return model path)
- `contextSize() -> int` (return context size)
- `maxOutputTokens() -> int` (return max output tokens)
- `temperature() -> float` (return temperature)
- `threads() -> int` (return thread count)
- `topP() -> float` (return top‑p)
- `topK() -> int` (return top‑k)
- `minP() -> float` (return min‑p)
- `topNSigma() -> float` (return top‑sigma)
- `repeatPenalty() -> float` (return repetition penalty)
- `chatTemplateEnableThinking() -> boolean` (return chat‑template flag)
- `cachePrompt() -> boolean` (return prompt caching flag)
- `swaFull() -> boolean` (return SWA KV flag)
- `cacheReuse() -> int` (return cache‑reuse size)
- `gpuLayers() -> int` (return GPU layer count)
- `mainGpu() -> int` (return primary GPU index)
- `devices() -> String` (return device list)
- `reasoningEffort() -> String` (return reasoning effort)
- `reasoningBudgetTokens() -> int` (return reasoning budget)
- `dryMultiplier() -> float` (return DRY multiplier)
- `dryBase() -> float` (return DRY base)
- `dryAllowedLength() -> int` (return DRY allowed length)
- `dryPenaltyLastN() -> int` (return DRY penalty window)
- `drySequenceBreakers() -> List<String>` (return unmodifiable DRY breakers)
- `stopStrings() -> List<String>` (return unmodifiable stop strings)

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`
- Custom annotation: `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- `NullPointerException` thrown if `modelPath` is `null`.

#### Concurrency
- Immutable after construction; safe for concurrent use.
