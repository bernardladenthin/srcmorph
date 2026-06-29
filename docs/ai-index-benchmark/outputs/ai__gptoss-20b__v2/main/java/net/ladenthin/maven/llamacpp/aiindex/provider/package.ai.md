### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:22:58Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
---
> Provides pluggable AI text generation backends (llama.cpp JNI or mock) and parses LLM completions into clean answers.

#### Purpose
- Expose a configurable AI generation provider API.
- Parse raw model outputs, removing internal reasoning blocks.

#### Responsibilities
- **Parsing**: `AiCompletionParser` cleans completion text.
- **Interface**: `AiGenerationProvider` defines generation contract.
- **Factory**: `AiGenerationProviderFactory` selects provider implementation.
- **JNI provider**: `LlamaCppJniAiGenerationProvider` loads a GGUF model, builds prompts, and invokes native inference.
- **Configuration**: `LlamaCppJniConfig` holds immutable JNI settings.
- **Mock**: `MockAiGenerationProvider` supplies deterministic summaries for tests.

#### Key units
- `AiCompletionParser` – removes `THINKING_BLOCK_*` markers; throws `IOException` on incomplete blocks.
- `AiGenerationProvider` – `generate(AiGenerationRequest)` and optional temperature override; `close()` default no‑op.
- `AiGenerationProviderFactory` – `create(String, LlamaCppJniConfig, AiPromptSupport)` returns provider; defaults to `MockAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider` – lazily builds `LlamaModel`, constructs `InferenceParameters`, calls native `chatCompleteText`, and parses result.
- `LlamaCppJniConfig` – immutable holder for library path, model path, context size, max output, sampling params, stop strings.
- `MockAiGenerationProvider` – returns `"Mock summary for <file>"` based on request’s source file.

#### Data flow
1. Caller invokes `AiGenerationProviderFactory.create(...)`.
2. Provider receives `AiGenerationRequest` (prompt key, source file/text, header).
3. For JNI provider, `AiPromptSupport.buildPrompt` generates chat prompt.
4. `InferenceParameters` constructed with temperature, NPredict, topP/K, repeatPenalty, stopStrings.
5. Native `LlamaModel.chatCompleteText` executed.
6. Output parsed by `AiCompletionParser.parseCompletion` to strip reasoning blocks.
7. Final answer returned to caller.

#### Dependencies
- Factory ↔ `Java8CompatibilityHelper`, `AiPromptSupport`, `LlamaCppJniConfig`, provider classes.
- JNI provider ↔ `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `LlamaModel`, `InferenceParameters`, `ModelParameters`.
- Config ↔ `ConvertToRecord`, `Objects`, `List`.
- Parser ↔ `IOException`, `lombok.ToString`.
- Mock ↔ `AiGenerationRequest`, `Path`.

#### Cross-cutting
- Lombok annotations (`@ToString`, `@EqualsAndHashCode`) for concise data classes.
- Consistent `IOException` for I/O or parsing errors.
- Immutability of `LlamaCppJniConfig` ensures thread safety.
- Lazy initialization pattern in `LlamaCppJniAiGenerationProvider`.
