### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:08:06Z
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
> A modular AI generation framework that abstracts local and OpenAI‑compatible back‑ends, providing configurable inference, request construction, and response parsing for code‑generation tasks.  

#### Purpose
- Offer pluggable AI text‑generation providers (local, mock, OpenAI‑compatible).  
- Centralize configuration, request building, and result parsing.  

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations.  
- **Factory & configuration** – `AiGenerationProviderFactory`, `LlamaCppJniConfig`.  
- **Local inference** – `LlamaCppJniAiGenerationProvider` with llama.cpp JNI integration.  
- **OpenAI‑compatible support** – `OpenAiCompatibleProviderSupport` for request/response handling.  
- **Completion parsing** – `AiCompletionParser` to strip internal reasoning blocks.  

#### Key units
- `AiGenerationProvider` (interface) – defines `generate`, `generateWithTimings`, `close`.  
- `AiGenerationProviderFactory` – resolves provider key, creates provider instances.  
- `LlamaCppJniConfig` – immutable runtime knobs for llama.cpp (model path, context size, temperature, etc.).  
- `LlamaCppJniAiGenerationProvider` – lazy‑loads `LlamaModel`, executes chat completions, returns text and metrics.  
- `OpenAiCompatibleProviderSupport` – builds JSON request, resolves endpoint, secures API key, parses timings.  
- `AiCompletionParser` – removes `THINKING_BLOCK_*` markers from raw completions.  

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt, source file, header) is passed to a provider.  
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses key; `create` instantiates the provider.  
3. **Local provider** – `LlamaCppJniAiGenerationProvider` builds inference parameters via `AiPromptSupport`, loads `LlamaModel`, runs `chatComplete`, parses JSON with `ChatResponseParser`, and returns text/metrics.  
4. **OpenAI provider** – `OpenAiCompatibleProviderSupport` serialises request body, resolves endpoint, secures API key, sends HTTP call, receives `ChatResponse`, and computes `AiGenerationTimings`.  
5. **Post‑processing** – `AiCompletionParser.parseCompletion` strips reasoning blocks before persisting the answer.  

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.  
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`.  
- **Configuration** – `LlamaCppJniConfig` (defaults from `AiGenerationConfig`).  

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable, thread‑safe.  
- **Error handling** – `IOException` for I/O or parsing errors; `IllegalArgumentException` for unknown provider keys or invalid URLs.  
- **Timing metrics** – `AiGenerationTimings` captures token counts and throughput for both local and OpenAI providers.  
- **Logging & warnings** – providers log cache reuse and truncation warnings.  
- **API key security** – `OpenAiCompatibleProviderSupport.apiKeyFor` blocks sending keys over non‑loopback HTTP.
