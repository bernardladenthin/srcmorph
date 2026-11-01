### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:02:43Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-aware AI documentation generation and routes completion requests to local or OpenAI-compatible models.

#### Purpose
- Defines shared run, model, source-selection, fact-extraction, and oversized-file settings for documentation generation.
- Provides provider selection and completion support for local llama.cpp and OpenAI-compatible backends.

#### Responsibilities
- **Run and generation configuration:** `SrcMorphConfiguration` and `AiGenerationConfig` hold source filters, prompts, model and provider routing, output metadata, inference options, and generation controls.
- **Source selection and facts:** `AiConditionGroup` and `AiConditionEvaluator` validate and match file-selection conditions; `AiFactExtractor` counts configured regex matches in source text.
- **Oversized-file policy:** `AiOversizeStrategy` parses configured behavior for oversized files, defaulting to `FAIL`.
- **Provider dispatch and local inference:** `AiGenerationProviderFactory` selects the backend; `LlamaCppJniAiGenerationProvider` lazily loads, reuses, and closes a local model.
- **Compatible requests and completion cleanup:** `OpenAiCompatibleProviderSupport` prepares and validates chat-completion requests; `AiCompletionParser` strips leading reasoning blocks where used.

#### Key units
- `SrcMorphConfiguration` — shared mutable run configuration and documentation metadata.
- `AiGenerationConfig` — per-model generation, provider, hardware, and caching settings.
- `AiConditionEvaluator` — validates condition trees, detects line-count requirements, and matches file contexts.
- `AiFactExtractor` — validates fact-counter definitions and formats regex match counts.
- `AiOversizeStrategy` — maps configuration tokens to oversized-file policies.
- `AiGenerationProvider` — interchangeable generation contract with default zero timings and no-op `close`.
- `AiGenerationProviderFactory` — applies model-level versus run-level provider precedence and selects an implementation.
- `LlamaCppJniConfig` — immutable builder-created settings for local and compatible generation.
- `LlamaCppJniAiGenerationProvider` — invokes and closes the lazily initialized llama.cpp model.
- `OpenAiCompatibleProviderSupport` — builds chat-completion JSON and handles endpoint, API-key, response, and timing details.
- `AiCompletionParser` — removes a leading `THINKING_BLOCK_START_MARKER`…`THINKING_BLOCK_END_MARKER` block and trims answer text.

#### Data flow
- Run and model settings provide source filters, generation definitions, and provider options.
- `AiConditionEvaluator` matches conditions against `AiFileContext`, delegating glob checks to `AiSourceExcludeFilter`; `AiFactExtractor` converts source text and counters into a formatted facts block.
- The provider factory chooses a backend using the model provider when nonblank, otherwise the run provider; a missing key defaults to mock.
- Providers render prompts through `AiPromptSupport`, then generate locally through llama.cpp or prepare compatible chat requests; completion text and optional timings are returned.
- `AiOversizeStrategy.fromConfig` converts the configured oversize token into a handling policy.

#### Dependencies
- Internal configuration and source types include `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Provider integration uses `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, and `Java8CompatibilityHelper`.
- External libraries and APIs include llama.cpp JNI, Jackson, `ChatResponse`/`Usage`, Java collections, regex, time, HTTP/URL, and environment lookup.

#### Cross-cutting
- Configuration beans are mutable and unsynchronized; `LlamaCppJniConfig` is immutable, with a non-thread-safe builder and unmodifiable list views.
- Condition and fact-counter validation is explicit; configuration values generally lack numeric-range validation.
- Provider generation methods declare `IOException`; unknown provider keys raise `IllegalArgumentException`.
- Local model initialization is lazy, with no stated synchronization or coordination between generation and `close()`.
- Plain-HTTP API keys are permitted only for loopback hosts; HTTPS endpoints may use configured keys.
