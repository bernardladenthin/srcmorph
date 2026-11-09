### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:41:38Z
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
> Provides a pluggable AI generation engine for source‑code transformation, supporting local llama.cpp models, OpenAI‑compatible APIs, and a mock provider, with utilities for request construction, response parsing, and timing metrics.  

#### Purpose
- Enable AI‑driven text generation for source‑morphing workflows.  
- Offer multiple backend strategies (local, cloud‑compatible, mock).  

#### Responsibilities
- **Provider selection** – `AiGenerationProviderFactory` resolves provider keys and instantiates the appropriate `AiGenerationProvider`.  
- **Request handling** – `OpenAiCompatibleProviderSupport` builds JSON payloads, validates endpoints, and secures API keys for OpenAI‑compatible services.  
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, executes chat completions, and parses responses.  
- **Response cleaning** – `AiCompletionParser` strips internal “thinking” blocks from raw LLM outputs.  
- **Configuration** – `LlamaCppJniConfig` encapsulates all llama.cpp tuning knobs in an immutable record‑style object.  

#### Key units
- **AiGenerationProvider** – interface defining `generate`, `generateWithTimings`, and `close`.  
- **AiGenerationProviderFactory** – creates providers based on a key; exposes `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.  
- **LlamaCppJniAiGenerationProvider** – local provider that lazily loads `LlamaModel`, runs chat completions, and returns text or `AiGenerationTimings`.  
- **OpenAiCompatibleProviderSupport** – helper for building request bodies, normalising URLs, and handling API keys for OpenAI‑compatible endpoints.  
- **AiCompletionParser** – removes Gemma‑style “thinking” blocks from completions.  
- **LlamaCppJniConfig** – immutable configuration holder with a fluent builder for 37 llama.cpp parameters.  

#### Data flow
1. **Factory** receives a provider key → resolves to a concrete provider.  
2. **Provider** receives an `AiGenerationRequest` (prompt key, source file, text, header).  
3. For OpenAI‑compatible providers:  
   - `OpenAiCompatibleProviderSupport.requestBody` builds JSON.  
   - HTTP client (not shown) sends request → receives JSON response.  
   - `AiCompletionParser` cleans the response.  
4. For local llama.cpp provider:  
   - `LlamaCppJniAiGenerationProvider.chatResponse` invokes native `chatComplete`.  
   - `AiCompletionParser` extracts the answer.  
5. Provider returns plain text or `AiGenerationTimings` (including token counts and rates).  

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`, `AiGenerationRequest`.  
- **External**: `net.ladenthin.llama` (model, args, JSON parser), Jackson `ObjectMapper`, `org.jspecify.annotations.Nullable`.  
- **Configuration**: `LlamaCppJniConfig` supplies all tuning knobs.  

#### Cross‑cutting
- **Immutability**: `LlamaCppJniConfig` and provider support classes are immutable, ensuring thread safety.  
- **Error handling**: `IOException` is the primary failure mode for missing markers, truncated responses, or insecure HTTP usage.  
- **Timing metrics**: `generateWithTimings` is a default implementation that wraps `generate` and supplies zeroed metrics unless overridden.  
- **Logging & warnings**: Local provider logs truncation and prompt‑cache reuse via `support` utilities (not detailed in summaries).  

This package equips a source‑morphing system with flexible AI backends, robust request/response handling, and comprehensive configuration for both local and cloud‑based inference.
