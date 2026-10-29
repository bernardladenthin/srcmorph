### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:45:16Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable llama.cpp JNI generation settings that configure model, sampling, context, cache, and provider options for AI generation.

#### Purpose
- Value object for llama.cpp JNI generation configuration.
- Centralizes named, defaulted generation settings for a model and provider.

#### Type
- Kind: `class`, `final`.
- Extends: none.
- Implements: none.
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested type: `public static final class Builder`.
- 41 instance fields; all are `private final` and copied from the builder.

#### Input
- `builder(String modelPath)` requires a non-null model path.
- `Builder` accepts optional values for context, sampling, cache, GPU, provider, API, reasoning, and DRY settings.
- `null` for String setters restores `AiGenerationConfig.DEFAULT_*`; `null` list setters become empty lists.
- Constructor consumes the fully populated `Builder` and copies each field into immutable state.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors return primitive, String, or list values for configuration.
- `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList(...)` views.
- The object is a value-type configuration snapshot with generated equality/hash/toString.

#### Core logic
- `Builder` initializes all settings from `AiGenerationConfig.DEFAULT_*` before any overrides.
- `Builder` methods mutate a single builder instance; the model path is enforced with `Objects.requireNonNull(modelPath, "modelPath")`.
- `build()` creates the immutable instance through the private constructor.
- The private constructor copies builder values one-to-one into final fields.
- Accessors expose named configuration values without transformation.
- The class avoids a positional constructor because 41 fields would be unreadable and error-prone.

#### Public API
- `builder(String) -> Builder`
- `modelPath() -> String`
- `modelPath..stopStrings (41 total)`
- `Builder: contextSize..stopStrings (40 total); build() -> LlamaCppJniConfig`

#### Dependencies
- Java: `Collections`, `List`, `Objects`, `@Nullable`
- Project: `AiGenerationConfig`, `ConvertToRecord`, `LlamaCppJniConfigFactory`, `Float`
- Lombok: `EqualsAndHashCode`, `ToString`

#### Exceptions / Errors
- `Objects.requireNonNull(modelPath, "modelPath")` throws `NullPointerException` when the required model path is null.
- String setters normalize `null` to the matching `AiGenerationConfig.DEFAULT_*` value.
- List setters normalize `null` to empty collections.
- No checked exceptions are declared.

#### Concurrency
- `LlamaCppJniConfig` is immutable after construction; all fields are `final`.
- `Builder` is not thread-safe and is intended for single-threaded configuration assembly.
