### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:28:30Z
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
> Provides a pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock back‑ends, exposing a unified request/response contract and configurable prompt handling.

#### Purpose
- Central AI text generation service with interchangeable back‑ends.
- Unified request/response model (`AiGenerationRequest` → `String`/`AiGenerationTimings`).

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface defines generation contract.
- **Factory logic** – `AiGenerationProviderFactory` resolves provider keys and instantiates concrete providers.
- **Local inference** – `LlamaCppJniAiGenerationProvider` wraps llama.cpp JNI, handles model loading, prompt reuse, and timing extraction.
- **Remote API support** – `OpenAiCompatibleProviderSupport` builds chat‑completion requests, validates endpoints, secures API keys, and parses responses.
- **Configuration** – `LlamaCppJniConfig` holds immutable runtime knobs for llama.cpp.
- **Output cleaning** – `AiCompletionParser` strips internal reasoning blocks from raw completions.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `create`, `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp provider, lazy model load, timing extraction.
- `OpenAiCompatibleProviderSupport` – request/endpoint/key handling, success/error logic, timing calculation.
- `LlamaCppJniConfig` – immutable config record with builder.
- `AiCompletionParser` – removes Gemma‑4 style thinking blocks.

#### Data flow
1. **Request**: `AiGenerationRequest` (source file + prompt key) is passed to a provider via `generate`/`generateWithTimings`.
2. **Provider selection**: `AiGenerationProviderFactory` resolves provider key → concrete provider.
3. **Local provider**: `LlamaCppJniAiGenerationProvider` builds a chat request using `AiPromptSupport`, calls native `LlamaModel.chatComplete`, parses `ChatResponse`, extracts text and timings.
4. **Remote provider**: `OpenAiCompatibleProviderSupport` constructs JSON body, resolves endpoint, fetches API key, sends HTTP request, parses JSON into `ChatResponse`, computes timings.
5. **Output**: Raw completion text is optionally cleaned by `AiCompletionParser` before being returned to the caller.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`, `@EqualsAndHashCode`).

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and provider support classes are immutable, thread‑safe.
- **Error handling**: Providers throw `IOException` for I/O or configuration errors; `OpenAiCompatibleProviderSupport` validates URLs and API keys.
- **Timing metrics**: `AiGenerationTimings` captures text, prompt/cache stats, and throughput; default zeroed values for unsupported providers.
- **Configuration via builder**: `LlamaCppJniConfig.Builder` offers fluent API for all llama.cpp knobs.
- **Stateless interfaces**: `AiGenerationProvider` is stateless; implementations may be stateful (e.g., model cache).
- **Concurrency**: Factory is thread‑safe; local provider’s lazy model load is not synchronized, potentially loading multiple times concurrently. Remote provider operations are stateless and thread‑safe.
