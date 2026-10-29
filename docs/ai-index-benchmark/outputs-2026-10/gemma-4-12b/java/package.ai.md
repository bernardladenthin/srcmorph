### java
- H: 1.0
- C: 80B66B6A
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T04:30:06Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides a pluggable infrastructure for configuring and executing AI-driven source code analysis, supporting both local GGUF models via JNI and remote OpenAI-compatible APIs.

#### Purpose
* Provides a unified framework for managing AI generation parameters, model routing, and source code processing strategies.
* Standardizes the execution of LLM inference across local hardware and remote endpoints.

#### Responsibilities
* **Configuration Management**: Handles complex nested condition trees (AND/OR/NOT) for file filtering, model routing rules, and hardware acceleration settings.
* **Source Processing**: Manages strategies for handling oversized files (Sample, Map-Reduce, etc.) and extracts deterministic facts (regex-based counts) to enrich AI prompts.
* **Provider Abstraction**: Defines a unified contract for text generation, including performance timing and response parsing.
* **Local Inference (llama.cpp)**: Manages JNI bindings, GPU/CPU layer offloading, and KV cache configurations for local GGUF models.
* **Remote Inference**: Handles JSON payload construction, endpoint resolution, and security validation for OpenAI-compatible APIs.
* **Response Sanitization**: Parses raw model outputs to strip internal reasoning blocks (e.g., thinking blocks).

#### Key units
* `SrcMorphConfiguration`: Primary JavaBean aggregating project paths, file filters, and model definitions.
* `AiConditionEvaluator`: Recursively evaluates `AiCondition` trees against `AiFileContext` metadata.
* `AiGenerationProvider`: Core interface for unified text generation and performance metrics.
* `LlamaCppJniAiGenerationProvider`: Implementation for local GGUF inference via native JNI bindings.
* `AiGenerationProviderFactory`: Resolves and instantiates providers based on configuration.
* `AiFactExtractor`: Generates formatted "facts" blocks from source text using regex.
* `AiCompletionParser`: Extracts final answers by stripping reasoning markers from completions.
* `LlamaCppJniConfig`: Immutable configuration for hardware offloading and sampling.

#### Data flow
1. `SrcMorphConfiguration` captures raw inputs (Maven/JSON/YAML).
2. `AiConditionEvaluator` filters target files based on configuration rules.
3. `AiFactExtractor` processes selected source text to generate metadata facts.
4. `AiGenerationProvider` receives an `AiGenerationRequest` and executes inference.
5. `AiCompletionParser` cleans the raw output before returning the final string and `AiGenerationTimings`.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`.
* **External**: `llama.cpp` JNI bindings, Jackson (JSON), and `net.ladenthin.llama` models.

#### Cross-cutting
* **Validation**: Extensive `validate()` methods for condition trees and regex patterns.
* **Resource Management**: Providers implement `AutoCloseable` to release native handles or network resources.
* **Thread Safety**: Configuration objects are mutable JavaBeans; evaluators and extractors are stateless and thread-safe.
* **Security**: Enforces HTTPS/loopback checks for API keys in remote provider support.
* **Patterns**: Uses fluent `Builder` patterns for complex hardware configurations and standardized `IOException` for native/network failures.
