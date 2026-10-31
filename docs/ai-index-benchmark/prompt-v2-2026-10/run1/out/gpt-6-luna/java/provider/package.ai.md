### java/provider
- H: 1.0
- C: A064EE97
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:33:50Z
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
> Selects and runs AI text-generation backends for source-aware requests, with configurable local or OpenAI-compatible inference and cleaned completion output.

#### Purpose
- Defines a pluggable generation contract and selects a provider for each run, defaulting to mock generation when no provider is configured.
- Supplies llama.cpp JNI model configuration and generation support, plus utilities for OpenAI-compatible request construction and completion cleanup.

#### Responsibilities
- **Provider selection and contract:** Resolve model-level versus run-level provider settings; create mock, llama.cpp JNI, or OpenAI-compatible providers; expose generation, optional timing data, and resource closing.
- **Local inference:** Lazily load and reuse a llama.cpp model, build inference parameters from configuration and requests, parse chat responses, and report token and throughput metrics.
- **Configuration:** Provide immutable, builder-created `LlamaCppJniConfig` settings for sampling, hardware placement, batching, caches, reasoning, and stop behavior.
- **Compatible API support:** Construct chat-completion JSON, normalize endpoints, resolve and validate API-key transport, format HTTP errors, and derive generation timings.
- **Completion cleanup:** Strip a leading Gemma-4 reasoning block while preserving marker text elsewhere in the answer.

#### Key units
- `AiGenerationProvider` — backend contract for generating text, optionally with timings, and closing resources.
- `AiGenerationProviderFactory` — resolves provider precedence and dispatches to mock, llama.cpp JNI, or OpenAI-compatible implementations.
- `LlamaCppJniAiGenerationProvider` — performs local model inference and returns parsed completions with optional timing metrics.
- `LlamaCppJniConfig` — immutable generation and model configuration with a fluent `Builder`.
- `OpenAiCompatibleProviderSupport` — package-private utilities for compatible chat-completion requests, endpoint and key validation, and response metrics.
- `AiCompletionParser` — removes a leading Gemma-4 thinking block from completion text.

#### Data flow
- Provider keys and model settings flow through `AiGenerationProviderFactory`; absent or blank keys select `MockAiGenerationProvider`, while recognized keys select their corresponding backend.
- For llama.cpp JNI, an `AiGenerationRequest` and `LlamaCppJniConfig` inform inference parameters; `ChatResponseParser` parses the response, and `AiCompletionParser` cleans its first content before return.
- The timing-aware path packages completion and available token/cache metrics as `AiGenerationTimings`; missing llama.cpp response timings produce zero metrics.
- OpenAI-compatible support maps request prompt and source context into system/user messages and configured options, and can convert a response plus elapsed time into generation timings.

#### Dependencies
- Shared request and result types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Local inference: `net.ladenthin:llama` types including `LlamaModel`, `ModelParameters`, and chat response parsing types.
- Compatible request support: Jackson JSON nodes, `ChatResponse` and `Usage`, and Java URL/environment lookup utilities.
- Provider dispatch also references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`; their implementations are not described in the supplied summaries.

#### Cross-cutting
- Provider implementations may throw `IOException`; the provider interface’s default timing method delegates to `generate` with zero timing values.
- `LlamaCppJniConfig` uses defaults from `AiGenerationConfig`; its builder is mutable and not thread-safe, while the built configuration has final fields.
- The llama.cpp provider lazily caches a model without synchronization; concurrent first calls may initialize more than one model.
- Compatible API support rejects configured API keys sent over plain HTTP to non-loopback hosts; HTTP(S) endpoint validation and 2xx success checks are centralized there.
