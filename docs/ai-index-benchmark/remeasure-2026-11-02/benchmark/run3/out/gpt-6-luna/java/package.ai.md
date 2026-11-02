### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:59:55Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects source files for AI-generated summaries and configures the generation backends that turn selected source text into cleaned completions.

#### Purpose
- Defines source-selection, fact-extraction, run, model, and oversized-file settings for source-summary generation.
- Provides provider selection and support for mock, local llama.cpp JNI, and OpenAI-compatible text generation.

#### Responsibilities
- **Source selection and facts:** Validate and evaluate file-condition rules, and extract configured regex match counts from source text.
- **Generation and run configuration:** Define model sampling and inference options, source scopes, output settings, prompts, facts, routing, fallback settings, and run metadata.
- **Oversized-file handling:** Parse strategies for failing, sampling, map-reduce, or deterministic output.
- **Provider lifecycle and inference:** Resolve provider choice, generate completions, expose optional timings, and support resource closure; local inference lazily loads and reuses a llama.cpp model.
- **Completion and request support:** Clean leading Gemma thinking blocks and build or interpret OpenAI-compatible chat-completion requests, without performing network requests in the shared support helper.

#### Key units
- `SrcMorphConfiguration` — central run configuration for source indexing and `.ai.md` generation.
- `AiConditionEvaluator` and `AiConditionGroup` — validate and evaluate composable file-selection conditions.
- `AiFactExtractor` — count configured regex matches across source text and format a facts block.
- `AiGenerationConfig` — hold per-model generation and provider settings with local-inference defaults.
- `AiOversizeStrategy` — map configuration tokens to oversized-file handling strategies.
- `AiGenerationProvider` and `AiGenerationProviderFactory` — define the generation contract and select a backend.
- `LlamaCppJniAiGenerationProvider` and `LlamaCppJniConfig` — configure and run local llama.cpp inference.
- `AiCompletionParser` — strip an initial Gemma thinking block and reject a missing closing marker.
- `OpenAiCompatibleProviderSupport` — prepare chat request JSON and handle endpoint, API-key, error-message, and timing support.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection rules and routing; `AiConditionEvaluator` applies conditions to file context.
- Selected source text can be passed to `AiFactExtractor` to produce configured facts for summary output.
- Generation settings and the selected oversized-file strategy guide downstream generation; `planOnly` supports producing a routing plan without model loading or generation.
- `AiGenerationProviderFactory` resolves a provider and creates it for an `AiGenerationRequest`; providers return text and may return timing data.
- The llama.cpp provider initializes its model, builds inference parameters, parses the chat response, and cleans completion content; OpenAI-compatible support builds request JSON and derives timing data from response usage and elapsed time.

#### Dependencies
- Configuration uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`; run settings reference prompt, model, field-generation, fact, and calibration definitions.
- Providers share `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`.
- Local inference uses `LlamaCppJniProviderSupport`, llama JNI types, and chat-response parsing types; OpenAI-compatible support uses Jackson, response usage types, and Java URL parsing.
- Provider selection also involves `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, and `Java8CompatibilityHelper`.

#### Cross-cutting
- Configuration types are primarily mutable JavaBeans; condition evaluation and fact extraction are stateless, and both reject invalid configuration with `IllegalArgumentException`.
- `AiGenerationProvider` extends `AutoCloseable`; default timing results contain zero timings and default closure does nothing.
- `LlamaCppJniConfig` is immutable after building, but its builder is not thread-safe; the llama.cpp provider’s cached model initialization and use are not synchronized.
- OpenAI-compatible support injects environment-variable lookup for API keys and rejects configured keys over plain HTTP except to loopback hosts.
