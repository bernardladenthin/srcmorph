### java/provider
- H: 1.0
- C: 87119406
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:54:42Z
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
> A modular AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with utilities for request construction, response parsing, and timing metrics.

#### Purpose
- Offer a pluggable AI text generation layer for source‑code transformation tools.
- Abstract provider selection, request formatting, and result cleanup.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and factory for concrete backends.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads GGUF models via JNI, handles chat completions, and extracts timings.
- **Remote API support** – `OpenAiCompatibleProviderSupport` builds chat‑completion requests, validates endpoints, secures API keys, and interprets responses.
- **Response cleanup** – `AiCompletionParser` strips internal reasoning blocks from LLM outputs.
- **Configuration** – `LlamaCppJniConfig` holds immutable settings for the llama.cpp provider.

#### Key units
- `AiGenerationProvider` (interface) – defines `generate` and `generateWithTimings`.
- `AiGenerationProviderFactory` – resolves provider keys and instantiates providers.
- `LlamaCppJniAiGenerationProvider` – local inference provider, lazy‑loads `LlamaModel`.
- `OpenAiCompatibleProviderSupport` – builds JSON bodies, validates URLs, secures keys, computes `AiGenerationTimings`.
- `AiCompletionParser` – removes Gemma‑4 style chain‑of‑thought blocks.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp options.

#### Data flow
1. **Request creation** – `AiPromptSupport` supplies a prompt; `AiGenerationRequest` bundles prompt key, source file, and header.
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a provider key; `create` returns the corresponding `AiGenerationProvider`.
3. **Generation** – Provider’s `generate` (or `generateWithTimings`) is invoked:
   - For local: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls `chatComplete`, parses JSON, extracts text via `AiCompletionParser`, and returns or wraps in `AiGenerationTimings`.
   - For remote: `OpenAiCompatibleProviderSupport` builds request body, validates endpoint, secures API key, sends HTTP request, parses `ChatResponse`, and computes timings.
4. **Result handling** – The caller receives plain text or `AiGenerationTimings`; errors propagate as `IOException`.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `LlamaCppJniProviderSupport`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON serialization), `java.net`, `java.io`, `org.jspecify.annotations`, Lombok.
- **Configuration** – `LlamaCppJniConfig` derived from `AiGenerationConfig`.

#### Cross-cutting
- **Thread safety** – Providers are stateless or immutable; local provider’s lazy model loading is not synchronized, potentially creating multiple models concurrently.
- **Error handling** – `IOException` signals missing or malformed responses, key issues, or endpoint problems; `IllegalArgumentException` for unknown provider names.
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache tokens, TPS, and predicted tokens for performance analysis.
- **Configuration immutability** – `LlamaCppJniConfig` is a record‑style immutable holder, ensuring safe concurrent reads.
