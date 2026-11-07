### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:56:31Z
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
> Provides a pluggable AI generation framework that supports local llama.cpp inference, mock generation, and OpenAI‑compatible HTTP backends, exposing a unified request/response API and timing metrics.

#### Purpose
- Centralize AI text generation across multiple backends.
- Offer a consistent request/response contract with optional timing data.
- Enable configuration‑driven provider selection and runtime tuning.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` defines the contract for generating text.
- **Factory logic** – `AiGenerationProviderFactory` resolves and instantiates the correct provider implementation.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via llama.cpp, handles chat templates, and parses completions.
- **Remote inference** – `OpenAiCompatibleProviderSupport` builds HTTP requests, validates endpoints, secures API keys, and interprets responses.
- **Configuration** – `LlamaCppJniConfig` holds 37 llama.cpp knobs and offers a fluent builder.
- **Parsing utilities** – `AiCompletionParser` strips Gemma‑4 thinking blocks from raw completions.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `effectiveProvider`, `create`.
- `LlamaCppJniAiGenerationProvider` – lazy model loading, chat completion, timing extraction.
- `OpenAiCompatibleProviderSupport` – request body construction, endpoint validation, API key handling, error formatting, timing calculation.
- `LlamaCppJniConfig.Builder` – fluent builder for 37 llama.cpp parameters.
- `AiCompletionParser` – removes Gemma‑4 thinking block.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file/text, header) is passed to a provider.
2. **Provider selection** – `AiGenerationProviderFactory` resolves the provider key (run or model config) and creates the appropriate provider instance.
3. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls `model.chatComplete`, parses JSON, extracts text, and optionally timing metrics.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds a JSON body, validates the endpoint, secures the API key, sends the HTTP request, parses the response, and extracts timing data.
4. **Post‑processing** – `AiCompletionParser` removes any internal thinking block from the raw completion before returning the final answer.

#### Dependencies
- **Internal** – `AiPromptSupport` for prompt lookup, `Java8CompatibilityHelper` for string checks, `AiCompletionParser`, `ChatResponseParser`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson.databind` (JSON serialization), `org.jspecify.annotations.Nullable`, `lombok` annotations.
- **Configuration** – `LlamaCppJniConfig` supplies all runtime knobs; `AiGenerationConfig.DEFAULT_*` values are used in the builder.

#### Cross‑cutting
- **Thread safety** – Providers are stateless except for the lazy `LlamaModel`; no explicit synchronization, potential race on first `generate` call.
- **Error handling** – `IOException` is the primary failure mode for missing markers, HTTP errors, or JSON issues; `IllegalArgumentException` signals unknown provider keys.
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache tokens, throughput, and predicted token counts for both local and remote providers.
- **Configuration immutability** – `LlamaCppJniConfig` is immutable; builder is single‑threaded.
- **Environment integration** – API keys are fetched from environment variables via a supplied lookup function; insecure HTTP to non‑loopback hosts is rejected.
