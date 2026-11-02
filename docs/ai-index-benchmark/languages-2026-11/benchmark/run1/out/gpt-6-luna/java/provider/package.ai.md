### java/provider
- H: 1.0
- C: 4CF08994
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:16:41Z
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
> Selects and runs AI text-generation backends for source-transformation requests, supporting local llama.cpp inference and OpenAI-compatible chat APIs.

#### Purpose
- Provide a pluggable `AiGenerationProvider` contract, provider selection, generation configuration, and completion cleanup.
- Support local native-model and remote OpenAI-compatible generation, including optional timing and request/response helpers.

#### Responsibilities
- **Provider selection and contract:** Resolve model-level versus run-level provider settings, default to mock generation, and define text generation, timing, and resource-close behavior.
- **Local inference:** Lazily load and reuse llama.cpp models, build inference parameters, parse completions, report truncation/cache reuse, and return timing data.
- **Remote request support:** Construct OpenAI-compatible chat-completions JSON, resolve endpoints and API keys, format HTTP failures, and calculate decode rates.
- **Configuration and output cleanup:** Store sampling, hardware, cache, reasoning, provider, and stop-sequence options; remove leading Gemma-style reasoning blocks from completion text.

#### Key units
- `AiGenerationProvider` — pluggable generation interface with default timing and close behavior.
- `AiGenerationProviderFactory` — selects mock, `llamacpp-jni`, or `openai-compatible` providers using effective provider configuration.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp inference and returns parsed text or model timing data.
- `LlamaCppJniConfig` — immutable provider and model configuration built through a fluent `Builder`.
- `OpenAiCompatibleProviderSupport` — package-private helper for chat request bodies, endpoints, API keys, errors, and timing calculations.
- `AiCompletionParser` — strips a leading reasoning block and reports an unclosed block as `IOException`.

#### Data flow
- `AiGenerationProviderFactory` resolves the configured provider and creates the corresponding implementation; absent or blank selection falls back to mock.
- Providers consume `AiGenerationRequest`; the local provider builds inference parameters and parses the model response, while OpenAI-compatible support builds request JSON and provides response-related helpers.
- Completion output may pass through `AiCompletionParser`; timing-capable generation returns `AiGenerationTimings`, with zero values used when local timing data is absent.

#### Dependencies
- Internal request, prompt, and timing types: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`.
- Local inference uses `LlamaCppJniProviderSupport` and llama JNI types such as `LlamaModel`, `ModelParameters`, and `ChatResponse`.
- Remote request support uses Jackson JSON nodes and `ChatResponse`/`Usage`; it does not perform network I/O itself.
- `Java8CompatibilityHelper` is used by provider selection and provider support.

#### Cross-cutting
- `LlamaCppJniConfig` has final fields and builder-based construction; its builder is not thread-safe, and supplied list contents are not defensively copied.
- The local provider’s lazy model initialization and close are unsynchronized, so concurrent initialization or close may race.
- Provider failures use `IOException` where declared; unsupported provider keys raise `IllegalArgumentException`.
- OpenAI-compatible support allows API keys over HTTPS or loopback HTTP only; other cleartext HTTP key transmission is rejected.
