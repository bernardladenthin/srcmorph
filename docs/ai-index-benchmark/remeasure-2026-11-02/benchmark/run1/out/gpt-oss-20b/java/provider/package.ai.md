### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:32:50Z
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
> Provides a pluggable AI generation subsystem that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, along with utilities for parsing completions and building request payloads.

#### Purpose
- Enable AI text generation for source‑code transformations.
- Abstract provider selection and configuration.
- Clean and parse LLM responses for downstream use.

#### Responsibilities
- **Provider Factory** – resolves and instantiates the correct `AiGenerationProvider` (mock, llama.cpp JNI, or OpenAI‑compatible).
- **Provider Implementations** – generate text, optionally with timing metrics.
- **Configuration** – immutable `LlamaCppJniConfig` holds all tuning parameters for llama.cpp.
- **Parsing** – `AiCompletionParser` strips internal thinking blocks from Gemma‑4 completions.
- **Request Building & Validation** – `OpenAiCompatibleProviderSupport` constructs JSON bodies, validates URLs, secures API keys, and interprets HTTP responses.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `create`, `effectiveProvider`.
- `MockAiGenerationProvider` – stub provider for testing.
- `LlamaCppJniAiGenerationProvider` – llama.cpp JNI implementation, lazy `LlamaModel` loading, timing extraction.
- `OpenAiCompatibleAiGenerationProvider` – wrapper around `OpenAiCompatibleProviderSupport` for HTTP chat completions.
- `LlamaCppJniConfig` – immutable config record for llama.cpp.
- `AiCompletionParser` – strips thinking blocks from raw completions.
- `OpenAiCompatibleProviderSupport` – JSON payload builder, endpoint & key handling, error formatting, timing calculation.

#### Data flow
1. **Request creation** – `AiGenerationRequest` is built by the caller with prompt key, source file, and text.
2. **Provider selection** – `AiGenerationProviderFactory.create` chooses a provider based on run or model config.
3. **Generation** – Provider’s `generate` (or `generateWithTimings`) consumes the request:
   - *Mock*: returns canned text.
   - *LlamaCpp*: builds inference parameters, calls native `chatComplete`, parses JSON, extracts timings.
   - *OpenAI*: builds JSON body via `OpenAiCompatibleProviderSupport`, sends HTTP request, parses response, extracts timings.
4. **Post‑processing** – `AiCompletionParser.parseCompletion` removes thinking blocks before persisting the answer.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (native llama.cpp bindings), `com.fasterxml.jackson` (JSON), `org.jspecify` annotations, Lombok.
- **Runtime** – HTTP client for OpenAI provider, native JNI for llama.cpp.

#### Cross‑cutting
- Immutable configuration objects (`LlamaCppJniConfig`) for thread safety.
- Stateless parsing and support classes (`AiCompletionParser`, `OpenAiCompatibleProviderSupport`) for re‑use.
- Default timing handling in `AiGenerationProvider` interface.
- Error handling via `IOException` for provider failures and malformed responses.
- Optional GPU/CPU tuning and flash‑attention flags exposed through `LlamaCppJniConfig`.
