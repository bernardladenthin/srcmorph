### java/provider
- H: 1.0
- C: 5AC65435
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:36:47Z
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
> Provides pluggable AI text generation for srcmorph, selecting a mock, local llama.cpp JNI, or OpenAI-compatible backend and cleaning model output for AI index files.

#### Purpose
- Supplies the AI completion backends used to generate per-file AI index text, behind one `AiGenerationProvider` contract.
- Handles provider selection, llama.cpp/OpenAI-compatible configuration, request building, endpoint and API-key safety, and removal of leading reasoning blocks from model output.

#### Responsibilities
- Provider contract and selection:
  - `AiGenerationProvider` interface (`generate`, `generateWithTimings`, `close`).
  - `AiGenerationProviderFactory` maps keys "mock", "llamacpp-jni", "openai-compatible" to implementations and resolves per-model vs run-level provider.
- Local llama.cpp inference:
  - `LlamaCppJniAiGenerationProvider` lazily loads a `LlamaModel` and runs chat completions with native timing metrics.
  - `LlamaCppJniConfig` immutable settings (GPU offload, KV cache, batch, DRY penalty, stop strings) built via `Builder`.
- Remote OpenAI-compatible inference:
  - `OpenAiCompatibleProviderSupport` builds chat-completions JSON, endpoint URL, key lookup, HTTP status checks, error messages, and timings. Socket handling lives in the provider.
- Output cleaning:
  - `AiCompletionParser` strips a leading `<|channel>thought ... <channel|>` block and rejects unterminated blocks.

#### Key units
- `AiGenerationProvider`: interface; generation contract with timing variant and `AutoCloseable`.
- `AiGenerationProviderFactory`: static `create`, `effectiveProvider`; constants `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider`: final class; lazy `LlamaModel` via `model()`, `chatResponse`, `generateWithTimings`.
- `LlamaCppJniConfig`: final immutable value class; nested `Builder` with 40 setters; `builder(modelPath)`.
- `OpenAiCompatibleProviderSupport`: package-private final helper; `requestBody`, `endpoint`, `apiKeyFor`, `isLoopbackHost`, `errorMessage`, `timings`.
- `AiCompletionParser`: `parseCompletion`; markers `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.
- `AiGenerationTimings`: result carrier for text plus prompt/cache/predicted token counts and rates (referenced, defined elsewhere).

#### Data flow
- Run config (`generationProvider`) and model config (`provider`) → `effectiveProvider` → `create` → concrete provider.
- Request (`AiGenerationRequest`: prompt key, source file, source text) → `AiPromptSupport` builds system and user messages → provider call.
- llama.cpp path: `LlamaCppJniConfig` → `ModelParameters` → `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser` → text.
- OpenAI-compatible path: `requestBody` JSON → HTTP POST to `endpoint()` (validated http/https, cleartext key check) → `ChatResponse` → `timings` / text cleaning.
- Output is the cleaned answer string or `AiGenerationTimings`, persisted by the caller to an AI index file.

#### Dependencies
- Internal collaborators: `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationConfig` (DEFAULT_* values), `ConvertToRecord`.
- llama.cpp binding (`net.ladenthin.llama`): `LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `Usage`, `ChatResponseParser`, `FlashAttn`, `ReasoningFormat`.
- Jackson: `ObjectMapper`, `ObjectNode`, `ArrayNode`.
- Lombok (`ToString`, `EqualsAndHashCode`) and jspecify `@Nullable`.
- Referenced, not in this package's summaries: `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`.

#### Cross-cutting
- Provider interface: `AiGenerationProvider` with default `generateWithTimings` (zero rates) and default no-op `close`; implementations override as needed.
- Factory-based selection: unknown key throws `IllegalArgumentException`; blank key falls back to mock.
- Errors: `IOException` for provider and parse failures (unterminated thinking block, HTTP errors via `errorMessage`, cleartext API key to non-loopback host); unchecked exceptions for config and unsupported providers.
- Null handling: `Objects.requireNonNull` in constructors; `@Nullable` on API keys, model handle, and `chatTemplateEnableThinking`; string and list setters in the builder map null to defaults.
- Configuration: all knobs in `LlamaCppJniConfig`; sentinel conventions (`-1` leave default, `0.0` disabled for minP and dry multiplier, `-1.0` disabled for topNSigma).
- Immutability: config and output carriers are immutable with unmodifiable lists; `OpenAiCompatibleProviderSupport` holds final fields only.
- Concurrency:
  - Factory and config are safe to share; each `create` returns a fresh provider.
  - `LlamaCppJniAiGenerationProvider` lazy `model` init is unsynchronized and not volatile; concurrent first calls may create multiple models.
  - `close()` does not null the model field, so repeated calls re-close the native handle.
