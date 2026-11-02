### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:48:10Z
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
> A pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompt handling, timing metrics, and response parsing.

#### Purpose
- Offer a unified interface for generating text from various AI backends.
- Provide configurable prompt templates, timing diagnostics, and clean response extraction.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface defines generation contracts.
- **Provider factory** – `AiGenerationProviderFactory` resolves provider keys and instantiates concrete providers.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, runs chat completions, and parses results.
- **Remote API** – `OpenAiCompatibleProviderSupport` builds request bodies, resolves endpoints and keys, validates security, and extracts timing data.
- **Configuration** – `LlamaCppJniConfig` holds immutable settings for local inference.
- **Response parsing** – `AiCompletionParser` strips internal reasoning blocks from raw completions.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `create`, `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp chat completion, lazy model loading, timing extraction.
- `OpenAiCompatibleProviderSupport` – JSON request builder, endpoint & key resolution, timing extraction.
- `LlamaCppJniConfig` – immutable config holder for model path, context, sampling, GPU, cache, and remote‑API options.
- `AiCompletionParser` – strips `THINKING_BLOCK_START_MARKER`/`END` from LLM output.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file/text, header) is passed to a provider.
2. **Provider selection** – `AiGenerationProviderFactory` resolves the provider key (run‑level or model‑level) and returns an instance.
3. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` builds chat template kwargs, lazily loads `LlamaModel`, calls `chatComplete`, parses JSON via `ChatResponseParser`, extracts text and timing, then passes through `AiCompletionParser`.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds JSON body, resolves endpoint and API key, sends HTTP request, receives `ChatResponse`, extracts text and timing, then passes through `AiCompletionParser`.
4. **Output** – `AiGenerationTimings` (text, token counts, throughput) is returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `ChatResponseParser`, `ChatResponse`, `Usage`.
- **External** – `net.ladenthin.llama` JNI bindings, `com.fasterxml.jackson` for JSON, `java.net.URL`, `java.io.IOException`, `lombok` annotations.
- **Configuration** – `LlamaCppJniConfig` (model path, GPU layers, cache, remote‑API URLs, API key env var).

#### Cross-cutting
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable; thread‑safe reads.
- **Error handling** – `IOException` for missing/invalid config, unclosed reasoning blocks, or HTTP key misuse.
- **Timing metrics** – `AiGenerationTimings` captures generated text, token counts, and decode throughput; providers expose `generateWithTimings`.
- **Prompt templating** – `AiPromptSupport` supplies system/user messages; providers delegate template construction to it.
- **Logging & warnings** – local provider logs truncation warnings and cache reuse; remote provider validates loopback key usage.

---
