### main/java
- H: 1.0
- C: E34B0133
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:38:45Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [net/](net/package.ai.md)
---
> A Maven‑plugin framework that orchestrates llama‑cpp based AI code‑analysis tasks, managing model config, fact extraction, rule routing, and oversize strategies.

#### Purpose
- Configure AI model parameters and routing rules for source‑code generation.  
- Evaluate file selection conditions and apply fact extraction.  
- Resolve model and fact definitions into concrete runtime configurations.

#### Responsibilities
- **Model configuration** – GGUF settings, runtime parameters, and calibration.  
- **Fact management** – deterministic fact block definition, extraction, and header generation.  
- **Condition evaluation** – recursive boolean logic for file selection.  
- **Routing selection** – choosing the appropriate generation rule per file.  
- **Oversize handling** – strategies for large inputs.  
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
1. Load `AiModelDefinition` and `AiFactDefinition`; build `AiGenerationConfig` and fact counters.  
2. `AiFieldGenerationSelector` evaluates `AiConditionEvaluator` against `AiFileContext` to pick a routing rule.  
3. `AiFactExtractor.factsBlock` creates the fact header string.  
4. `AiGenerationProviderFactory` creates a provider; provider runs a chat completion via JNI or mock, parses output with `AiCompletionParser`, records timings, and returns `AiGenerationTimings`.

#### Dependencies
- Internal: `AiConditionEvaluator`, `AiFieldGenerationSelector`, `AiFactExtractor`, `AiModelDefinitionSupport`, `AiFactDefinitionSupport`, `AiGenerationProviderFactory`, `AiCompletionParser`.  
- External: Lombok (`@ToString`), jspecify (`@Nullable`), Java 8 time, regex APIs, `net.ladenthin:llama` JNI library.  
- Configuration sources: Maven plugin XML, source files via `AiFileContext`.

#### Cross-cutting
- Lombok `@ToString` on configuration beans; mutable configuration classes are single‑use, not thread‑safe; immutable classes (`LlamaCppJniConfig`, `AiGenerationTimings`) are thread‑safe.  
- Fact extraction is pure and stateless, enabling reuse across threads.  
- Validation logic centralized in `AiConditionEvaluator` and `AiFieldGenerationSelector`.  
- Lazy model loading in `LlamaCppJniAiGenerationProvider`.  
- Mock provider facilitates unit testing without native dependencies.
