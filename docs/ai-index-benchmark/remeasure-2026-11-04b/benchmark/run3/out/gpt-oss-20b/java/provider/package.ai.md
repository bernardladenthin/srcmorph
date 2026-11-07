### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:11:37Z
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
> Provides a pluggable AI text‑generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and a mock provider, along with utilities for request construction, response parsing, and configuration.

#### Purpose
- Expose a unified `AiGenerationProvider` interface for generating text from prompts.
- Offer concrete implementations: local llama.cpp JNI, OpenAI‑compatible HTTP, and a mock stub.
- Supply configuration, parsing, and timing support for each provider.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and factory (`AiGenerationProviderFactory`).
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model, runs chat completions, and parses results.
- **Remote API** – `OpenAiCompatibleProviderSupport` builds request bodies, validates endpoints, handles API keys, and interprets responses.
- **Configuration** – `LlamaCppJniConfig` holds all runtime knobs for llama.cpp and is built via a fluent builder.
- **Parsing** – `AiCompletionParser` strips Gemma‑4 thinking blocks from raw completions.
- **Utilities** – shared helpers (`AiPromptSupport`, `Java8CompatibilityHelper`) and timing wrappers.

#### Key units
- `AiGenerationProvider` – contract for generating text and optional timing metrics.
- `AiGenerationProviderFactory` – resolves provider key and creates the appropriate provider instance.
- `LlamaCppJniAiGenerationProvider` – llama.cpp JNI implementation, lazy model loading, chat completion, and timing extraction.
- `OpenAiCompatibleProviderSupport` – constructs OpenAI‑compatible request JSON, validates URLs, retrieves API keys, and builds `AiGenerationTimings`.
- `LlamaCppJniConfig` – immutable configuration record for llama.cpp, built via `Builder`.
- `AiCompletionParser` – removes Gemma‑4 thinking blocks from raw LLM output.
- `AiGenerationTimings` – DTO holding generated text, token counts, cache usage, and throughput.

#### Data flow
1. **Factory** receives a provider key and configuration → creates a provider instance.  
2. **Client** calls `generate` or `generateWithTimings` on the provider with an `AiGenerationRequest`.  
3. **Local provider**: builds inference parameters → calls native `LlamaModel.chatComplete` → parses JSON → strips thinking block → returns text/timings.  
4. **Remote provider**: builds JSON body → validates endpoint → sends HTTP request → parses `ChatResponse` → extracts usage → constructs `AiGenerationTimings`.  
5. **Parser** (used by local provider) removes internal reasoning blocks before returning final answer.

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationRequest`, `AiGenerationTimings`.  
- **External**: `net.ladenthin:llama` JNI bindings, Jackson `ObjectMapper`, `org.jspecify.annotations.Nullable`.  
- **Configuration**: `LlamaCppJniConfig` (built from `AiGenerationConfig` defaults).

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and provider support classes are immutable; builders are single‑threaded.
- **Error handling**: `IOException` for missing end markers, HTTP errors, or insecure URLs; `IllegalArgumentException` for unsupported provider names.
- **Timing**: `generateWithTimings` wrappers compute prompt token counts, cache usage, and decode rates.
- **Environment**: API keys are read from environment variables; loopback checks enforce secure usage.
- **Parsing**: `AiCompletionParser` ensures only the final answer is returned from Gemma‑4 style completions.
