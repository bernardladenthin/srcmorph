### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:32:48Z
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
> Provides pluggable AI text generation for Maven‑based llama.cpp indexing, featuring a JNI local provider, a deterministic mock, and parsing utilities to clean LLM output.

#### Purpose  
- Deliver AI generation services for Maven llama.cpp indexer.  
- Support local (JNI) and mock backends.

#### Responsibilities  
- **Parsing** – Extract final answer from raw completion (AiCompletionParser).  
- **Contract** – Define generation API (`AiGenerationProvider`).  
- **Factory** – Create provider instances from a key (`AiGenerationProviderFactory`).  
- **JNI Implementation** – Lazily load llama.cpp, build prompts, run inference (`LlamaCppJniAiGenerationProvider`).  
- **Configuration** – Immutable holder for JNI parameters (`LlamaCppJniConfig`).  
- **Mocking** – Deterministic summary for tests (`MockAiGenerationProvider`).

#### Key units  
- `AiCompletionParser`: strips Gemma‑4 thinking blocks, returns clean string.  
- `AiGenerationProvider`: interface for generating text, with default temperature/close logic.  
- `AiGenerationProviderFactory`: selects provider (`"mock"` → `MockAiGenerationProvider`, `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`).  
- `LlamaCppJniAiGenerationProvider`: builds prompt via `AiPromptSupport`, configures `InferenceParameters`, invokes `model.chatCompleteText`, parses result.  
- `LlamaCppJniConfig`: record‑style immutable config (paths, context, tokens, temperature, threads, top‑p/k, repeat penalty, stop strings).  
- `MockAiGenerationProvider`: returns `"Mock summary for <fileName>"` from `AiGenerationRequest`.

#### Data flow  
1. `AiGenerationProviderFactory.create` → provider instance.  
2. Caller passes `AiGenerationRequest` (prompt key, source, header).  
3. JNI provider builds prompt (`promptSupport.buildPrompt`).  
4. Wraps prompt in chat message pair, configures `InferenceParameters` from `LlamaCppJniConfig`.  
5. Calls `model.chatCompleteText` → raw completion.  
6. `AiCompletionParser.parseCompletion` → trimmed answer.  
7. Result returned to caller.

#### Dependencies  
- `net.ladenthin.llama` (model, parameters, Pair).  
- `lombok` for annotations.  
- `jspecify.annotations.Nullable`.  
- Internal: `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`.  
- Native JNI library path supplied via `LlamaCppJniConfig`.

#### Cross-cutting  
- **Immutability**: `LlamaCppJniConfig` and most helper classes are immutable, thread‑safe.  
- **Exception handling**: `IOException` for provider failures, `IllegalArgumentException` for unknown provider keys.  
- **Concurrency**: `AiCompletionParser` is stateless; `LlamaCppJniAiGenerationProvider` performs unsynchronized lazy initialization, not thread‑safe by default.  
- **Factory pattern**: Centralizes provider creation, allows easy extension.  
- **Logging/ToString**: Lombok `@ToString` on key classes for debugging.
