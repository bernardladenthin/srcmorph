### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:13:23Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-aware AI documentation generation and routes text-generation requests to mock, local llama.cpp, or OpenAI-compatible backends.

#### Purpose
- Defines run-level and per-generation settings for source selection, facts, model behavior, and oversized-file policies.
- Provides a pluggable generation-provider system with shared request handling, completion parsing, and optional timing metrics.

#### Responsibilities
- **Source selection and validation:** `AiConditionEvaluator` checks nested file conditions against `AiFileContext`; `AiConditionGroup` supports Maven-bound `and`/`or` rules.
- **Generation and run configuration:** `AiGenerationConfig` and `SrcMorphConfiguration` hold model, provider, sampling, runtime, routing, scope, fallback, and output settings.
- **Facts and oversized sources:** `AiFactExtractor` validates regex counters and formats match counts; `AiOversizeStrategy` maps configured tokens to `fail`, `sample`, `mapReduce`, or `deterministic` policies.
- **Provider selection and inference:** `AiGenerationProviderFactory` resolves provider precedence; provider implementations include mock, llama.cpp JNI, and OpenAI-compatible backends.
- **Request and completion handling:** `OpenAiCompatibleProviderSupport` builds requests and applies endpoint, API-key, error, and timing policies; `AiCompletionParser` cleans leading reasoning blocks.

#### Key units
- `AiConditionEvaluator` — validates and evaluates condition trees, including whether line counts are required.
- `AiFactExtractor` — scans complete source text with configured regex counters and formats exact counts.
- `AiGenerationConfig` — mutable per-generation settings bean with local and OpenAI-compatible defaults.
- `SrcMorphConfiguration` — mutable run-level configuration for source scope, AI definitions, routing, fallback, and output metadata.
- `AiOversizeStrategy` — parses oversized-source policy tokens, defaulting to `FAIL`.
- `AiGenerationProvider` — backend contract for text generation, optional timings, and resource cleanup.
- `AiGenerationProviderFactory` — selects a backend, preferring a nonblank model provider over the run-level provider.
- `LlamaCppJniAiGenerationProvider` — lazily loads and reuses a GGUF model for local inference and returns parsed completions.
- `LlamaCppJniConfig` — immutable local-runtime and generation configuration built through a mutable builder.
- `OpenAiCompatibleProviderSupport` — serializes chat-completion requests and applies API-key, endpoint, error, and timing rules.
- `AiCompletionParser` — removes a leading reasoning block or raises `IOException` if it is unclosed.

#### Data flow
- `AiConditionEvaluator` validates configured rules, identifies required file facts, then evaluates each `AiFileContext`.
- `AiFactExtractor` applies configured counters to complete source text and produces a facts block.
- Run-level and generation-level configuration supplies settings to downstream consumers; `AiOversizeStrategy.fromConfig` converts tokens to policy values.
- `AiGenerationProviderFactory` resolves provider settings and creates the backend; the selected provider consumes an `AiGenerationRequest` and returns completion text, optionally with timings.
- The llama.cpp path builds inference inputs using `AiPromptSupport`, obtains a chat response, and parses its completion; the OpenAI-compatible path serializes chat messages and derives timings from response usage and elapsed time.

#### Dependencies
- Internal configuration and rule types include `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Provider types include `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `MockAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- Local inference uses `LlamaCppJniProviderSupport`, `LlamaModel`, llama.cpp response types, and `ChatResponseParser`; OpenAI-compatible handling uses Jackson and `ChatResponse`/`Usage`.
- Utilities include Java time, collections, regex, URL and environment lookup, `Runtime`, Lombok `@ToString`, and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration beans are generally mutable and unsynchronized; `LlamaCppJniConfig` is immutable with a mutable, non-thread-safe builder.
- `AiConditionEvaluator` and `AiFactExtractor` are stateless; condition, timestamp, counter, and regex validation reject malformed input.
- The provider contract extends `AutoCloseable`; default timing and close behavior provide zero metrics and a no-op cleanup.
- Llama.cpp model initialization and access are lazy and unsynchronized; the provider closes the model when present.
- API keys are resolved from a configured environment variable and rejected for plain-HTTP endpoints outside supported loopback hosts.
