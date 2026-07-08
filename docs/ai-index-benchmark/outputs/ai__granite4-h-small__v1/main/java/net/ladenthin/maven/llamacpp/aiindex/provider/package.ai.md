### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: 073ED68B
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:21:36Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [AiGenerationTimings.java](AiGenerationTimings.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
---
> Provides a pluggable AI generation engine for Maven AI‑index, exposing local llama.cpp bindings and a deterministic mock, along with utilities to parse responses, record timing metrics, and build providers from configuration.

#### Purpose
- Supply AI text generation backends to Maven AI‑index.
- Offer a mock implementation for testing and a JNI‑based llama.cpp provider.
- Record generation timings for calibration.

#### Responsibilities
- **Provider Instantiation** – `AiGenerationProviderFactory` selects and constructs a provider by name.
- **Generation API** – `AiGenerationProvider` defines `generate` and `generateWithTimings`.
- **JNI Implementation** – `LlamaCppJniAiGenerationProvider` loads a GGUF model, executes chat completions, and extracts timings.
- **Configuration** – `LlamaCppJniConfig` holds immutable runtime options for the JNI provider.
- **Mocking** – `MockAiGenerationProvider` returns deterministic summaries and synthetic timing data.
- **Response Parsing** – `AiCompletionParser` strips internal chain‑of‑thought blocks from Gemma‑4 completions.
- **Timing Data** – `AiGenerationTimings` stores text and per‑token throughput metrics.

#### Key units
- **`AiGenerationProvider`** – interface with `generate`, `generateWithTimings`, `close`.
- **`AiGenerationProviderFactory`** – builds providers; handles `"mock"` and `"llamacpp-jni"`.
- **`LlamaCppJniAiGenerationProvider`** – JNI provider; lazy‑loads `LlamaModel`, builds `InferenceParameters`, calls `chatCompleteText`, parses with `AiCompletionParser`.
- **`LlamaCppJniConfig`** – immutable record of library path, model path, token limits, sampling, threads, GPU, reasoning, DRY, stop strings.
- **`MockAiGenerationProvider`** – deterministic summary generator; returns fixed `AiGenerationTimings`.
- **`AiCompletionParser`** – static method `parseCompletion(String)` removes thinking blocks, throws `IOException` on incomplete block.
- **`AiGenerationTimings`** – immutable holder of text, promptTokens, prefillTokensPerSecond, predictedTokens, decodeTokensPerSecond.

#### Data flow
1. **Client** requests generation via `AiGenerationProvider.generate(request)`.
2. **Factory** supplies provider instance (`Mock` or `LlamaCppJni`).
3. **Provider** builds `InferenceParameters` from `LlamaCppJniConfig` and `AiPromptSupport`.
4. **JNI provider** invokes `LlamaModel.chatCompleteText` (or `chatComplete`), obtaining a raw completion.
5. **Parser** cleans the completion (`AiCompletionParser.parseCompletion`).
6. **Provider** returns cleaned text or `AiGenerationTimings` containing text and token throughput.
7. **Client** consumes result; calibration goal may use `AiGenerationTimings`.

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `ConvertToRecord`, Lombok annotations.
- **External**: `net.ladenthin.llama` native bindings (`LlamaModel`, `InferenceParameters`, `ChatResponseParser`).
- **Java**: `java.io.IOException`, `java.nio.file.Path`, collections, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and `AiGenerationTimings` are value objects.
- **Thread safety**: `MockAiGenerationProvider` is stateless; factory and config are thread‑safe; JNI provider is single‑threaded (no explicit sync).
- **Error handling**: `AiCompletionParser` throws `IOException` for malformed thinking blocks; provider methods propagate `IOException` for generation failures.
- **Configuration**: `LlamaCppJniConfig` normalizes nullable lists to empty immutable lists; all fields are final.
- **Parsing**: `AiCompletionParser` centralizes response cleaning, ensuring consistent output across providers.
