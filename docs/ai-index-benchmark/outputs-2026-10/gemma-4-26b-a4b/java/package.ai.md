### java
- H: 1.0
- C: C3D438BB
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T14:04:20Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Provides a configuration and execution framework for AI-driven source code summarization, managing file filtering, structural fact extraction, and pluggable AI model backends.

#### Purpose
* Centralizes engine settings and file processing logic to bridge external configurations (Maven, JSON, YAML) with AI inference.
* Abstracts text generation through a unified provider interface supporting both local JNI-based models and OpenAI-compatible APIs.

#### Responsibilities
* **Configuration & Filtering**: Manages global engine parameters, hierarchical file matching via logical condition trees, and strategies for handling files exceeding model context limits.
* **Metadata Extraction**: Performs deterministic, regex-based counting of code elements to supplement AI-generated summaries with structural facts.
* **AI Provider Management**: Orchestrates the lifecycle of various backends, including local GGUF model management via JNI and remote API communication.
* **Response Processing**: Handles the parsing of raw model outputs, specifically extracting clean text by stripping internal chain-of-thought reasoning blocks.

#### Key units
* `SrcMorphConfiguration`: The primary mutable JavaBean holding all global engine and plugin parameters.
* `AiConditionEvaluator`: A stateless engine that evaluates `AiCondition` trees against `AiFileContext` metadata.
* `AiFactExtractor`: A utility for generating deterministic text blocks of regex-based counts (e.g., function counts) to ground AI output.
* `AiGenerationProvider`: The core interface for generating text and performance metrics via `generate` and `generateWithTimings`.
* `AiGenerationProviderFactory`: A factory that resolves the effective provider (Mock, LlamaCppJNI, or OpenAI) based on settings.
* `LlamaCppJniAiGenerationProvider`: A JNI-based implementation that manages a native `LlamaModel` lifecycle.
* `AiCompletionParser`: A utility to extract clean answers by removing content between reasoning markers.
* `AiOversizeStrategy`: An enum defining fallback behaviors (Fail, Sample, Map-Reduce, Deterministic) for oversized files.

#### Data flow
1. **Setup**: `SrcMorphConfiguration` is populated via external binders; `AiGenerationProvider` is instantiated via `AiGenerationProviderFactory`.
2. **Selection**: `AiConditionEvaluator` uses configured filters to identify target files within an `AiFileContext`.
3. **Augmentation**: `AiFactExtractor` scans matched source files to produce deterministic metadata.
4. **Inference**: An `AiGenerationRequest` is sent to the provider; local requests map to native `ModelParameters`, while API requests are serialized via `OpenAiCompatibleProviderSupport`.
5. **Refinement**: Raw outputs are processed by `AiCompletionParser` to remove reasoning blocks, returning cleaned text and `AiGenerationTimings`.

#### Dependencies
* **Internal**: `AiCondition`, `AiFileContext`, `AiPromptDefinition`, `AiPromptSupport`, `AiGenerationConfig`.
* **Native**: `net.ladenthin.llama` (JNI bindings for `LlamaModel` and `ModelParameters`).
* **External**: Jackson (JSON serialization), `lombok` (data modeling), `java.util.regex` (pattern matching).

#### Cross-cutting
* **Immutability & Thread Safety**: Configuration objects like `LlamaCppJniConfig` are immutable; utility engines like `AiConditionEvaluator` are stateless.
* **Resource Management**: Providers implement `AutoCloseable` to manage native JNI handles and network resources.
* **Error Handling**: Includes validation for malformed configuration trees, regex errors, and handling of truncated model responses (missing reasoning markers).
* **Performance Monitoring**: Standardized use of `AiGenerationTimings` to track token counts and throughput across all providers.
