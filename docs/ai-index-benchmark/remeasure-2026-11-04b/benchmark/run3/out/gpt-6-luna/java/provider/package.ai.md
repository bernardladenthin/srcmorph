### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:07:46Z
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
> Generates AI documentation completions from source-aware requests using selectable local llama.cpp, OpenAI-compatible, or mock backends.

#### Purpose
- Defines provider selection, configuration, and generation support for AI completion workflows.
- Normalizes model output and exposes generation text plus optional token and throughput metrics.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` standardizes generation, timing, and cleanup; `AiGenerationProviderFactory` resolves run/model provider keys and creates the backend.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and configures a GGUF model, generates chat responses, reports timings, and closes native resources.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completions JSON, selects and validates endpoints and API-key transmission, formats HTTP errors, and computes timing metrics.
- **Configuration and output cleanup:** `LlamaCppJniConfig` captures sampling, model, hardware, cache, provider, and API settings; `AiCompletionParser` strips a leading Gemma-4 reasoning block and detects unterminated blocks.

#### Key units
- `AiGenerationProvider` — backend interface with default zero-valued timing metrics and no-op `close()`.
- `AiGenerationProviderFactory` — prefers a nonblank model provider over the run provider, defaults blank selection to mock, and rejects unknown keys.
- `LlamaCppJniConfig` — immutable configuration with a fluent builder and defaults from `AiGenerationConfig`.
- `LlamaCppJniAiGenerationProvider` — local JNI inference provider with lazy model creation and optional prompt-cache and throughput reporting.
- `OpenAiCompatibleProviderSupport` — package-private helper for request serialization, endpoint and key safety, HTTP errors, and usage-based timings.
- `AiCompletionParser` — trims completions and removes only a leading `"<|channel>thought"` block through its closing marker.

#### Data flow
- The factory resolves the provider key and constructs the selected backend from `LlamaCppJniConfig` and `AiPromptSupport`.
- Providers consume `AiGenerationRequest`; local inference builds backend parameters through prompt support, while OpenAI-compatible support creates system/user messages and serializes configured generation options.
- Generated text is parsed to remove leading reasoning content; provider timing paths return text alongside token counts and available throughput metrics.

#### Dependencies
- Internal request and prompt types: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`.
- Provider implementations referenced by the factory: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- External integrations include the `net.ladenthin:llama` JNI binding and Jackson JSON nodes/serialization; shared utilities include `Java8CompatibilityHelper`.

#### Cross-cutting
- Providers declare `IOException` for generation or resource failures; the completion parser uses it for an unterminated leading reasoning block.
- `LlamaCppJniConfig` centralizes inference, hardware, cache, and API settings; its builder is not thread-safe, and supplied list values are not defensively copied.
- Local model initialization and closure are unsynchronized; OpenAI-compatible API keys are restricted to HTTPS or loopback HTTP destinations.
