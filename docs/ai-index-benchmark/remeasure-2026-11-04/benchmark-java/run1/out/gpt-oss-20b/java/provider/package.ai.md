### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:18:09Z
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
> Provides pluggable AI back‑ends for source‑code generation, including local llama.cpp inference and OpenAI‑compatible chat completions, with configurable prompts, timing metrics, and response parsing.

#### Purpose
- Supply AI‑generated text for code transformation requests.
- Offer multiple provider implementations (mock, local, OpenAI‑compatible).

#### Responsibilities
- **Provider selection** – `AiGenerationProviderFactory` resolves provider keys and instantiates the appropriate `AiGenerationProvider`.
- **Prompt handling** – `OpenAiCompatibleProviderSupport` builds request payloads and validates API keys.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads GGUF models via JNI, runs chat completions, and extracts timing data.
- **Response parsing** – `AiCompletionParser` strips internal reasoning blocks from raw completions.
- **Configuration** – `LlamaCppJniConfig` holds immutable settings for llama.cpp execution.

#### Key units
- `AiGenerationProviderFactory` – resolves provider key → concrete provider.
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `MockAiGenerationProvider` – dummy provider for testing.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference, lazy model loading, timing extraction.
- `OpenAiCompatibleProviderSupport` – builds OpenAI‑compatible request, validates key, parses response, computes timings.
- `AiCompletionParser` – removes leading THINKING_BLOCK markers from completions.
- `LlamaCppJniConfig` – immutable builder‑style config for llama.cpp parameters.

#### Data flow
1. **Request creation** – `AiGenerationRequest` contains prompt key, source file, text, header.
2. **Provider selection** – `AiGenerationProviderFactory.create` chooses provider based on run or model config.
3. **Prompt resolution** – provider (or support class) obtains system/user prompts via `AiPromptSupport`.
4. **Generation** – provider’s `generate` or `generateWithTimings` is invoked:
   - *Local*: `LlamaCppJniAiGenerationProvider` loads model, calls `chatComplete`, parses `ChatResponse`, extracts text and timings.
   - *OpenAI*: `OpenAiCompatibleProviderSupport` builds JSON, sends HTTP request, parses `ChatResponse`, computes timings.
5. **Post‑processing** – `AiCompletionParser.parseCompletion` removes any leading reasoning block before returning final answer.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `LlamaCppJniConfig`.
- **External**: `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `lombok`.
- **Configuration**: `AiGenerationConfig` defaults used by `LlamaCppJniConfig.Builder`.

#### Cross‑cutting
- **Immutability**: `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are immutable, thread‑safe.
- **Error handling**: `IOException` for I/O or malformed responses; `IllegalArgumentException` for unknown provider keys or invalid URLs.
- **Timing**: `AiGenerationTimings` captures prompt, cache, and prediction metrics; providers expose `generateWithTimings`.
- **Logging**: Providers log prompt cache reuse and truncation warnings (not shown in summaries but implied).
- **Resource management**: `AiGenerationProvider` extends `AutoCloseable`; local provider disposes native model on `close`.
