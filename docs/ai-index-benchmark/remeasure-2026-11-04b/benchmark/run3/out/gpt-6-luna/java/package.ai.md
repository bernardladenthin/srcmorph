### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:07:53Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-aware AI documentation generation and routes completion requests through local, remote, or mock model providers.

#### Purpose
- Defines source-selection, generation, and oversized-file configuration alongside AI completion-provider support.
- Evaluates per-file conditions and facts, then supplies configured requests to selectable inference backends.

#### Responsibilities
- **Source scope and generation settings:** Configure source filters, model and prompt definitions, generation options, routing, fallback parameters, and output metadata.
- **Conditions and facts:** Validate and evaluate nested file-selection conditions; validate regex counters and format source-match counts.
- **Provider selection and inference:** Resolve model- or run-level provider choices, support local llama.cpp and OpenAI-compatible generation, and provide a mock backend.
- **Completion handling:** Normalize generated text by removing a leading Gemma-4 reasoning block, and expose token and throughput metrics where available.

#### Key units
- `SrcMorphConfiguration` — run-level configuration for source scope, generation routing, fallback parameters, and output metadata.
- `AiGenerationConfig` — per-model provider, inference, sampling, prompt, and output settings with defaults.
- `AiConditionEvaluator` and `AiConditionGroup` — validate and match nested source-selection conditions.
- `AiFactExtractor` — validate configured regex counters and format whole-source match counts.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling choices.
- `AiGenerationProvider` — backend contract for completion generation, timing metrics, and cleanup.
- `AiGenerationProviderFactory` — selects and constructs a backend using model- or run-level provider keys.
- `LlamaCppJniConfig` — immutable provider settings assembled through a fluent builder.
- `LlamaCppJniAiGenerationProvider` — lazily initializes local JNI model inference and reports available timings.
- `OpenAiCompatibleProviderSupport` — builds chat-completion requests and handles endpoint, API-key, HTTP-error, and timing support.
- `AiCompletionParser` — trims completions and strips a leading reasoning block, rejecting an unterminated one.

#### Data flow
- `SrcMorphConfiguration` provides source filters and generation definitions; `AiConditionEvaluator` matches condition trees against per-file facts.
- `AiFactExtractor` applies configured counters to source text and returns a formatted facts block.
- `AiGenerationProviderFactory` resolves the provider key and creates a backend using `LlamaCppJniConfig` and `AiPromptSupport`.
- Backends consume `AiGenerationRequest`; generated completions are normalized, with text and available token or throughput metrics returned.

#### Dependencies
- Configuration references `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, prompt/model definitions, field-generation settings, and fact definitions.
- Provider workflows use `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`; factory implementations include mock, llama.cpp JNI, and OpenAI-compatible providers.
- External integrations include the `net.ladenthin:llama` JNI binding and Jackson JSON support; shared utilities include `Java8CompatibilityHelper`.

#### Cross-cutting
- Configuration types are mutable, unsynchronized JavaBeans; `AiConditionEvaluator` and `AiFactExtractor` are stateless utilities that reject malformed configuration.
- Provider generation and resource failures use `IOException`; provider interface timing defaults are zero-valued and `close()` is a no-op.
- Local model initialization and closure are unsynchronized; the llama.cpp builder is not thread-safe and does not defensively copy supplied lists.
- OpenAI-compatible API keys are restricted to HTTPS or loopback HTTP destinations; nullable and blank oversize-strategy values default to `FAIL`.
