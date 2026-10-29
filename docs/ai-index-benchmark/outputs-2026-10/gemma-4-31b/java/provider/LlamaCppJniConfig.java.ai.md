### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:31:49Z
- G: 
- A: 0.0.0
- X: file
---
> Provides immutable configuration settings for the llama.cpp JNI AI provider.

#### Purpose
* Holds hyperparameters and hardware settings for llama.cpp.
* Ensures type-safe, named configuration via a fluent builder.

#### Type
Final class. Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
* `modelPath` (String, required).
* Default constants from `AiGenerationConfig`.
* `List<String>` for sequence breakers and stop strings.

#### Output
* Immutable `LlamaCppJniConfig` instance.

#### Core logic
* Enforces `modelPath` presence via `Objects.requireNonNull` in `Builder`.
* Applies defaults from `AiGenerationConfig` during `Builder` instantiation.
* Provides record-style accessors for 41 configuration fields.
* Returns unmodifiable views of `drySequenceBreakers` and `stopStrings` lists via `Collections.unmodifiableList`.
* Uses a private constructor to force usage of the `Builder` and prevent positional argument errors.

#### Public API
* `builder(String) -> Builder` starts configuration for a specific model.
* `modelPath()..stopStrings() (41 total)` record-style accessors for config fields.
* `Builder.contextSize()..stopStrings() (41 total)` fluent setters for config fields.
* `Builder.build() -> LlamaCppJniConfig` produces the immutable config instance.

#### Dependencies
* `net.ladenthin.srcmorph.config.AiGenerationConfig`
* `net.ladenthin.srcmorph.support.ConvertToRecord`
* `java.util.Collections`
* `java.util.List`
* `java.util.Objects`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `NullPointerException` thrown by `Builder` if `modelPath` is null.

#### Concurrency
* `LlamaCppJniConfig` is immutable and thread-safe.
* `Builder` is not thread-safe.
