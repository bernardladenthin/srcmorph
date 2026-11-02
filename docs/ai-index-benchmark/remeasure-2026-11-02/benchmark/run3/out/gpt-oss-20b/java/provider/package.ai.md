### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:03:21Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> A pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response API with timing metrics and robust request construction.

#### Purpose
- Provide a single interface for generating text from LLMs.
- Support multiple back‑ends (local, cloud, mock) with configurable parameters.
- Offer timing and token‑usage metrics for performance analysis.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` defines the contract for generating text.
- **Factory & configuration** – `AiGenerationProviderFactory` resolves provider keys and builds `AiGenerationProvider` instances; `LlamaCppJniConfig` holds all runtime knobs for the llama.cpp provider.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, runs chat completions, parses JSON, and reports timings.
- **Cloud compatibility** – `OpenAiCompatibleProviderSupport` builds HTTP payloads, validates endpoints, secures API keys, parses responses, and extracts metrics.
- **Utility parsing** – `AiCompletionParser` strips internal reasoning blocks from raw completions.

#### Key units
- `AiGenerationProvider` – interface for generating text and optional timings.
- `AiGenerationProviderFactory` – resolves provider key → concrete provider.
- `LlamaCppJniConfig` – immutable config for llama.cpp provider.
- `LlamaCppJniAiGenerationProvider` – local GGUF inference implementation.
- `OpenAiCompatibleProviderSupport` – HTTP request/response handling for OpenAI‑compatible APIs.
- `AiCompletionParser` – cleans raw LLM output.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file/text, header) is passed to a provider.
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses the key; `create` returns the matching provider.
3. **Generation** – Provider’s `generate` or `generateWithTimings` is invoked.
   - *Local*: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, builds inference parameters, calls `chatComplete`, parses `ChatResponse`, extracts text and timings.
   - *Cloud*: `OpenAiCompatibleProviderSupport` builds JSON body, endpoint, API key, sends HTTP request, parses `ChatResponse`, computes timings.
4. **Post‑processing** – `AiCompletionParser.parseCompletion` removes thinking blocks from the raw completion before persisting.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`.
- **Configuration** – `LlamaCppJniConfig` (37 knobs), `AiGenerationConfig` defaults.

#### Cross‑cutting
- **Thread safety** – Providers are stateless or lazily initialized; factories are thread‑safe. No explicit synchronization in `LlamaCppJniAiGenerationProvider` (single‑threaded assumption).
- **Error handling** – `IOException` for I/O or missing reasoning blocks; `IllegalArgumentException` for unknown provider keys; `IOException` for API key misuse.
- **Immutability** – `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are immutable; builders are single‑threaded.
- **Logging** – Providers log cache reuse, truncation warnings, and timing metrics (not shown in summaries but implied by comments).
