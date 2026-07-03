### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: 073ED68B
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:27:05Z
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
> A framework for generating and timing AI‑based text completions, with a pluggable backend that supports a mock provider for testing and a native llama.cpp JNI provider for production.

#### Purpose
- Provide a pluggable AI text generation service for Maven AI indexing.
- Offer optional performance metrics for calibration.

#### Responsibilities
- **Request handling** – accept `AiGenerationRequest` objects containing prompt data.
- **Provider selection** – instantiate the correct `AiGenerationProvider` (mock or llama.cpp JNI) via a factory.
- **Completion parsing** – strip internal thinking blocks from raw LLM outputs.
- **Timing extraction** – capture model token counts and throughput for analysis.
- **Configuration** – expose immutable runtime settings for the llama.cpp backend.

#### Key units
- `AiGenerationProvider` (interface) – defines `generate` and `generateWithTimings`.
- `AiGenerationProviderFactory` – selects and creates providers based on a key.
- `MockAiGenerationProvider` – deterministic mock implementation for tests.
- `LlamaCppJniAiGenerationProvider` – JNI‑backed provider that loads a `LlamaModel`, builds inference parameters, runs chat completion, and parses timing.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp runtime options.
- `AiCompletionParser` – removes thinking blocks from raw completions.
- `AiGenerationTimings` – immutable DTO with text, token counts, and throughput.

#### Data flow
1. **Client code** requests generation via `AiGenerationProviderFactory.create(...)`.
2. Factory returns a concrete provider (mock or JNI) based on the supplied name.
3. Provider receives an `AiGenerationRequest` (prompt key, source file/text, header).
4. For the JNI provider:
   - Lazily loads a `LlamaModel` using `LlamaCppJniConfig`.
   - Builds `InferenceParameters` from the request and config.
   - Calls the native model to produce a chat completion JSON.
   - Parses the first choice’s content with `AiCompletionParser`.
   - Extracts `Timings` from the JSON and wraps results in `AiGenerationTimings`.
5. For the mock provider:
   - Generates a deterministic summary string and synthetic timing data.

#### Dependencies
- **Internal**: `AiGenerationRequest`, `AiPromptSupport`, `AiCompletionParser`, `ChatResponseParser`, `InferenceParameters`, `LlamaModel`.
- **External**: `net.ladenthin:llama` JNI library, Lombok annotations, `org.jspecify.annotations.Nullable`.
- **Config**: `LlamaCppJniConfig` provides all runtime parameters for the native provider.

#### Cross‑cutting
- **Immutability**: `LlamaCppJniConfig` and `AiGenerationTimings` are final and thread‑safe.
- **Error handling**: `AiCompletionParser` throws `IOException` for malformed thinking blocks; providers propagate `IOException` on failures.
- **Lazy initialization**: `LlamaCppJniAiGenerationProvider` defers model loading until first request.
- **Mock support**: `MockAiGenerationProvider` offers deterministic outputs for unit tests, avoiding native dependencies.
