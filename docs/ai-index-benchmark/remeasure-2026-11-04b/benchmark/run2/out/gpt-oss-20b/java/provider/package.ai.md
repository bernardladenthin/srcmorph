### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:03:14Z
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
> A modular AI‑generation framework that abstracts multiple back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) and provides configuration, request parsing, and timing support for source‑code transformation tasks.

#### Purpose
- Expose a unified `AiGenerationProvider` API for generating text from prompts.
- Offer pluggable back‑ends with lazy model loading and detailed timing metrics.
- Centralize configuration for llama.cpp and OpenAI‑compatible providers.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and factory (`AiGenerationProviderFactory`) select the concrete implementation.
- **Configuration** – `LlamaCppJniConfig` builds immutable runtime knobs for llama.cpp.
- **Request handling** – `AiPromptSupport` (not shown) supplies prompt templates; `AiCompletionParser` cleans raw completions.
- **OpenAI compatibility** – `OpenAiCompatibleProviderSupport` builds request bodies, validates endpoints, secures API keys, and extracts usage statistics.
- **Timing & metrics** – `AiGenerationTimings` (used by providers) captures prompt/cache/prediction counts and rates.

#### Key units
- `AiGenerationProvider` – interface for text generation.
- `AiGenerationProviderFactory` – resolves provider key and constructs provider.
- `LlamaCppJniAiGenerationProvider` – local GGUF inference via llama.cpp JNI.
- `LlamaCppJniConfig` – immutable config builder for llama.cpp.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible HTTP calls.
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from completions.
- `AiPromptSupport` – (external) prompt rendering support.
- `AiGenerationTimings` – data holder for generated text and timing metrics.

#### Data flow
1. **Request creation** – client builds an `AiGenerationRequest` (prompt key, source file/text, header).
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the key; `create` returns the chosen provider.
3. **Generation** – provider’s `generate` or `generateWithTimings` is invoked:
   - For llama.cpp: lazy model load → build inference params → native chat completion → parse JSON → optional timing extraction.
   - For OpenAI‑compatible: `OpenAiCompatibleProviderSupport.requestBody` builds JSON → HTTP call → parse response → timing extraction.
4. **Post‑processing** – `AiCompletionParser` removes internal thinking blocks if present.
5. **Result** – `AiGenerationTimings` (or plain text) returned to caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationRequest`, `AiGenerationTimings`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parser), Jackson `ObjectMapper`, `org.jspecify.annotations.Nullable`.
- **Configuration** – `LlamaCppJniConfig` supplies all knobs; `AiGenerationConfig` provides defaults.

#### Cross-cutting
- **Immutability** – `LlamaCppJniConfig` is thread‑safe; builders are single‑threaded.
- **Error handling** – `IOException` for missing markers, endpoint validation, or JSON failures; `IllegalArgumentException` for unsupported provider keys.
- **Lazy initialization** – llama.cpp model is loaded on first request; not thread‑safe, so external coordination is required.
- **Logging & warnings** – providers log truncated answers and prompt‑cache reuse (implementation‑specific).
- **Environment lookup** – `OpenAiCompatibleProviderSupport` uses a `Function<String, String>` to read API keys from the environment, ensuring secure handling of credentials.
