### java/provider
- H: 1.0
- C: C381D24F
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:32:48Z
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
> Provides pluggable AI text generation for source-file documentation via mock, local llama.cpp JNI, and OpenAI-compatible backends, with provider selection, config, and completion parsing.

#### Purpose
- Abstracts AI text generation behind `AiGenerationProvider`, so local llama.cpp, remote OpenAI-compatible, and mock providers are interchangeable.
- Supplies per-call timings for the `srcmorph:calibrate` goal and strips model chain-of-thought before answers are stored.

#### Responsibilities
- Provider contract: `AiGenerationProvider` interface with `generate`, `generateWithTimings` (default zeroed timings), and `close`.
- Provider selection: `AiGenerationProviderFactory` maps keys `mock`, `llamacpp-jni`, `openai-compatible` to instances; model `provider` overrides run `generationProvider` via `effectiveProvider`.
- Local inference: `LlamaCppJniAiGenerationProvider` lazily loads a GGUF model through `net.ladenthin.llama.LlamaModel` and maps chat responses and `Timings` to `AiGenerationTimings`.
- Remote inference: `OpenAiCompatibleProviderSupport` builds chat-completions JSON, endpoint URL, API key lookup, and HTTP status/error handling. Socket code lives in `OpenAiCompatibleAiGenerationProvider`, which is not in this package summary set.
- Configuration: immutable `LlamaCppJniConfig` with a fluent `Builder` (40 setters, 41 fields) defaulting from `AiGenerationConfig.DEFAULT_*`.
- Output cleanup: `AiCompletionParser` removes leading `<|channel>thought` ... `<channel|>` Gemma-4 thinking blocks.

#### Key units
- `AiGenerationProvider`: backend interface extending `AutoCloseable`.
- `AiGenerationProviderFactory`: `create(...)` and `effectiveProvider(...)`; constants `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider`: local GGUF generation over `LlamaModel`; lazy `model()` loader.
- `OpenAiCompatibleProviderSupport`: package-private offline helper; `requestBody`, `endpoint`, `apiKeyFor`, `isLoopbackHost`, `errorMessage`, `timings`.
- `LlamaCppJniConfig`: immutable settings value; nested `Builder`; `builder(modelPath)`.
- `AiCompletionParser`: `parseCompletion(String)`; markers `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.

#### Data flow
- Run/model config → `AiGenerationProviderFactory.effectiveProvider` → `create(...)` → concrete `AiGenerationProvider`.
- `AiGenerationRequest` (prompt key, source file, source text) → `AiPromptSupport` builds messages and inference parameters → provider call.
- Local path: `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser.parseCompletion` → text; `Timings` → `AiGenerationTimings`.
- Remote path: `requestBody` JSON → HTTP POST to `endpoint()` (in provider class) → `ChatResponse`/`Usage` → `timings(...)`.

#### Dependencies
- Internal: `AiGenerationRequest` and `AiPromptSupport` (`document`, `prompt`); `Java8CompatibilityHelper`; `AiGenerationConfig` defaults; `ConvertToRecord`.
- External: `net.ladenthin.llama` (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `Usage`, `ChatResponseParser`, `FlashAttn`, `ReasoningFormat`); Jackson `ObjectMapper`/`ObjectNode`/`ArrayNode`; Lombok; `org.jspecify.annotations.Nullable`.
- Referenced but not summarized here: `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`, `AiGenerationTimings`.

#### Cross-cutting
- Errors: `IOException` for provider failures, missing end marker, and cleartext-key rejection; `IllegalArgumentException` for unknown provider keys and non-http(s) `baseUrl`; `NullPointerException` via `Objects.requireNonNull` on constructor inputs.
- Security: API keys read from environment variables named in `apiKeyEnv`; plain `http` allowed only for loopback hosts.
- Concurrency: config and parser are immutable or stateless; `LlamaCppJniAiGenerationProvider.model()` lazy init is not thread-safe; `LlamaCppJniConfig.Builder` is single-thread only.
- Null handling: null string config values fall back to defaults; null lists become empty; `@Nullable` used on `model`, `chatTemplateEnableThinking`, and environment lookups.
- Lombok `@ToString` used across classes, with sensitive or heavy fields excluded.
