### java
- H: 1.0
- C: 2170A0D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:50:11Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Defines the configuration model, condition evaluation, fact extraction, and pluggable AI generation provider abstraction (local llama.cpp JNI and remote OpenAI-compatible) for the srcmorph source code summarization and indexing pipeline.

#### Purpose
- Establishes the data structures, validation logic, and SPI contracts required to configure, filter, and process source files for AI-assisted summarization.
- Bridges Maven plugin configuration to concrete AI inference backends, managing the lifecycle from parameter binding to response parsing and timing metrics.

#### Responsibilities
- **Configuration & Validation**: Manages mutable JavaBean state for global run settings (`SrcMorphConfiguration`) and per-model inference parameters (`AiGenerationConfig`), including recursive evaluation of `AiCondition` trees for file filtering.
- **Fact Extraction**: Computes deterministic regex-based match counts from source text to generate exact "facts" blocks for AI prompts via `AiFactExtractor`.
- **Provider Abstraction & Factory**: Defines the `AiGenerationProvider` SPI and implements `AiGenerationProviderFactory` to resolve and instantiate backends (Mock, LlamaCppJni, OpenAI-compatible) based on configuration.
- **Inference Execution**: Handles local native inference via `LlamaCppJniAiGenerationProvider` (lazy GGUF model loading, JNI bindings) and remote API calls via `OpenAiCompatibleProviderSupport` (JSON payload construction, secure API key resolution).
- **Post-processing**: Strips chain-of-thought reasoning blocks from raw LLM output using `AiCompletionParser` and extracts timing metrics.

#### Key units
- `SrcMorphConfiguration`: Central mutable bean binding source filters, provider settings, and prompt/model definitions for the entire run.
- `AiGenerationConfig`: Mutable JavaBean carrying 40+ inference parameters (sampling, KV cache, GPU offload) for a single generation step.
- `AiConditionEvaluator`: Stateless utility that matches `AiCondition` trees against `AiFileContext` metadata to filter source files.
- `AiFactExtractor`: Static utility generating deterministic label-count pairs from `AiFactCounter` regex definitions.
- `AiOversizeStrategy`: Enum defining handling strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for files exceeding context limits.
- `AiGenerationProvider`: SPI interface for AI backends defining `generate` and `generateWithTimings` contracts, extending `AutoCloseable`.
- `AiGenerationProviderFactory`: Factory resolving effective provider keys and instantiating the correct implementation.
- `LlamaCppJniAiGenerationProvider`: Final class managing local inference, `LlamaModel` lifecycle, and native resource cleanup.
- `LlamaCppJniConfig`: Immutable value object (Builder pattern) encapsulating 41 parameters for local inference.
- `OpenAiCompatibleProviderSupport`: Package-private helper for constructing JSON payloads, resolving secure API keys, and calculating remote timings.
- `AiCompletionParser`: Utility stripping Gemma-4 chain-of-thought blocks from raw LLM output.

#### Data flow
- **Configuration**: Maven plugin or JSON/YAML input populates `SrcMorphConfiguration` and `AiGenerationConfig` via JavaBean setters.
- **Filtering**: `AiConditionEvaluator` consumes `AiCondition` trees and `AiFileContext` records to filter source files; `AiFactExtractor` processes raw source strings to produce deterministic facts.
- **Provider Initialization**: `AiGenerationProviderFactory` resolves the provider key from `LlamaCppJniConfig` and run configuration, instantiating the appropriate `AiGenerationProvider`.
- **Inference**: Providers receive `AiGenerationRequest`; local providers map config to `ModelParameters` and execute JNI calls, while remote providers build JSON payloads and validate endpoints.
- **Post-processing**: Raw response text is passed to `AiCompletionParser` to strip thinking blocks; timing data is extracted and wrapped in `AiGenerationTimings` before returning cleaned text to the caller.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config` (AiCondition, AiFileContext, AiFactCounter, AiPromptDefinition), `net.ladenthin.srcmorph.document` (AiGenerationRequest), `net.ladenthin.srcmorph.prompt` (AiPromptSupport), `net.ladenthin.srcmorph.support` (AiSourceExcludeFilter, Java8CompatibilityHelper).
- **External**: `net.ladenthin.llama` (JNI bindings: LlamaModel, ModelParameters, ChatResponse), `com.fasterxml.jackson.databind` (JSON serialization), `lombok`, `org.jspecify.annotations`.

#### Cross-cutting
- **Null Safety & Validation**: Extensive use of `@Nullable` annotations; fail-fast validation via `IllegalArgumentException` for invalid condition trees, regex patterns, or config tokens.
- **Immutability & Thread Safety**: `LlamaCppJniConfig` and utility classes (`AiConditionEvaluator`, `AiFactExtractor`) are immutable/stateless and thread-safe; configuration beans are mutable and intended to be read-only during execution.
- **Resource Management**: All providers extend `AutoCloseable` to ensure native handles or network resources are released.
- **Error Handling**: `IOException` for inference failures, token budget exhaustion, and security violations; `IllegalArgumentException` for invalid provider keys or endpoint schemes.
- **Maven Binding**: Classes structured as mutable JavaBeans with defensive copying in setters to support Maven plugin XML binding and Jackson/YAML deserialization.
