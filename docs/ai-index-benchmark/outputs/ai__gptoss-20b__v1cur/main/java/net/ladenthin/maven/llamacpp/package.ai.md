### main/java/net/ladenthin/maven/llamacpp
- H: 1.0
- C: BB07F829
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:32:14Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [aiindex/](aiindex/package.ai.md)
---
> A Maven plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, handling model configuration, fact extraction, rule routing, and oversize strategies.

#### Purpose
- Configure AI model parameters and routing rules for source‑code generation.
- Evaluate file selection conditions and apply fact extraction.
- Resolve model and fact definitions into concrete runtime configurations.

#### Responsibilities
- **Model configuration** – GGUF settings and runtime parameters.  
- **Fact management** – deterministic fact block definition and extraction.  
- **Condition evaluation** – recursive boolean logic for file selection.  
- **Routing selection** – choose appropriate generation rule per file.  
- **Oversize handling** – strategies for large inputs.  
- **Calibration** – hardware‑specific timing and token‑to‑char ratios.  
- **Context snapshot** – immutable representation of file metadata.

#### Key units
- `AiModelDefinition` – full GGUF model settings.  
- `AiModelDefinitionSupport` – builds `Map<String, AiGenerationConfig>`.  
- `AiGenerationConfig` – mutable runtime parameters for llama‑cpp.  
- `AiFactDefinition` – named group of regex counters.  
- `AiFactDefinitionSupport` – resolves fact keys to counters.  
- `AiFactExtractor` – generates deterministic facts header.  
- `AiCondition` / `AiConditionGroup` – recursive boolean tree for selection.  
- `AiRangeCondition` – numeric bounds for size/lines.  
- `AiConditionEvaluator` – validates and matches conditions.  
- `AiFieldGenerationConfig` – single routing rule (priority, action, oversize, facts).  
- `AiFieldGenerationSelector` – selects applicable rule for a file.  
- `AiOversizeStrategy` – enum of oversize handling options.  
- `AiCalibration` – per‑model timing and token‑to‑char ratios.  
- `AiFileContext` – immutable snapshot of file metadata.  
- `AiGenerationProvider` – interface for text generation.  
- `AiGenerationProviderFactory` – creates mock or JNI providers.  
- `MockAiGenerationProvider` – deterministic mock implementation.  
- `LlamaCppJniAiGenerationProvider` – JNI‑backed provider.  
- `LlamaCppJniConfig` – immutable config holder for llama.cpp runtime options.  
- `AiCompletionParser` – removes thinking blocks from completions.  
- `AiGenerationTimings` – immutable DTO with text, token counts, throughput.

#### Data flow
1. **Configuration** – load `AiModelDefinition` and `AiFactDefinition`; build runtime `AiGenerationConfig` and fact counters.  
2. **Routing** – `AiFieldGenerationSelector` iterates `AiFieldGenerationConfig` rules, using `AiConditionEvaluator` against `AiFileContext`; highest‑priority rule is chosen.  
3. **Fact extraction** – `AiFactExtractor.factsBlock` produces header string.  
4. **Generation** – `AiGenerationProviderFactory` creates provider; provider receives `AiGenerationRequest`, runs chat completion via JNI or mock, parses output with `AiCompletionParser`, records timings, returns `AiGenerationTimings`.

#### Dependencies
- Internal: `AiConditionEvaluator`, `AiFieldGenerationSelector`, `AiFactExtractor`, `AiModelDefinitionSupport`, `AiFactDefinitionSupport`, `AiGenerationProviderFactory`, `AiCompletionParser`.  
- External: Lombok (`@ToString`), jspecify (`@Nullable`), Java 8 time, regex APIs, `net.ladenthin:llama` JNI library.  
- Data sources: Maven plugin configuration XML, source files via `AiFileContext`.

#### Cross-cutting
- Lombok `@ToString` on configuration beans; uninitialized warnings suppressed.  
- Mutable configuration classes are single‑use, not thread‑safe; immutable classes (`LlamaCppJniConfig`, `AiGenerationTimings`) are thread‑safe.  
- Fact extraction is pure and stateless, enabling reuse across threads.  
- Validation logic centralized in `AiConditionEvaluator` and `AiFieldGenerationSelector`.  
- Lazy model loading in `LlamaCppJniAiGenerationProvider`.  
- Mock provider facilitates unit testing without native dependencies.
