### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: 83E61DEB
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:26:20Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Provides a Maven‑based AI field‑generation framework with configurable prompt‑model mappings and pluggable Llama‑Cpp JNI or mock providers for text generation.

#### Purpose
- Configure AI field generation for Maven Llama‑Cpp plugin.
- Expose pluggable Llama‑Cpp JNI or mock generation backends.

#### Responsibilities
- **Configuration** – `AiFieldGenerationConfig`, `AiFieldGenerationSelector`, `AiGenerationConfig`, `AiModelDefinition`, `AiModelDefinitionSupport`.
- **Provider** – `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, `MockAiGenerationProvider`.

#### Key units
- `AiFieldGenerationConfig` – DTO for a single prompt‑model mapping, copies extensions.
- `AiFieldGenerationSelector` – static helper selecting the first matching config for a filename.
- `AiGenerationConfig` – JavaBean of hyper‑parameters, exposes unmodifiable stop strings.
- `AiModelDefinition` – mutable model definition mirroring `AiGenerationConfig`.
- `AiModelDefinitionSupport` – builds key → `AiGenerationConfig` map, validates keys.
- `AiGenerationProvider` – contract: `generate(AiGenerationRequest)` with optional temperature, `close()`.
- `AiGenerationProviderFactory` – selects provider implementation, defaults to `MockAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider` – lazily constructs `LlamaModel`, builds `InferenceParameters`, calls native `chatCompleteText`, parses result.
- `LlamaCppJniConfig` – immutable settings: library path, model path, context size, max output, sampling params, stop strings.
- `MockAiGenerationProvider` – deterministic summary `"Mock summary for <file>"`.
- `AiCompletionParser` – removes `THINKING_BLOCK_*` markers, throws `IOException` on incomplete blocks.

#### Data flow
1. Maven injects `AiFieldGenerationConfig` instances.
2. `AiFieldGenerationSelector.selectForFileName()` picks config by file extension.
3. `AiModelDefinitionSupport` maps `AiModelDefinition` keys to `AiGenerationConfig`.
4. `AiGenerationProviderFactory.create()` selects provider (JNI or mock).
5. Provider builds prompt via `AiPromptSupport`, constructs `InferenceParameters`.
6. Native `LlamaModel.chatCompleteText` executes.
7. `AiCompletionParser.parseCompletion()` strips internal reasoning blocks.
8. Final answer returned to caller.

#### Dependencies
- Internal: `AiFieldGenerationConfig`, `AiModelDefinition`, `AiGenerationConfig`, `AiFieldGenerationSelector`, `AiModelDefinitionSupport`, `AiPromptSupport`.
- External: Lombok (`@ToString`, `@EqualsAndHashCode`), Java Collections (`List`, `Map`, `HashMap`), `org.jspecify.annotations.Nullable`, `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition`.
- Provider internals: `Java8CompatibilityHelper`, `InferenceParameters`, `ModelParameters`, `LlamaModel`.

#### Cross-cutting
- Defensive copying and unmodifiable views for mutable collections.
- Null handling: empty/`null` extensions treated as fallbacks.
- Thread safety: selector uses local variables; config classes immutable after construction.
- Lombok for concise data classes.
- Consistent `IOException` for parsing or I/O errors.
