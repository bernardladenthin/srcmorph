### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:38:39Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-summary generation and routes AI completion requests to local, remote, or mock providers.

#### Purpose
- Defines source-selection, generation, and oversized-file settings used to prepare `.ai.md` summaries.
- Provides configurable AI text-generation backends and shared completion handling.

#### Responsibilities
- **Source and generation configuration:** `SrcMorphConfiguration` and `AiGenerationConfig` hold discovery, routing, model, inference, provider, and output settings.
- **File rules and facts:** `AiConditionEvaluator` evaluates source-selection conditions; `AiFactExtractor` validates and formats regex-based source facts.
- **Oversized-file policy:** `AiOversizeStrategy` parses configured handling modes: `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC`.
- **Provider dispatch and inference:** `AiGenerationProviderFactory` selects mock, llama.cpp JNI, or OpenAI-compatible generation; provider implementations handle local or remote completion requests.
- **Completion and metrics:** `AiCompletionParser` cleans completion text, while `AiGenerationTimings` represents available timing and token-count data.

#### Key units
- `SrcMorphConfiguration` — mutable run-level source-discovery, output, routing, and fallback settings.
- `AiGenerationConfig` — mutable per-model inference and provider options with defaults.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate file-condition trees and their grouped children.
- `AiFactExtractor` / `AiOversizeStrategy` — extract configured source facts and resolve oversized-file policy.
- `AiGenerationProvider` — provider contract for generation, timing, and cleanup.
- `AiGenerationProviderFactory` — resolves provider settings and constructs the selected backend.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — run local native inference using built model settings.
- `OpenAiCompatibleProviderSupport` — builds remote chat-completion requests and handles endpoint, key, HTTP-error, and timing support.
- `AiCompletionParser` — removes a leading Gemma-4 thinking block and trims the answer.

#### Data flow
- Source contexts and condition trees are validated and evaluated; configured counters and source text can be converted into a formatted facts block.
- Run- and model-level settings feed provider selection; the chosen provider consumes an `AiGenerationRequest` and produces completion text with optional timing data.
- Local generation uses llama.cpp support and a cached model; remote generation builds a chat-completion request from prompt messages.
- Completion text is cleaned before returning; oversized-file configuration tokens map to a strategy, with blank or null values defaulting to `FAIL`.

#### Dependencies
- Configuration and selection: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, and prompt/model/field-generation definitions.
- Provider contracts and support: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, mock and OpenAI-compatible providers, and `Java8CompatibilityHelper`.
- External libraries: llama.cpp JNI types, Jackson JSON nodes and `ObjectMapper`, and response `ChatResponse` / `Usage` data.

#### Cross-cutting
- Configuration beans are mutable and unsynchronized; `LlamaCppJniConfig` is immutable after construction, with a mutable, non-thread-safe builder.
- Provider generation declares `IOException`; the provider interface supplies default zero-valued timings and a no-op `close()`.
- Local model initialization is lazy and unsynchronized, so concurrent first calls may race.
- OpenAI-compatible support restricts API-key use to HTTPS or loopback HTTP endpoints.
