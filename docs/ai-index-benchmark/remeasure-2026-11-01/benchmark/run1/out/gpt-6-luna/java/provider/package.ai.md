### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:23:17Z
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
> Generates text for document-processing workflows through configurable local or OpenAI-compatible AI backends, with completion cleanup and optional inference metrics.

#### Purpose
- Defines the provider contract and selects a generation backend from run- and model-level configuration.
- Supports mock generation, llama.cpp JNI inference, and OpenAI-compatible request preparation.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProvider` defines generation, optional timing results, and lifecycle closure; `AiGenerationProviderFactory` resolves provider precedence and instantiates the selected backend.
- **Local inference:** `LlamaCppJniAiGenerationProvider` configures and lazily reuses a llama.cpp model, generates chat completions, and exposes timing and prompt-cache metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` constructs chat-completion JSON, resolves endpoints and API keys, validates cleartext credential destinations, and converts responses into status, error, and timing data.
- **Configuration and completion cleanup:** `LlamaCppJniConfig` holds model, sampling, hardware, caching, provider, reasoning, and stop-string options; `AiCompletionParser` removes a leading Gemma-compatible reasoning block.

#### Key units
- `AiGenerationProvider` — backend interface for generating text and optionally reporting `AiGenerationTimings`.
- `AiGenerationProviderFactory` — chooses `mock`, `llamacpp-jni`, or `openai-compatible`, defaulting to mock when no provider is configured.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp chat completion using a lazily initialized `LlamaModel`.
- `OpenAiCompatibleProviderSupport` — prepares and validates request, endpoint, credential, error, and timing data for OpenAI-compatible calls.
- `LlamaCppJniConfig` — immutable configuration value with a chained `Builder` for model and generation settings.
- `AiCompletionParser` — strips a leading thinking block and returns trimmed completion text.

#### Data flow
- The factory selects a provider using model-level configuration when nonblank, otherwise run-level configuration; a missing selection uses mock generation.
- A backend receives `AiGenerationRequest`; the local provider builds inference parameters from `AiPromptSupport`, calls `chatComplete`, and parses completion text.
- OpenAI-compatible support turns request and configuration values into a non-streaming chat-completions body and endpoint, then supplies response status/error and timing conversions.
- `AiCompletionParser` removes a leading reasoning block before the answer is returned; an unclosed leading block raises `IOException`.

#### Dependencies
- Shared domain types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Provider integrations: `LlamaModel` and llama.cpp JNI model/configuration types; `OpenAiCompatibleAiGenerationProvider` and `MockAiGenerationProvider` are selected by the factory.
- Response and serialization support: `ChatResponse`, `ChatResponseParser`, `Timings`, `Usage`, and Jackson JSON nodes.
- Utilities include `Java8CompatibilityHelper`, Java collections/network APIs, Lombok annotations, and JSpecify nullability annotations.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable; its builder is intended for single-threaded use, and list accessors expose unmodifiable views.
- The llama.cpp provider lazily initializes and reuses its model without synchronization; concurrent first calls may race.
- Provider methods declare `IOException` for generation failures; the provider interface supplies zero timings when an implementation does not report them.
- OpenAI-compatible API keys are rejected for non-loopback plain-HTTP destinations; endpoint URLs must use HTTP or HTTPS.
