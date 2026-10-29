### java
- H: 1.0
- C: BBEDBCD3
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:15:24Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package enables AI-driven source code analysis and transformation by evaluating file conditions, extracting deterministic facts, configuring generation parameters, and handling large files through intelligent oversizing strategies—supporting both local and remote AI models with clean, production-ready output.

#### Purpose
- Enables AI-powered source code transformation through conditional file filtering, deterministic fact extraction, and intelligent handling of large files  
- Provides pluggable AI generation backends (local models, OpenAI-compatible APIs) with consistent response parsing and timing metrics  

#### Responsibilities
- Evaluate file eligibility using AI condition trees and metadata-based rules  
- Extract exact, deterministic code facts via regex matching across full source files  
- Configure and manage AI generation parameters including context size, sampling, threading, and caching  
- Handle oversized files using configurable strategies (trim, sample, map-reduce, deterministic output)  
- Instantiate and route AI generation requests to appropriate providers (local or remote)  
- Parse raw model responses to extract clean final outputs by removing internal reasoning  

#### Key units
- **AiConditionEvaluator**: Evaluates file conditions against metadata and content to determine matches and detect line count needs  
- **AiConditionGroup**: Groups multiple AI conditions using AND/OR logic for complex configuration binding  
- **AiFactExtractor**: Generates deterministic, exact counts of code elements via regex matching across full source files  
- **AiGenerationConfig**: Central mutable configuration for AI model parameters including context, sampling, threading, and caching  
- **AiOversizeStrategy**: Defines how to handle files larger than model context (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC)  
- **SrcMorphConfiguration**: Root configuration object for AI source transformation, managing paths, exclusions, AI providers, and generation rules  
- **AiGenerationProvider**: Interface defining generation and timing capabilities  
- **AiGenerationProviderFactory**: Selects and creates provider instances based on configuration  
- **LlamaCppJniAiGenerationProvider**: Executes local GGUF models via llama.cpp JNI with full timing  
- **LlamaCppJniConfig**: Immutable configuration for model parameters, GPU, sampling, and cache policies  
- **AiCompletionParser**: Strips internal reasoning from raw model responses to extract final answer  

#### Data flow
- File metadata is evaluated by **AiConditionEvaluator** against condition trees (built from **AiConditionGroup**) to determine eligibility  
- Eligible files are processed by **AiFactExtractor** to compute exact regex-based fact counts from full source content  
- Generation parameters are pulled from **AiGenerationConfig** and applied via **SrcMorphConfiguration** to drive AI model behavior  
- If a file exceeds context size, **AiOversizeStrategy** determines the handling path (e.g., trim or sample) before processing  
- Generation request is routed via **AiGenerationProviderFactory** to either **LlamaCppJniAiGenerationProvider** or **OpenAiCompatibleProviderSupport**  
- Raw response is parsed by **AiCompletionParser** to extract final output and timing metrics  

#### Dependencies
- Internal: AiCondition, AiFileContext, AiRangeCondition, AiPromptDefinition, AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition  
- External: java.util.List, java.io.File, java.time.Instant, java.util.regex.Pattern, java.util.regex.Matcher, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.prompt.AiPromptSupport, com.fasterxml.jackson.databind.ObjectMapper, java.net.URL, net.ladenthin.llama.value.ChatResponse, Usage, java.util.Collections, java.util.Objects  

#### Cross-cutting
- Immutable design: all configuration and response objects are immutable; no shared mutable state  
- Null safety: all inputs are validated or treated as empty; outputs are never null  
- Defensive copying: collections (e.g., in AiConditionGroup) are copied to prevent external mutation  
- Configuration validation at build time (e.g., regex patterns, date parsing, condition structure)  
- Error handling focused on configuration validity and invalid inputs (e.g., invalid regex, malformed date)  
- Concurrency: thread-safe via immutability and lazy initialization  
- Reasoning extraction: all model responses are processed to remove internal thinking blocks before storage or use
