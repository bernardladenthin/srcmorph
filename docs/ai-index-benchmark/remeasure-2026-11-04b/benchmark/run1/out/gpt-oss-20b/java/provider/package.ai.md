### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:55:01Z
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
> A modular AI generation framework that abstracts multiple back‑ends (local llama.cpp, OpenAI‑compatible APIs, and a mock provider) and supplies utilities for request construction, response parsing, and timing metrics.

#### Purpose
- Provide a unified interface for generating text from LLMs.
- Support local and remote providers with configurable runtime knobs.
- Strip internal reasoning from responses and expose clean answers.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Factory orchestration** – `AiGenerationProviderFactory` resolves provider keys and constructs the appropriate provider.
- **Configuration** – `LlamaCppJniConfig` holds 37 tunable parameters for llama.cpp execution.
- **Request/response handling** – `OpenAiCompatibleProviderSupport` builds JSON payloads, resolves endpoints, secures API keys, and parses chat responses.
- **Response cleaning** – `AiCompletionParser` removes Gemma‑4 thinking blocks from raw completions.
- **Timing extraction** – providers expose `AiGenerationTimings` with prompt/cache tokens and throughput metrics.

#### Key units
- `AiGenerationProvider` – contract for `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – resolves provider key, creates provider instance.
- `LlamaCppJniConfig` – immutable config builder for llama.cpp JNI.
- `LlamaCppJniAiGenerationProvider` – lazy‑load llama.cpp model, parse chat, return timings.
- `OpenAiCompatibleProviderSupport` – builds request body, endpoint, API key, parses `ChatResponse`, computes `AiGenerationTimings`.
- `AiCompletionParser` – strips Gemma‑4 thinking block, validates markers.
- `AiGenerationTimings` – DTO for generated text and performance metrics.

#### Data flow
1. **Factory** receives provider key, config, and prompt support → creates provider.
2. **Client** calls `generate`/`generateWithTimings` with `AiGenerationRequest`.
3. **Provider** builds request (via `OpenAiCompatibleProviderSupport` or llama.cpp chat template), sends to backend.
4. **Backend** returns chat JSON → provider parses with `ChatResponseParser`.
5. **Provider** extracts text, optionally parses timings, and returns `AiGenerationTimings`.
6. **Parser** (`AiCompletionParser`) cleans raw completion before persistence.

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `LlamaCppJniProviderSupport`, `ChatResponseParser`.
- **External**: `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`.
- **Configuration**: `LlamaCppJniConfig` (model path, threads, GPU layers, cache settings, OpenAI base URL, API key env).

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and support classes are immutable; builders are single‑threaded.
- **Error handling**: `AiCompletionParser` throws `IOException` for malformed responses; provider factory throws `IllegalArgumentException` for unknown keys; `OpenAiCompatibleProviderSupport` throws `IOException` for insecure HTTP or missing keys.
- **Timing metrics**: All providers expose `AiGenerationTimings`; local provider parses native `Timings`, remote provider derives from `ChatResponse.usage`.
- **Configuration lookup**: `Java8CompatibilityHelper.isBlank` used for string validation across the package.
- **Thread safety**: Providers are stateless except for lazy model initialization; no explicit synchronization, but designed for single‑threaded or externally synchronized use.
