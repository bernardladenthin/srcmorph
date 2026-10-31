### java
- H: 1.0
- C: 603ABF3C
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:46:17Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A configurable AI‑powered source‑code transformation engine that evaluates file‑selection rules, extracts language‑agnostic facts, and orchestrates model‑based generation for documentation or refactoring.

#### Purpose
- Bind runtime settings from Maven, JSON/YAML, or Java code into a single mutable configuration.  
- Evaluate logical file‑selection conditions and determine if file contents must be read.  
- Extract deterministic fact counts from source files for AI prompts.  
- Define how oversized source files are handled during routing.  
- Provide a comprehensive holder for AI generation parameters.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all srcmorph options, including file filters, AI provider settings, and model paths.  
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees against file metadata.  
- **Fact Extraction** – `AiFactExtractor` builds fact blocks from regex counters and validates counter definitions.  
- **Oversize Handling** – `AiOversizeStrategy` enumerates strategies for routing oversized files.  
- **Generation Parameter Storage** – `AiGenerationConfig` holds detailed AI model and prompt settings for a single generation run.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for all srcmorph settings.  
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.  
- `AiConditionGroup` – container for child conditions in `<and>`/`<or>` combinators.  
- `AiFactExtractor` – static utility for building fact blocks from `AiFactCounter` lists.  
- `AiOversizeStrategy` – enum mapping config tokens to oversize handling policies.  
- `AiGenerationConfig` – mutable holder for AI generation parameters (model path, context size, temperature, cache, etc.).  
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.  
- `AiGenerationProviderFactory` – `effectiveProvider`, `create`.  
- `LlamaCppJniConfig` – immutable record of llama.cpp settings.  
- `LlamaCppJniAiGenerationProvider` – lazy model loading, chat completion, timing extraction.  
- `OpenAiCompatibleProviderSupport` – JSON request construction, endpoint resolution, API key validation, timing calculation.  
- `AiCompletionParser` – cleans raw LLM completions.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or Java code.  
2. **File Selection** – For each source file, `AiConditionEvaluator` checks the configured `AiCondition` tree; `usesLines` determines if the file must be read.  
3. **Fact Extraction** – If required, `AiFactExtractor.factsBlock` generates a facts string from `AiFactCounter` definitions.  
4. **Routing Decision** – Oversized files are handled per `AiOversizeStrategy`; routing rules from `AiFieldGenerationConfig` are applied.  
5. **Generation** – `AiGenerationProviderFactory` resolves the provider key and creates a concrete provider; the provider builds the request (via `OpenAiCompatibleProviderSupport` or llama.cpp chat API), sends it, receives a JSON response, parses the completion, extracts timings, and returns `AiGenerationTimings` or plain `String` to the caller.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.  
- **External** – `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (model, parameters, JSON parser), `com.fasterxml.jackson` (JSON), `lombok`.  
- **Runtime** – llama.cpp native library, OpenAI‑compatible HTTP endpoint, environment variables for API keys.

#### Cross-cutting
- **Thread safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; providers are stateless or lazily initialized; the factory is thread‑safe.  
- **Immutability** – `LlamaCppJniConfig` is a record‑style immutable holder; providers expose read‑only state.  
- **Error handling** – `IOException` for I/O or configuration errors; `IllegalArgumentException` for unknown provider keys or malformed configurations.  
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce well‑formed configurations.  
- **Configuration patterns** – Builder pattern for `LlamaCppJniConfig`; factory method for provider creation; helper class (`OpenAiCompatibleProviderSupport`) encapsulates request/endpoint logic.
