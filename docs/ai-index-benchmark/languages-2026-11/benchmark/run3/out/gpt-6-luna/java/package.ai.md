### java
- H: 1.0
- C: 68579582
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:01:11Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects and prepares source files for AI-assisted analysis, then routes generation requests to configured local or compatible text-generation providers.

#### Purpose
- Coordinates source-selection rules, generation settings, oversized-file policies, and whole-source facts.
- Provides a common AI generation contract with mock, llama.cpp JNI, and OpenAI-compatible provider support.

#### Responsibilities
- **Source scope and configuration:** Stores run-wide file filters, prompts, models, output settings, field-generation rules, and fallback options; validates and evaluates composable conditions against file facts.
- **Generation preparation:** Defines per-model sampling and provider settings, parses oversized-file strategies, and extracts configured regex-match counts from complete source text.
- **Provider selection and generation:** Resolves run-level and model-level provider keys and exposes shared generation, timing, and cleanup behavior.
- **Provider-specific support:** Configures and runs local llama.cpp JNI inference; prepares OpenAI-compatible chat-completion requests and handles endpoint, credential, error, and timing support.
- **Completion cleanup:** Removes a leading Gemma-style thinking block from completion text.

#### Key units
- `SrcMorphConfiguration` — stores run-wide source, output, prompt, model, fact, and fallback settings.
- `AiConditionEvaluator` — validates and matches `AiCondition` trees against `AiFileContext` facts.
- `AiFactExtractor` — counts configured regex matches over source text and formats nonempty counts.
- `AiGenerationConfig` — holds per-model generation, provider, and prompt-limit settings.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies.
- `AiGenerationProvider` — defines generation, optional timings, and resource cleanup.
- `AiGenerationProviderFactory` — selects `mock`, `llamacpp-jni`, or `openai-compatible` based on configured provider keys.
- `LlamaCppJniAiGenerationProvider` — performs local inference and exposes response, timing, cache, and model-lifecycle behavior.
- `LlamaCppJniConfig` — immutable builder-created configuration for local model and generation settings.
- `AiCompletionParser` — strips a leading thinking block and rejects a missing closing marker.
- `OpenAiCompatibleProviderSupport` — prepares chat-completion JSON and validates endpoint and credential handling without network I/O.

#### Data flow
- `SrcMorphConfiguration` supplies source scope, exclusions, provider/model/prompt definitions, and fallback settings.
- `AiConditionEvaluator` applies validated conditions to file facts; `AiFactExtractor` produces formatted whole-source facts.
- Oversized-file policy tokens are converted through `AiOversizeStrategy.fromConfig`.
- `AiGenerationProviderFactory` applies model-key precedence over the run-level provider key, defaulting to mock when no key is set.
- The selected provider receives an `AiGenerationRequest`; JNI inference builds parameters, calls `chatComplete`, parses text, and may return timing and cache metrics.
- OpenAI-compatible support assembles request JSON and prepares endpoint, credential, error, and timing results.

#### Dependencies
- Configuration and selection types: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiFactCounter`.
- Provider support types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, and `Java8CompatibilityHelper`.
- External libraries: llama JNI model types, Jackson JSON nodes and `ObjectMapper`, Java collections and regex APIs, Lombok, and JSpecify.

#### Cross-cutting
- Configuration JavaBeans are mutable and unsynchronized; `LlamaCppJniConfig` is immutable after construction, while its builder is not thread-safe.
- Condition evaluation and fact extraction are stateless; malformed conditions raise `IllegalArgumentException`.
- Provider failures use `IOException` where declared; unsupported provider keys and invalid endpoint schemes raise `IllegalArgumentException`.
- Timing results share `AiGenerationTimings`; default timings and missing JNI timing data resolve to zero metrics.
- API keys are trimmed, and credentials are not sent over plain HTTP except to recognized loopback hosts.
