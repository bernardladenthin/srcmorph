### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:51:31Z
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
> Generates and cleans AI completions through selectable local llama.cpp or OpenAI-compatible backends, with configurable inference and timing support.

#### Purpose
- Defines provider selection and a common generation contract for turning `AiGenerationRequest` inputs into completion text and optional timing metrics.
- Configures local or compatible API generation, and removes leading model reasoning blocks from returned answers.

#### Responsibilities
- **Provider abstraction and selection:** `AiGenerationProvider` defines generation, timing, and resource-close operations; `AiGenerationProviderFactory` selects mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a GGUF model, applies inference configuration, parses responses, reports native timings, and closes the model.
- **Generation configuration:** `LlamaCppJniConfig` supplies builder-based settings for sampling, hardware, caching, reasoning, stop controls, and API-provider options.
- **Completion cleanup:** `AiCompletionParser` trims output and removes a leading Gemma-compatible thinking block, reporting an incomplete block as an `IOException`.
- **OpenAI-compatible request support:** `OpenAiCompatibleProviderSupport` builds chat-completion payloads and endpoints, resolves API keys, interprets HTTP results, and calculates timing metrics.

#### Key units
- `AiGenerationProvider` — pluggable generation interface with a default zero-metrics timing wrapper and no-op `close`.
- `AiGenerationProviderFactory` — resolves model/run provider precedence and constructs the selected provider.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp JNI inference with lazy model caching and response parsing.
- `LlamaCppJniConfig` — immutable configuration value object with a fluent nested `Builder`.
- `AiCompletionParser` — strips a leading reasoning block and returns cleaned completion text.
- `OpenAiCompatibleProviderSupport` — package-private helper for request construction, API-key safety, HTTP result interpretation, and timings.

#### Data flow
- Provider selection uses a nonblank model provider in preference to the run provider; absent or blank selection defaults to the mock provider.
- A generation request and prompt support feed the chosen backend; the local backend configures or reuses a `LlamaModel`, invokes chat completion, then parses the response text.
- Timing-aware generation returns completion text with token and rate metrics when available; absent local timing data yields zero metrics.
- OpenAI-compatible support turns request and configuration data into a non-streaming chat-completion payload, resolves endpoint and key settings, and derives timing metrics from response usage and elapsed time.

#### Dependencies
- Project request, prompt, and metrics types: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`.
- Provider implementations: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- Local inference and response types: llama.cpp JNI `LlamaModel`, `ModelParameters`, `ChatResponse`, and related parser/timing types.
- OpenAI-compatible support uses Jackson JSON nodes; provider/configuration code also uses `Java8CompatibilityHelper`.

#### Cross-cutting
- Provider operations use `IOException` for generation and resource-close failures; completion parsing rejects an unclosed leading thinking block.
- `LlamaCppJniConfig` is final-field based, but retains list references; its builder is not thread-safe.
- Local model initialization and cached model access are unsynchronized; no broader provider thread-safety guarantee is specified.
- API-key support avoids sending configured credentials over plain HTTP to non-loopback hosts.
