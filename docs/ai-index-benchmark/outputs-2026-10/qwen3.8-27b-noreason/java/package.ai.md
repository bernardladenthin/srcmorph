### java
- H: 1.0
- C: FDEB87CD
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T10:00:07Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Pluggable AI text-generation infrastructure for source-code indexing, providing configuration management, file-exclusion logic, deterministic fact extraction, and multi-backend LLM inference (local llama.cpp JNI and remote OpenAI-compatible APIs).

#### Purpose
- Centralizes configuration, validation, and routing logic for the `srcmorph` AI code-indexing pipeline.
- Abstracts LLM inference backends to support local native (JNI) and remote HTTP-based generation with unified response parsing and timing metrics.

#### Responsibilities
- **Configuration & Validation**: Manages mutable run-state (`SrcMorphConfiguration`) and immutable inference parameters (`AiGenerationConfig`, `LlamaCppJniConfig`), including binding from Maven/JSON and applying tuned defaults.
- **File Filtering & Fact Extraction**: Evaluates recursive condition trees (`AiConditionEvaluator`) for file inclusion/exclusion and computes deterministic regex-based facts (`AiFactExtractor`) to supplement AI outputs.
- **Provider Abstraction & Factory**: Defines the `AiGenerationProvider` contract and resolves/instantiates specific backends (`mock`, `llamacpp-jni`, `openai-compatible`) via `AiGenerationProviderFactory`.
- **Inference Execution**: Executes local GGUF model inference via JNI (`LlamaCppJniAiGenerationProvider`) or constructs/validates remote JSON payloads for OpenAI-compatible endpoints (`OpenAiCompatibleProviderSupport`).
- **Post-Processing**: Sanitizes raw LLM outputs by stripping chain-of-thought blocks (`AiCompletionParser`) and calculates generation timings.

#### Key units
- `SrcMorphConfiguration`: Central mutable JavaBean for source scope, provider routing, and inference tuning.
- `AiGenerationConfig`: Data carrier for 42+ inference parameters (sampling, KV cache, GPU offload) with tuned defaults.
- `AiConditionEvaluator`: Stateless utility to match/validate AI file-exclusion condition trees against file contexts.
- `AiFactExtractor`: Static utility generating deterministic regex match count blocks from source text.
- `AiGenerationProvider`: Interface for `generate`/`generateWithTimings`, extending `AutoCloseable`.
- `AiGenerationProviderFactory`: Resolves provider keys and instantiates the correct backend implementation.
- `LlamaCppJniAiGenerationProvider`: Final class for local inference using `net.ladenthin.llama` JNI bindings with lazy model initialization.
- `LlamaCppJniConfig`: Immutable builder-based configuration holder for local inference parameters.
- `OpenAiCompatibleProviderSupport`: Helper for remote request serialization, URL validation, and security enforcement.
- `AiCompletionParser`: Utility to remove thinking-block markers from raw LLM responses.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` and `AiGenerationConfig` are populated via setters and passed to the execution engine.
2. **Filtering**: `AiConditionEvaluator` consumes condition trees and `AiFileContext` to determine file inclusion; `AiFactExtractor` generates deterministic facts prepended to prompts.
3. **Provider Resolution**: `AiGenerationProviderFactory` resolves the active provider key and instantiates the backend.
4. **Inference**:
   - **Local**: `LlamaCppJniAiGenerationProvider` initializes `LlamaModel`, executes `chatComplete`, and parses `ChatResponse`.
   - **Remote**: `OpenAiCompatibleProviderSupport` builds JSON payloads and handles HTTP transmission.
5. **Post-Processing**: `AiCompletionParser` strips thinking blocks from raw output.
6. **Output**: Returns clean text and `AiGenerationTimings` to the caller.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config` (conditions, facts, prompts), `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`.
- **External**:
  - `net.ladenthin.llama` (JNI library for local inference).
  - `com.fasterxml.jackson.databind` (JSON serialization for remote requests).
  - `lombok` (code generation).
  - `org.jspecify.annotations` (nullability).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive null-checks.
- **Immutability vs. Mutability**: Configuration classes like `LlamaCppJniConfig` are immutable; `SrcMorphConfiguration` is a mutable JavaBean intended to be read-only during execution.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable`; local providers explicitly manage native `LlamaModel` lifecycle.
- **Security**: Remote providers enforce that API keys are not sent over plain HTTP to non-loopback hosts.
- **Error Handling**: `IllegalArgumentException` for configuration/validation errors; `IOException` for provider failures and security violations.
- **Concurrency**: Local JNI provider is not thread-safe (lazy init); remote support and evaluators are thread-safe/stateless.
