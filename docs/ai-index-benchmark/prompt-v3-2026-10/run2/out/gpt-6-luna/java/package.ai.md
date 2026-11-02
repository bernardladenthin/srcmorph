### java
- H: 1.0
- C: 0361907E
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:13:33Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects source material and generates AI-assisted documentation through configurable local or OpenAI-compatible inference backends.

#### Purpose
- Configure source selection, metadata conditions, exact source facts, prompt and model settings, and oversized-file policies.
- Route generation requests to mock, llama.cpp JNI, or OpenAI-compatible providers and process completion results.

#### Responsibilities
- **Source and generation configuration (`config/`):** Defines Maven-bindable settings, condition evaluation, generation parameters, fact extraction, and oversize strategy parsing.
- **Provider routing and inference (`provider/`):** Resolves provider selection, prepares and runs inference requests, reports optional timing metrics, and handles provider lifecycle.
- **Completion processing:** Removes a leading Gemma-style thinking block and rejects an unterminated block.

#### Key units
- `SrcMorphConfiguration` — central configuration for source scope, output, prompts, models, routing, and metadata.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and apply metadata conditions, including grouped rules.
- `AiFactExtractor` — counts configured regex matches in source text and formats a facts block.
- `AiGenerationConfig` — carries per-generation provider, model, inference, and prompt-budget settings.
- `AiOversizeStrategy` — parses configured handling policies for oversized files.
- `AiGenerationProvider` — backend contract for generation, timing results, and resource closing.
- `AiGenerationProviderFactory` — selects and constructs a backend from model- or run-level provider settings.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — perform local inference using configured model, sampling, hardware, cache, and stop settings.
- `OpenAiCompatibleProviderSupport` — prepares chat-completions request data and handles endpoint, API-key, response, and timing concerns.
- `AiCompletionParser` — extracts answer text after an initial thinking block.

#### Data flow
- `SrcMorphConfiguration` supplies source filters and routing definitions; `AiConditionEvaluator` applies conditions to file metadata.
- `AiFactExtractor` converts configured counters and source text into exact whole-file facts.
- `AiGenerationProviderFactory` gives a nonblank model provider key precedence over the run-level key; absent keys select the mock backend.
- Providers consume `AiGenerationRequest`; local inference uses `AiPromptSupport` and `LlamaCppJniProviderSupport`, while OpenAI-compatible support builds chat-completions data.
- Completion parsing removes a leading thinking block; provider timing results may accompany generated output.

#### Dependencies
- Shared domain types include `AiFileContext`, `AiCondition`, `AiFactCounter`, `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Path-glob condition matching delegates to `AiSourceExcludeFilter`.
- Local inference relies on llama.cpp JNI types and `LlamaCppJniProviderSupport`; remote request support uses Jackson and chat-response usage data.
- Configuration and provider utilities include `AiGenerationConfig` and `Java8CompatibilityHelper`.

#### Cross-cutting
- Configuration holders are generally mutable and do not synchronize; `LlamaCppJniConfig` is immutable after construction.
- Validation is explicit in `AiConditionEvaluator` and `AiFactExtractor`; provider operations may report `IOException`, while unsupported provider keys raise `IllegalArgumentException`.
- The llama.cpp provider caches a native model with unsynchronized initialization and access; `close()` closes the current model without clearing the cached field.
- OpenAI-compatible support rejects non-HTTP(S) endpoints and restricts API-key transmission over cleartext HTTP to recognized loopback hosts.
