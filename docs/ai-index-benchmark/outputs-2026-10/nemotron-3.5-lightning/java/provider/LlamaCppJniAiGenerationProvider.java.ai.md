### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:02:08Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI code generation by running local GGUF model inference via llama.cpp.

#### Purpose
- `LlamaCppJniAiGenerationProvider` provides AI code generation by running local GGUF model inference via llama.cpp
- Native `LlamaModel` handle is lazily initialized on first `generate` call

#### Type
- Kind: class
- Modifiers: public, final
- Implements: `AiGenerationProvider`, `AutoCloseable`
- Annotations: `@ToString` on class, `@ToString.Exclude` on fields
- No `extends` (implicit `Object`)

#### Input
- Constructor `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig config, AiPromptSupport promptSupport)` requires non-null `config` and `promptSupport`
- `AiGenerationRequest` passed to `generate` and `generateWithTimings` carries generation parameters
- `LlamaCppJniProviderSupport support` field consumes `config` and `promptSupport` to build inference parameters
- Model configuration read from `config`: model path, context size, threads, GPU layers, flash attention, cache type KV/V, batch size, ubatch size, threads batch, main GPU, devices, lazy mode, SWA full, cache reuse, CPU MoE/FFN layers

#### Output
- `generate`
