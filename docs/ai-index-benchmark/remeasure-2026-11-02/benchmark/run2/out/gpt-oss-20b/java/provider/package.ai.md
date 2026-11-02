### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:49:14Z
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
> Provides AI‑powered text generation for source‑code transformation, supporting local llama.cpp inference, OpenAI‑compatible APIs, and mock testing, with configurable prompts, timing metrics, and robust error handling.  

#### Purpose
- Deliver generated code snippets or transformations for source‑code morphing tasks.  
- Offer interchangeable back‑ends (local, remote, mock) and optional performance telemetry.  

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface defines generation contracts.  
- **Factory & configuration** – `AiGenerationProviderFactory` selects and constructs providers; `LlamaCppJniConfig` holds tuning parameters.  
- **Local inference** – `LlamaCppJniAiGenerationProvider` wraps llama.cpp JNI for on‑device generation.  
- **Remote inference** – `OpenAiCompatibleProviderSupport` builds HTTP requests, validates endpoints, secures API keys, and parses responses.  
- **Parsing** – `AiCompletionParser` extracts clean answers from raw LLM output.  
- **Mock support** – `MockAiGenerationProvider` (referenced in factory) supplies deterministic responses for testing.  

#### Key units
- `AiGenerationProvider` – interface for generating text and optional timings.  
- `AiGenerationProviderFactory` – resolves provider key and creates concrete provider.  
- `LlamaCppJniConfig` – immutable config for llama.cpp JNI provider.  
- `LlamaCppJniAiGenerationProvider` – local GGUF inference implementation.  
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible chat‑completion calls.  
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from completions.  

#### Data flow
1. **Request creation** – `AiGenerationRequest` (source file + prompt key) is passed to a provider.  
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses the provider key; `create` instantiates the provider.  
3. **Generation** – Provider’s `generate` or `generateWithTimings` is invoked.  
   - *Local*: provider builds `ModelParameters`, loads `LlamaModel`, runs `chatComplete`, parses JSON, returns text or timings.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds JSON body, endpoint, API key, sends HTTP request, parses `ChatResponse`, computes timings.  
4. **Post‑processing** – `AiCompletionParser.parseCompletion` removes any leading thinking block before persisting the answer.  

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `MockAiGenerationProvider`.  
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON serialization), `java.net`, `java.io`, `lombok`.  
- **Configuration** – `AiGenerationConfig` (default values for `LlamaCppJniConfig`).  

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` is thread‑safe; builders are single‑threaded.  
- **Error handling** – `IOException` for missing end markers, invalid URLs, or API key issues; `IllegalArgumentException` for unknown provider names.  
- **Timing support** – `generateWithTimings` returns `AiGenerationTimings` with token counts, decode rates, and optional native timings.  
- **Security** – `OpenAiCompatibleProviderSupport` enforces API key usage only over HTTPS or loopback hosts.  
- **Logging** – providers log truncation warnings, cache reuse, and native model disposal.  

---
