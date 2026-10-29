### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:27:08Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration object holding forty-one llama.cpp generation parameters for JNI provider initialization.

#### Purpose
* Immutable value type for llama.cpp JNI provider settings.
* Centralizes default values and validation for model generation configuration.

#### Type
* `public final class LlamaCppJniConfig`
* `public static final class Builder`
* Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`
* Fields: 41 private final fields (String, int, float, Boolean, List)

#### Input
* Constructor: `Builder` instance (private).
* Static factory: `builder(String modelPath)` requires non-null model path.
* Builder setters: 40 parameters (int, float, String, Boolean, List).
* Defaults: `AiGenerationConfig.DEFAULT_*` constants applied in Builder initialization.

#### Output
* Return: `LlamaCppJniConfig` instance via `build()`.
* Getters: 41 public methods returning field values (String, int, float, Boolean, List).
* State: Immutable fields set once during construction.
* Lists: `drySequenceBreakers()` and `stopStrings()` return unmodifiable views.

#### Core logic
* Private constructor copies all 41 fields from `Builder`.
* `builder(String)` initializes `Builder` with `modelPath` and `AiGenerationConfig` defaults.
* Builder setters update internal state and return `this` for chaining.
* `build()` instantiates `LlamaCppJniConfig` with current `Builder` state.
* Null handling: `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` restore defaults if null.
* Null handling: `drySequenceBreakers`, `stopStrings` default to empty list if null.

#### Public API
* `builder(String) -> Builder`
* `modelPath()..stopStrings() (41 total)`
* `contextSize(int)..stopStrings(List<String>) (40 total)`
* `build() -> LlamaCppJniConfig`

#### Dependencies
* `java.util.Collections`
* `java.util.List`
* `java.util.Objects`
* `net.ladenthin.srcmorph.config.AiGenerationConfig`
* `net.ladenthin.srcmorph.support.ConvertToRecord`
* `org.jspecify.annotations.Nullable`
* Lombok (`@EqualsAndHashCode`, `@ToString`)

#### Exceptions / Errors
* `NullPointerException` thrown if `modelPath` is null in `builder(String)`.
* Null handling: `chatTemplateEnableThinking` allows `null`.
* Null handling: String setters restore defaults if `null` is passed.

#### Concurrency
* `LlamaCppJniConfig` is immutable (final fields).
* `Builder` is not thread-safe.
