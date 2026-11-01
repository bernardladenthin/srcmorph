### java/provider
- H: 1.0
- C: 43C2C5D3
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:39:05Z
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
> Generates and normalizes AI completions for source-document workflows, selecting local llama.cpp, OpenAI-compatible, or mock backends and optionally reporting generation timings.

#### Purpose
- Provides a pluggable `AiGenerationProvider` contract and factory for producing text from `AiGenerationRequest`.
- Centralizes completion cleanup and configuration for local GGUF inference and OpenAI-compatible generation.

#### Responsibilities
- **Provider selection:** `AiGenerationProviderFactory` resolves model-level versus run-level provider keys and creates mock, `llamacpp-jni`, or `openai-compatible` providers.
- **Local inference and lifecycle:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a native `LlamaModel`, applies configured inference settings, returns completion text and timing metrics, and closes the model.
- **OpenAI-compatible request support:** `OpenAiCompatibleProviderSupport` builds chat-completions request JSON, endpoint and API-key values, response error messages, and timing metadata; it does not perform network I/O.
- **Completion cleanup:** `AiCompletionParser` removes a leading Gemma-4 thinking block while preserving other marker text.
- **Generation configuration:** `LlamaCppJniConfig` and its fluent `Builder` hold model, sampling, provider, reasoning, caching, GPU/CPU, and batching settings, with defaults sourced from `AiGenerationConfig`.

#### Key units
- `AiGenerationProvider` — backend interface for generation, optional timing results, and resource cleanup.
- `AiGenerationProviderFactory` — provider-key resolution and backend construction.
- `LlamaCppJniAiGenerationProvider` — local llama.cpp JNI inference and native-model lifecycle.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — generation and provider configuration values and fluent construction.
- `OpenAiCompatibleProviderSupport` — OpenAI-compatible request, endpoint, API-key, error, and timing preparation.
- `AiCompletionParser` — strips a leading reasoning block from completion text.

#### Data flow
- Run-level and optional model-level provider keys go to `AiGenerationProviderFactory`; the selected provider receives an `AiGenerationRequest`.
- The llama.cpp provider combines request data, `AiPromptSupport`, and `LlamaCppJniConfig` to run inference, then parses completion text and may return model-reported timing and cache metrics.
- OpenAI-compatible support converts request and configuration data into chat-completions JSON and endpoint/auth metadata; it can also convert response usage and elapsed time into `AiGenerationTimings`.
- `AiCompletionParser` trims generated text and removes a leading thinking block; an unterminated leading block raises `IOException`.

#### Dependencies
- Project types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, and `Java8CompatibilityHelper`.
- Local inference: `LlamaCppJniProviderSupport` and llama JNI types including `LlamaModel`, `ModelParameters`, `ChatResponse`, and `Timings`.
- OpenAI-compatible support: Jackson JSON types and llama response usage types (`ChatResponse`, `Usage`).
- Provider factory also references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
- Provider implementations may throw `IOException`; the `AiGenerationProvider` default timing method wraps generated text with zero-valued timing data.
- `LlamaCppJniConfig` is final-field configuration; its builder is not thread-safe, and supplied list values are not copied.
- Local model initialization and cached access are unsynchronized; concurrent first calls may race, with no explicit thread-safety guarantee.
- OpenAI-compatible support validates HTTP(S) endpoints and only permits API keys over HTTPS or plain HTTP to loopback hosts.
