### java/provider
- H: 1.0
- C: 61221DB3
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:53:05Z
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
> A pluggable AI‑generation subsystem that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response API for source‑code transformation tooling.

#### Purpose
- Provide text generation for code‑refactoring requests.
- Offer interchangeable back‑ends (local, cloud, mock) with consistent timing metrics.

#### Responsibilities
- **Request handling** – build chat‑completion payloads and parse responses.  
- **Provider selection** – resolve the effective AI provider from configuration.  
- **Local inference** – load and cache a llama.cpp model via JNI, perform chat completions, and expose token‑level timings.  
- **Remote inference** – construct OpenAI‑compatible requests, resolve endpoints, manage API keys, and compute throughput.  
- **Response parsing** – strip internal reasoning blocks and return clean answers.

#### Key units
- `AiGenerationProvider` (interface) – contract for `generate` and `generateWithTimings`.  
- `AiGenerationProviderFactory` – creates concrete providers based on provider keys.  
- `LlamaCppJniAiGenerationProvider` – local llama.cpp implementation with lazy model loading and timing extraction.  
- `OpenAiCompatibleProviderSupport` – helper for building OpenAI‑compatible requests, endpoint resolution, API key handling, and timing calculation.  
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from raw completions.  
- `LlamaCppJniConfig` – immutable configuration holder for the llama.cpp provider.

#### Data flow
1. **Client** submits an `AiGenerationRequest` (prompt key, source text, header).  
2. **Factory** resolves the provider key → concrete `AiGenerationProvider`.  
3. **Provider** (`generate`/`generateWithTimings`) builds a chat request (via `OpenAiCompatibleProviderSupport` or internal logic).  
4. **Remote provider** sends HTTP POST, receives `ChatResponse`; local provider calls native `chatComplete`.  
5. **Response** is parsed by `AiCompletionParser` to extract the final answer.  
6. **Timings** (token counts, throughput) are wrapped in `AiGenerationTimings` and returned to the caller.

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationRequest`, `AiGenerationTimings`.  
- **External**: `net.ladenthin.llama` (model, parameters, JSON parser), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`.  
- **Configuration**: `LlamaCppJniConfig` (model path, GPU layers, cache settings, etc.).

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable and thread‑safe.  
- **Error handling** – `IOException` propagates from network or model failures; `IllegalArgumentException` signals unknown provider keys.  
- **Timing** – all providers expose `generateWithTimings`; local provider extracts native timings, remote provider computes decode rate from usage data.  
- **Resource management** – providers implement `AutoCloseable`; local provider releases the native `LlamaModel` on `close`.  
- **Logging** – providers log truncation and cache reuse events (not shown in summaries but implied by comments).
