### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:53:25Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-summary generation and routes requests to mock, local llama.cpp, or OpenAI-compatible AI providers.

#### Purpose
- Defines source selection, generation settings, and oversized-file handling for `.ai.md` summaries.
- Provides provider selection and completion generation, with optional timing metrics and output cleanup.

#### Responsibilities
- **Configuration and selection:** `SrcMorphConfiguration`, `AiGenerationConfig`, and condition rules define source scope, output and model settings, per-field routing, and file eligibility.
- **Facts and oversized inputs:** `AiFactExtractor` validates and applies regex counters to source text; `AiOversizeStrategy` represents failure, sampling, map-reduce, or deterministic handling.
- **Provider routing and inference:** `AiGenerationProviderFactory` selects mock, llama.cpp JNI, or OpenAI-compatible generation; provider implementations turn requests into completions.
- **Provider support and output handling:** `LlamaCppJniConfig` configures inference; shared helpers build remote requests and resolve endpoints, while `AiCompletionParser` cleans leading thinking blocks.

#### Key units
- `SrcMorphConfiguration` — mutable run-level source-selection, output, routing, fallback-model, and metadata settings.
- `AiGenerationConfig` — generation defaults and overrides for provider, sampling, limits, runtime tuning, stop strings, reasoning, and calibration.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate nested file-selection conditions against `AiFileContext`.
- `AiFactExtractor` — count configured regex matches in full source text and format a facts block.
- `AiOversizeStrategy` — parse and represent the configured oversized-input strategy.
- `AiGenerationProvider` — generation contract with optional timing support and resource cleanup.
- `AiGenerationProviderFactory` — resolve provider settings and dispatch to mock, `llamacpp-jni`, or `openai-compatible`.
- `LlamaCppJniAiGenerationProvider` — lazily load a GGUF model and run local chat completions.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable local and remote provider settings with fluent construction.
- `OpenAiCompatibleProviderSupport` — assemble chat-completion requests, resolve endpoints and keys, check responses, and calculate timings.
- `AiCompletionParser` — remove a leading Gemma 4 thinking block or raise `IOException` for an unterminated block.
- `AiGenerationTimings` — carry generation timing and usage data.

#### Data flow
- `SrcMorphConfiguration` supplies selection and generation settings; `AiConditionEvaluator` checks condition trees against file facts.
- `AiFactExtractor` applies configured counters to source text, while `AiOversizeStrategy` parses how oversized inputs are to be handled.
- `AiGenerationProviderFactory` resolves provider settings and creates the selected implementation for an `AiGenerationRequest`.
- The llama.cpp path builds inference parameters through `LlamaCppJniProviderSupport`, runs chat completion, and parses the result; the OpenAI-compatible support path builds a JSON request and maps response usage to timing data.
- Providers return completion text and, where supported, timing metrics; implementations without timing data use the contract’s zero-valued defaults.

#### Dependencies
- Internal types include `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptDefinition`, `AiGenerationRequest`, and `AiGenerationTimings`.
- Condition evaluation delegates path-glob exclusions to `AiSourceExcludeFilter`; provider support uses `AiPromptSupport`, `LlamaCppJniProviderSupport`, and `Java8CompatibilityHelper`.
- External dependencies include Java collections, regex, time and file APIs; llama.cpp JNI model and response types; and Jackson JSON nodes and mapper.

#### Cross-cutting
- Configuration uses mutable JavaBeans, nullable omitted settings, defaults, and sentinel values; general validation is limited, with explicit checks for condition trees and fact counters.
- Providers implement `AutoCloseable`; llama.cpp model loading is lazy and cleanup occurs only if initialized.
- The llama.cpp provider’s lazy model initialization is unsynchronized; the configuration beans and builder are not thread-safe.
- An unrecognized nonblank provider key raises `IllegalArgumentException`; OpenAI-compatible support restricts API keys over plain HTTP to recognized loopback hosts.
