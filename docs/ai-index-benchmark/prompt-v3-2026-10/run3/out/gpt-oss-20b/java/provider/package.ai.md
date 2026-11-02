### java/provider
- H: 1.0
- C: 9D08F4C5
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:46:09Z
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
> A modular AI‑generation framework that supplies pluggable back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) for source‑code transformation tasks, exposing a clean request/response API and handling configuration, timing, and response parsing.

#### Purpose
- Provide a unified interface for generating text from AI models.
- Support multiple back‑ends with a common factory and configuration system.
- Offer timing diagnostics and response cleaning for downstream processing.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` defines the contract for generating text and optional timing data.
- **Factory & configuration** – `AiGenerationProviderFactory` resolves provider keys and constructs concrete providers; `LlamaCppJniConfig` holds immutable runtime knobs for the llama.cpp JNI provider.
- **Provider implementations** – `LlamaCppJniAiGenerationProvider` implements local llama.cpp generation; `OpenAiCompatibleProviderSupport` builds and validates OpenAI‑compatible requests.
- **Response handling** – `AiCompletionParser` strips internal reasoning blocks from LLM outputs.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `effectiveProvider`, `create`.
- `LlamaCppJniConfig` – immutable record of llama.cpp settings.
- `LlamaCppJniAiGenerationProvider` – lazy model loading, chat completion, timing extraction.
- `OpenAiCompatibleProviderSupport` – JSON request construction, endpoint resolution, API key validation, timing calculation.
- `AiCompletionParser` – cleans raw LLM completions.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (source file, prompt key) is passed to a provider.
2. **Provider selection** – `AiGenerationProviderFactory` resolves the provider key (run or model config) and instantiates the appropriate provider.
3. **Generation** – Provider builds the request (via `OpenAiCompatibleProviderSupport` or llama.cpp chat API), sends it, receives a JSON response.
4. **Parsing & timing** – Provider extracts the completion text and optional `Timings`; `AiCompletionParser` removes any Gemma‑4 thinking block.
5. **Result delivery** – `AiGenerationTimings` (text + metrics) or plain `String` is returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parser), `com.fasterxml.jackson` (JSON), `org.jspecify` (nullable annotations), `lombok` (annotations).
- **Runtime** – llama.cpp native library, OpenAI‑compatible HTTP endpoint, environment variables for API keys.

#### Cross-cutting
- **Immutability** – `LlamaCppJniConfig` is a record‑style immutable holder; providers expose read‑only state.
- **Error handling** – `IOException` is the primary checked exception for I/O or configuration errors; `IllegalArgumentException` signals unknown provider keys.
- **Thread safety** – Providers are stateless or lazily initialized; the factory is thread‑safe. `LlamaCppJniAiGenerationProvider` is intended for single‑threaded use due to unsynchronized lazy model loading.
- **Configuration patterns** – Builder pattern for `LlamaCppJniConfig`; factory method for provider creation; helper class (`OpenAiCompatibleProviderSupport`) encapsulates request/endpoint logic.
