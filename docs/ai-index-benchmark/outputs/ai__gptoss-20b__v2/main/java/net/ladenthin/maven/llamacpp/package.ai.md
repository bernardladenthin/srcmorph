### main/java/net/ladenthin/maven/llamacpp
- H: 1.0
- C: 52B8025B
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:28:56Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [aiindex/](aiindex/package.ai.md)
---
> A Maven‑based AI field‑generation framework for Llama‑Cpp that maps prompts to models and offers pluggable JNI or mock providers.

#### Purpose
- Configure AI field generation for the Llama‑Cpp Maven plugin.  
- Provide pluggable Llama‑Cpp JNI or deterministic mock backends.

#### Responsibilities
- **Configuration** – `AiFieldGenerationConfig`, `AiGenerationConfig`, `AiModelDefinition`, `AiModelDefinitionSupport`.  
- **Provider** – `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `MockAiGenerationProvider`.  
- **Parsing** – `AiCompletionParser`.

#### Key units
- `AiFieldGenerationConfig`: DTO for prompt‑model mapping, copies extensions.  
- `AiFieldGenerationSelector`: chooses first matching config by file extension.  
- `AiGenerationConfig`: immutable hyper‑parameters, unmodifiable stop strings.  
- `AiModelDefinition`: mutable mirror of `AiGenerationConfig`.  
- `AiModelDefinitionSupport`: builds key → `AiGenerationConfig` map, validates keys.  
- `AiGenerationProvider`: contract `generate(AiGenerationRequest)`; optional temperature; `close()`.  
- `AiGenerationProviderFactory`: selects provider implementation; defaults to `MockAiGenerationProvider`.  
- `LlamaCppJniAiGenerationProvider`: lazily constructs `LlamaModel`, builds `InferenceParameters`, calls native `chatCompleteText`, parses result.  
- `LlamaCppJniConfig`: immutable settings (library path, model path, context size, max output, sampling, stop strings).  
- `MockAiGenerationProvider`: deterministic summary `"Mock summary for <file>"`.  
- `AiCompletionParser`: removes `THINKING_BLOCK_*` markers, throws `IOException` on incomplete blocks.

#### Data flow
1. Maven injects `AiFieldGenerationConfig` instances.  
2. `AiFieldGenerationSelector.selectForFileName()` picks config by file extension.  
3. `AiModelDefinitionSupport` maps `AiModelDefinition` keys to `AiGenerationConfig`.  
4. `AiGenerationProviderFactory.create()` selects provider (JNI or mock).  
5. Provider builds prompt via `AiPromptSupport`, constructs `InferenceParameters`.  
6. Native `LlamaModel.chatCompleteText` executes.  
7. `AiCompletionParser.parseCompletion()` removes internal reasoning blocks.  
8. Final answer returned to caller.

#### Dependencies
- Internal: `AiFieldGenerationConfig`, `AiModelDefinition`, `AiGenerationConfig`, `AiFieldGenerationSelector`, `AiModelDefinitionSupport`, `AiPromptSupport`.  
- External: Lombok annotations, Java Collections (`List`, `Map`, `HashMap`), `org.jspecify.annotations.Nullable`, `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition`, Llama‑Cpp JNI types (`InferenceParameters`, `ModelParameters`, `LlamaModel`).

#### Cross-cutting
- Defensive copying and unmodifiable views for mutable collections.  
- Null handling: empty/`null` extensions treated as fallbacks.  
- Thread safety via local variables; config classes immutable after construction.
