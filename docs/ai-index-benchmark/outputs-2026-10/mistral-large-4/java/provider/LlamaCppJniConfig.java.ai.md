### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:39:47Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for llama.cpp JNI AI text generation, holding the GGUF model path, sampling knobs (temperature, top-p/k, min-p, DRY penalty), GPU/CPU layer offload, KV-cache settings, reasoning budget, and stop strings, constructed exclusively through a defaults-backed fluent builder.

#### Purpose
- Immutable value object carrying every llama.cpp JNI provider setting for a generation run
- Single construction path via `Builder` to avoid an unreadable 41-argument positional constructor and guarantee `AiGenerationConfig` defaults

#### Type
- `final class net.ladenthin.srcmorph.provider.LlamaCppJniConfig` with nested `public static final class Builder`
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok over all 41 fields)
- 41 `private final` fields with record-style accessors; full value semantics including exact-bit float comparison

#### Input
- `builder(String modelPath)` — required non-null GGUF model file path
- Builder constructor plus 40 fluent setters — optional overrides; 9 String setters and 2 List setters coalesce `null` to `AiGenerationConfig.DEFAULT_*` / empty list
- Defaults: all 40 optional fields initialized from `AiGenerationConfig.DEFAULT_*` constants at builder creation

#### Output
- Immutable `LlamaCppJniConfig` instance via private constructor copying all 41 builder fields
- 41 record-style accessors; `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList` views
- Lombok-generated `equals`, `hashCode`, `toString`

#### Core logic
- Private constructor assigns all 41 builder fields verbatim — no validation or normalization (builder already applied it)
- `builder(String)` calls `Objects.requireNonNull(modelPath, "modelPath")` — model path is the only mandatory value
- Builder fields default to `AiGenerationConfig.DEFAULT_*` so unset knobs receive domain defaults, not Java zeros
- Null-coalescing setters: `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` (null → default), `drySequenceBreakers`, `stopStrings` (null → empty list); `chatTemplateEnableThinking` preserves null to mean "omit enable_thinking kwarg"
- `build()` delegates to `new LlamaCppJniConfig(this)`

#### Public API
- `builder(String modelPath) -> Builder` — static factory; throws NPE if modelPath null
- `modelPath() -> String` through `stopStrings() -> List<String>` (41 accessors total) — record-style getters for each field
- Builder setters `contextSize(int) -> Builder` through `stopStrings(List<String>) -> Builder` (40 total) — fluent, null restores default for reference types
- `build() -> LlamaCppJniConfig` — constructs the immutable configuration

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- `lombok.EqualsAndHashCode`, `lombok.ToString`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `NullPointerException` from `Objects.requireNonNull(modelPath, "modelPath")` when `builder(null)` is called
- No other thrown exceptions; null setter inputs are silently converted to defaults (except `chatTemplateEnableThinking`, where null is meaningful)

#### Concurrency
- `LlamaCppJniConfig`: immutable (all fields final, list accessors return unmodifiable views) — thread-safe
- `Builder`: explicitly not thread-safe; intended for single-thread fill-then-build
