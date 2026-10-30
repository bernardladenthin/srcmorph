### java/provider
- H: 1.0
- C: F22FA714
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:48:01Z
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
> Local AI text generation for source-aware coding tasks, with provider selection, native llama.cpp/JNI execution, OpenAI-compatible HTTP handling, and reasoning stripping before completion storage.

#### Purpose
- Implements provider-agnostic AI generation for source-aware coding requests.
- Normalizes provider selection, model configuration, output cleanup, and timing/error metadata before results are used.

#### Responsibilities
- Provider selection and fallback — `AiGenerationProviderFactory` resolves the effective backend and defaults to `MockAiGenerationProvider`.
- Native local generation — `LlamaCppJniAiGenerationProvider` and `LlamaCppJniConfig` configure llama.cpp JNI options and chat execution.
- OpenAI-compatible request handling — `OpenAiCompatibleProviderSupport` builds payloads, validates URLs and API keys, and formats errors/timings.
- Completion sanitization — `AiCompletionParser` strips leading Gemma reasoning markers from raw model output.
- Shared generation contract — `AiGenerationProvider` defines generate/close/timing behavior for all providers.

#### Key units
- `AiGenerationProvider` — contract for AI text generation with default `generateWithTimings(...)` and no-op `close()`.
- `AiGenerationProviderFactory` — resolves the active provider key and instantiates the correct backend.
- `LlamaCppJniConfig` — immutable builder-backed settings object for model path, context, sampling, cache, GPU, and API configuration.
- `LlamaCppJniAiGenerationProvider` — lazy-loads the native `LlamaModel`, builds inference parameters, and returns parsed completion text and timings.
- `OpenAiCompatibleProviderSupport` — prepares OpenAI-compatible chat-completions JSON, validates keys and hosts, and computes HTTP timing/error metadata.
- `AiCompletionParser` — removes leading Gemma `THINKING_BLOCK_*` markers so only final answer text remains.
- `AiGenerationRequest` — request carrier for prompt key, source file, source text, and current header context.
- `AiGenerationTimings` — timing metadata object for prompt/prediction rate and token counts.

#### Data flow
- `AiGenerationProviderFactory.create(...)` chooses the backend via `effectiveProvider(...)`.
- `LlamaCppJniAiGenerationProvider.generate(...)` builds inference parameters from `AiPromptSupport`, loads the native `LlamaModel`, and executes `chatComplete(...)`.
- `ChatResponseParser` extracts the raw response; `AiCompletionParser.parseCompletion(...)` strips leading reasoning before returning the final answer.
- `generateWithTimings(...)` wraps text and `Timings` into `AiGenerationTimings`, defaulting to zero values when metrics are unavailable.
- `OpenAiCompatibleProviderSupport.requestBody(...)` builds the JSON chat-completions payload, applies endpoint/key checks, and converts HTTP results into formatted errors or timing metadata.

#### Dependencies
- Internal project modules:
  - `net.ladenthin.srcmorph.document.AiGenerationRequest`
  - `net.ladenthin.srcmorph.prompt.AiPromptSupport`
  - `net.ladenthin.srcmorph.provider.AiGenerationTimings`
  - `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
  - `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
  - `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
- External libraries and native surfaces:
  - `net.ladenthin.llama.*` for `LlamaModel`, `ChatResponse`, `Timings`, `ModelParameters`, and chat parsing
  - `com.fasterxml.jackson` `ObjectMapper`, `ObjectNode`, and `ArrayNode`
  - `java.io`, `java.util`, `java.net.URL`, `java.util.function.Function`
  - Lombok `@ToString`, `@EqualsAndHashCode`, and `@ConvertToRecord`
  - JSpecify `@Nullable`

#### Cross-cutting
- Null/blank normalization — provider names, config strings, and environment values are trimmed and defaulted; blank inputs fall back to mock or default config values.
- Immutable configuration — `LlamaCppJniConfig` is final and builder-driven; the package heavily relies on value objects and generated equality/toString output.
- Validation and security checks — `Objects.requireNonNull`, URL scheme validation, loopback host enforcement, and cleartext API key rejection prevent invalid or insecure requests.
- Lazy native lifecycle — `LlamaCppJniAiGenerationProvider` caches a single `LlamaModel` and closes it only when present; no explicit synchronization or lock is used.
- Timing and error handling — provider methods return zeroed `AiGenerationTimings` when unavailable and truncate HTTP error bodies before formatting failure messages.
- Shared conventions — `@ToString` on provider/config classes and small default implementations reduce boilerplate without centralizing provider behavior.
