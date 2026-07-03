### main/java/net/ladenthin
- H: 1.0
- C: DCA70847
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:35:39Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [maven/](maven/package.ai.md)
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
- `AiModelDefinition`, `AiModelDefinitionSupport` – GGUF model settings and builder.  
- `AiGenerationConfig`, `AiGenerationProvider`, `AiGenerationProviderFactory` – runtime generation parameters and provider creation.  
- `AiFactDefinition`, `AiFactDefinitionSupport`, `AiFactExtractor` – named regex counter groups and fact header generation.  
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiConditionEvaluator` – recursive boolean tree for file selection.  
- `AiFieldGenerationConfig`, `AiFieldGenerationSelector` – routing rule definition and rule selection.  
- `AiOversizeStrategy` – enum of oversize handling options.  
- `AiCalibration` – per‑model timing and token‑to‑char ratios.  
- `AiFileContext` – immutable snapshot of file metadata.  
- `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig` – JNI‑backed provider and immutable runtime config.  
- `MockAiGenerationProvider` – deterministic mock implementation.  
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
