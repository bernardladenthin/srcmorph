### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: E77032F7
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:24:45Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven plugin that indexes source files for AI‑generation, routing each file to a suitable LlamaCPP model based on declarative rules and extracting deterministic facts to drive prompt construction.

#### Purpose
- Parse Maven configuration into AI‑generation rules, facts, and model settings.
- Evaluate source files against these rules to select a LlamaCPP provider.
- Invoke the provider, capture timing metrics, and return cleaned AI completions.

#### Responsibilities
- **Configuration handling** – read and validate XML into immutable POJOs (`AiFieldGenerationConfig`, `AiFactDefinition`, `AiModelDefinition`).
- **Rule evaluation** – match files to rules using predicate trees (`AiCondition`, `AiRangeCondition`) and selector logic (`AiFieldGenerationSelector`).
- **Fact extraction** – generate deterministic `label: count` blocks from source content (`AiFactExtractor`).
- **Provider orchestration** – instantiate the correct AI backend (`AiGenerationProviderFactory`) and expose a uniform generation API.
- **JNI integration** – load and run a GGUF model via `LlamaCppJniAiGenerationProvider`, parse raw completions, and record per‑token throughput.
- **Mocking & calibration** – provide a deterministic mock provider for tests and expose timing data (`AiGenerationTimings`) for calibration.

#### Key units
- **`AiFieldGenerationConfig`** – rule definition: condition, model key, prompt key, priority, fallbacks, oversize strategy, fact counters.
- **`AiConditionEvaluator`** – validates and evaluates condition trees against `AiFileContext`.
- **`AiFactExtractor`** – produces fact blocks from regex counters in a file.
- **`AiModelDefinitionSupport`** – maps model keys to fully‑configured `AiGenerationConfig`.
- **`AiGenerationProviderFactory`** – builds `AiGenerationProvider` instances (`mock`, `llamacpp-jni`).
- **`LlamaCppJniAiGenerationProvider`** – JNI‑backed provider that loads `LlamaModel`, runs `chatCompleteText`, and parses responses.
- **`MockAiGenerationProvider`** – deterministic summary generator with synthetic timings.
- **`AiCompletionParser`** – removes internal chain‑of‑thought blocks from Gemma‑4 completions.
- **`AiGenerationTimings`** – immutable holder of generated text, token counts, and throughput metrics.

#### Data flow
1. **Configuration**: Maven XML → `AiFieldGenerationConfig`, `AiFactDefinition`, `AiModelDefinition`.
2. **Setup**: `AiFactDefinitionSupport` injects fact counters into rules; `AiModelDefinitionSupport` creates `AiGenerationConfig` instances.
3. **Runtime**: For each source file,
   - Build `AiFileContext`.
   - `AiFieldGenerationSelector` uses `AiConditionEvaluator` to pick the best rule.
   - If facts are needed, `AiFactExtractor` generates the fact block.
   - `AiGenerationProviderFactory` supplies the appropriate provider.
   - Provider runs inference, `AiCompletionParser` cleans the output, and `AiGenerationTimings` records metrics.
4. **Result**: Cleaned AI completion returned to the plugin; timings optionally used for calibration.

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `ConvertToRecord`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`.
- **External**: Maven plugin framework, native `net.ladenthin.llama` bindings (`LlamaModel`, `InferenceParameters`, `ChatResponseParser`), Java standard libraries (`java.time`, `java.util.regex`, `java.nio.file.Path`).

#### Cross-cutting
- **Immutability & thread safety** – configuration POJOs and timing records are immutable; provider factories are stateless.
- **Validation & error handling** – centralized in `AiConditionEvaluator` and `AiFieldGenerationSelector`; `AiCompletionParser` throws `IOException` for malformed responses.
- **Configuration normalization** – `LlamaCppJniConfig` turns nullable lists into immutable empty lists; all fields are final.
- **Consistent response parsing** – `AiCompletionParser` ensures all providers return the same cleaned text format.
