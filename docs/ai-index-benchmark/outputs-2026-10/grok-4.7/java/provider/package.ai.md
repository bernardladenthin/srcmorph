### java/provider
- H: 1.0
- C: C381D24F
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:45:45Z
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
I am synthesizing the per-file summaries into a dense structured markdown overview of the package.> Selects and runs local llama.cpp or OpenAI-compatible backends that turn source-morph generation requests into cleaned completion text and model timings.

#### Purpose
- Pluggable AI generation backends for source-morph: map a provider key to an `AiGenerationProvider`, run a request, and return answer text plus optional timings.
- Shared completion cleanup and immutable generation settings sit beside the local JNI and remote OpenAI-compatible paths.

#### Responsibilities
- Backend contract and selection: `AiGenerationProvider` plus `AiGenerationProviderFactory` resolve `mock`, `llamacpp-jni`, or `openai-compatible` and construct the matching provider.
- Local inference: `LlamaCppJniAiGenerationProvider` lazily loads a GGUF `LlamaModel` and runs chat completions with timings.
- Remote request assembly: `OpenAiCompatibleProviderSupport` builds the chat-completions JSON, endpoint, API-key cleartext guard, error excerpt, and timings without opening a socket.
- Answer cleanup: `AiCompletionParser` strips a leading Gemma-4 thinking block before the answer is stored.
- Settings: `LlamaCppJniConfig` holds immutable llama.cpp JNI sampling, KV/GPU, DRY, stop-string, and remote-provider fields for one model path.

#### Key units
- `AiGenerationProvider` — closeable contract: `generate`, default `generateWithTimings`, no-op `close`.
- `AiGenerationProviderFactory` — `effectiveProvider` prefers a model provider over the run key; `create` instantiates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider` — local GGUF chat via llama.cpp JNI; parses first content and maps `Timings` into `AiGenerationTimings`.
- `LlamaCppJniConfig` / `Builder` — 41-field immutable config; only construction path is `builder(modelPath)` then fluent setters and `build`.
- `OpenAiCompatibleProviderSupport` — package-private JSON body, `/chat/completions` URL, loopback-only cleartext API key, HTTP success/error excerpt, and timings.
- `AiCompletionParser` — `parseCompletion` drops a leading `<|channel>thought` … `<channel|>` block.

#### Data flow
- Run and model provider strings enter `effectiveProvider`; a null or blank effective key becomes `MockAiGenerationProvider`.
- `create` passes `LlamaCppJniConfig` and `AiPromptSupport` into the JNI or OpenAI-compatible provider.
- An `AiGenerationRequest` (prompt key, source file, source text, header) is the generation input.
- JNI path: lazy `ModelParameters` and `LlamaModel.chatComplete`, then `ChatResponseParser`, truncation/cache logs, then `AiCompletionParser`.
- Remote path: support builds `messages` from `AiPromptSupport`, sampling and optional seed/stop/reasoning fields, and a guarded endpoint; the provider that sends the request is not in these summaries.
- `generate` returns non-null text; `generateWithTimings` adds prompt/cache/predicted counts and rates, zeroed when timings are missing. Default interface timings are all zeros.

#### Dependencies
- Documents: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig` defaults.
- Local binding: `LlamaModel`, `ModelParameters`, `ChatResponse`, `ChatResponseParser`, `Timings`, `FlashAttn`, `ReasoningFormat`, `LlamaCppJniProviderSupport`.
- Remote JSON: Jackson `ObjectMapper` / `ObjectNode`; `Usage` for token counts; `Function` environment lookup for `apiKeyEnv`.
- Shared helpers: `Java8CompatibilityHelper` blank checks; Lombok `@ToString` / `@EqualsAndHashCode`; `@Nullable`; `@ConvertToRecord`.

#### Cross-cutting
- Provider keys: `PROVIDER_MOCK` (`"mock"`), `PROVIDER_LLAMACPP_JNI` (`"llamacpp-jni"`), `PROVIDER_OPENAI_COMPATIBLE` (`"openai-compatible"`). Unrecognized keys throw `IllegalArgumentException`.
- Failures surface as `IOException` (generation, missing thinking end marker, JSON, cleartext key to a non-loopback host) or `IllegalArgumentException` / `NullPointerException` on bad config.
- Sentinels stay numeric: `-1` often leaves the binding default; `0` often means disabled. `apiKeyEnv` stores an env-var name, never the key.
- Immutability: config fields are final; list accessors are unmodifiable views. Parser and support are stateless after construction.
- Concurrency gap: JNI `LlamaModel` lazy init is unsynchronized and not volatile. `Builder` is not thread-safe. Support’s static `MAPPER` is shared with no locks in that class.
