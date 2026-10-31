### java/provider
- H: 1.0
- C: 1E5D7C55
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:11:19Z
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
> A pluggable AI generation framework that supports local llama.cpp JNI, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response API for source‑code transformation tooling.

#### Purpose
- Provide a single interface for generating AI text from prompts.
- Support multiple backends (local, cloud, mock) with configurable parameters.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface defines generation and timing APIs.
- **Factory & configuration** – `AiGenerationProviderFactory` resolves provider keys and builds instances; `LlamaCppJniConfig` holds all llama.cpp runtime options.
- **Local generation** – `LlamaCppJniAiGenerationProvider` loads a GGUF model, builds chat prompts, parses responses, and reports timings.
- **Remote generation** – `OpenAiCompatibleProviderSupport` constructs JSON requests, resolves endpoints, secures API keys, and interprets responses.
- **Utility parsing** – `AiCompletionParser` strips internal reasoning blocks from model output.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `effectiveProvider`, `create`.
- `LlamaCppJniConfig` – immutable config builder for llama.cpp.
- `LlamaCppJniAiGenerationProvider` – local JNI provider implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible APIs.
- `AiCompletionParser` – removes Gemma‑4 thinking blocks.
- `MockAiGenerationProvider` – stub provider for testing.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file/text, header) is passed to a provider.
2. **Provider selection** – `AiGenerationProviderFactory` chooses the concrete provider based on run or model config.
3. **Generation** –  
   - *Local*: provider builds chat prompt via `AiPromptSupport`, calls `LlamaModel.chatComplete`, parses JSON with `ChatResponseParser`, extracts answer with `AiCompletionParser`.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds request body, resolves endpoint, sends HTTP call, parses `ChatResponse`, extracts answer.
4. **Timing** – `generateWithTimings` wraps the raw text with `AiGenerationTimings`, including token counts and decode rates.
5. **Output** – cleaned answer string (or timings object) returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `ChatResponseParser`, `ChatResponse`, `LlamaModel`, `LlamaCppJniConfig`.
- **External** – `net.ladenthin.llama` (JNI bindings), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `lombok`, `org.jspecify`.
- **Configuration** – `AiGenerationConfig` defaults used by `LlamaCppJniConfig.builder`.

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` and support classes are immutable; builders are single‑threaded.
- **Error handling** – `IOException` for missing end markers, insecure API keys, or JSON failures; `IllegalArgumentException` for unknown provider keys.
- **Thread safety** – Providers are stateless except for the lazy‑loaded native model; `LlamaCppJniAiGenerationProvider` is not thread‑safe, while the factory and config are.
- **Timing metrics** – `AiGenerationTimings` captures token counts and decode rates; `OpenAiCompatibleProviderSupport.timings` computes these from `ChatResponse` usage data.
