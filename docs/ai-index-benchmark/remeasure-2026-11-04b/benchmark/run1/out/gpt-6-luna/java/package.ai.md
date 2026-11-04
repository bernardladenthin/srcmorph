### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:50:19Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-code indexing and generates AI summaries through local or OpenAI-compatible models.

#### Purpose
- Define which source files are included, what facts and prompts feed summaries, and how generation and oversized files are handled.
- Route completion requests to mock, llama.cpp JNI, or OpenAI-compatible backends.

#### Responsibilities
- **Indexing configuration:** Store source-selection rules, prompts, model and field routing, fact definitions, output metadata, and fallback settings.
- **Selection and facts:** Validate and evaluate file-condition trees against file context; count configured regex matches in source text.
- **Generation settings:** Configure per-model sampling, runtime, provider, token budgets, reasoning, and stop conditions; represent oversized-file strategies.
- **Provider construction and inference:** Resolve provider overrides and create the selected backend; generate completions locally or through OpenAI-compatible support.
- **Response and timing support:** Clean completion text, report inference timings where available, and handle provider-specific response details.

#### Key units
- `SrcMorphConfiguration` — top-level source-indexing, routing, prompt, facts, fallback, and output configuration.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate nested source-file selection rules.
- `AiFactExtractor` — validate fact-counter definitions and format regex match counts from source text.
- `AiGenerationConfig` / `AiOversizeStrategy` — hold generation options and define oversized-source handling modes.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define the backend contract and select a provider from model- or run-level settings.
- `LlamaCppJniConfig` / `LlamaCppJniAiGenerationProvider` — configure local inference and generate completions using llama.cpp JNI.
- `OpenAiCompatibleProviderSupport` — build chat-completion requests, enforce transport safety for keyed requests, and derive timings.
- `AiCompletionParser` — trim completion text and remove a leading Gemma-4 thinking block.

#### Data flow
- `SrcMorphConfiguration` supplies source filters, prompts, routing, facts, and generation settings to the indexing workflow.
- `AiConditionEvaluator` matches configured rules against `AiFileContext`; `AiFactExtractor` turns source text into a formatted facts block.
- `AiGenerationRequest` and generation configuration flow through `AiGenerationProviderFactory` to the selected backend.
- The local backend invokes `LlamaModel.chatComplete` and returns parsed completion text or timing data; OpenAI-compatible support serializes chat requests and derives timings from response usage and elapsed time.

#### Dependencies
- Internal configuration types include `AiCondition`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiFactCounter`, and `AiCalibration`.
- Provider support uses `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, and `LlamaCppJniProviderSupport`.
- External integrations include llama.cpp JNI, Jackson JSON, Java regex/time/I/O/URL APIs, Lombok, and JSpecify.

#### Cross-cutting
- Configuration is primarily mutable JavaBeans; `LlamaCppJniConfig` is immutable, and its builder is not thread-safe.
- Defaults cover generation, provider routing, output location, llama.cpp fallback, and oversized-source handling (`FAIL`).
- Condition trees and fact definitions have explicit validation; most configuration setters do not.
- The llama.cpp provider lazily caches its model without synchronization; generation and `close()` are not coordinated.
- Provider timing defaults are zero-valued, and the base `close()` is a no-op; keyed plain-HTTP requests are limited to recognized loopback hosts.
