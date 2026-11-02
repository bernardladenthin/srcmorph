### java/provider
- H: 1.0
- C: 748BAA0F
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:55:35Z
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
> A package that supplies AI‑generation backends, parses model responses, and manages configuration for local (llama.cpp) and OpenAI‑compatible providers.

#### Purpose
- Provide pluggable AI text generation services.
- Offer utilities for parsing and cleaning LLM outputs.
- Expose immutable configuration for local llama.cpp models.

#### Responsibilities
- **Provider Factory** – resolves and creates `AiGenerationProvider` instances (`mock`, `llamacpp-jni`, `openai-compatible`).
- **Local Generation** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, runs chat completions, and extracts timing metrics.
- **OpenAI‑compatible Support** – `OpenAiCompatibleProviderSupport` builds request bodies, resolves endpoints, handles API keys, and formats errors.
- **Response Parsing** – `AiCompletionParser` strips Gemma‑4 thinking blocks from raw completions.
- **Configuration** – `LlamaCppJniConfig` holds all llama.cpp runtime options in an immutable record‑style object.

#### Key units
- `AiGenerationProvider` (interface) – abstract generation contract.
- `AiGenerationProviderFactory` – creates providers based on keys.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI provider.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible APIs.
- `AiCompletionParser` – cleans Gemma‑4 responses.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp.

#### Data flow
1. **Request**: `AiGenerationRequest` (prompt key, source file, text, header) is passed to a provider via `generate` or `generateWithTimings`.
2. **Provider Selection**: `AiGenerationProviderFactory.effectiveProvider` resolves the key; `create` instantiates the appropriate provider.
3. **Generation**:
   - *Local*: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, builds inference parameters, calls `chatComplete`, parses JSON with `ChatResponseParser`, extracts text via `AiCompletionParser`, and returns `AiGenerationTimings`.
   - *OpenAI‑compatible*: `OpenAiCompatibleProviderSupport` builds the JSON body, resolves endpoint, fetches API key, sends HTTP request, parses response, and computes timings.
4. **Output**: `AiGenerationTimings` (text + metrics) or plain `String` is returned to the caller.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (model, args, JSON parser), `com.fasterxml.jackson` (JSON), `org.jspecify` (nullable annotations), `lombok` (annotations).
- **Runtime**: JNI for llama.cpp, HTTP client for OpenAI‑compatible calls.

#### Cross‑cutting
- Immutable configuration (`LlamaCppJniConfig`) ensures thread safety after construction.
- `AiCompletionParser` is stateless and thread‑safe.
- Providers expose `close()` for resource cleanup; local provider releases native model.
- Error handling: `IOException` for missing end markers, provider failures, or insecure API key usage.
- Timing metrics are consistently returned via `AiGenerationTimings` across providers.
