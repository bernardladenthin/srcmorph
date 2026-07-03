### main/java/net
- H: 1.0
- C: FAF52897
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:31:48Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [ladenthin/](ladenthin/package.ai.md)
---
> A Maven plugin that selects a suitable LlamaCPP model for each source file, runs inference via JNI, parses the completion, and records timing metrics for AI‑based code insights.

#### Purpose
- Transform Maven XML rules into immutable configuration objects.
- Evaluate source files against these rules to determine the appropriate AI model.
- Execute model inference through JNI, clean the output, and capture performance data.

#### Responsibilities
- **Configuration** – parse XML into `AiFieldGenerationConfig`, `AiFactDefinition`, `AiModelDefinition`.
- **Rule evaluation** – tree‑based conditions (`AiCondition`, `AiRangeCondition`) and selector (`AiFieldGenerationSelector`).
- **Fact extraction** – produce deterministic `label: count` blocks via `AiFactExtractor`.
- **Provider orchestration** – factory (`AiGenerationProviderFactory`) creates mock or JNI providers.
- **JNI integration** – `LlamaCppJniAiGenerationProvider` loads `LlamaModel`, calls `chatCompleteText`, parses responses, and records throughput.
- **Mocking & calibration** – deterministic `MockAiGenerationProvider` and timing holder (`AiGenerationTimings`).

#### Key units
- `AiFieldGenerationConfig` – rule definition with condition, model key, prompt key, priority, fallbacks, oversize strategy, fact counters.
- `AiConditionEvaluator` – validates and evaluates condition trees against `AiFileContext`.
- `AiFactExtractor` – generates fact blocks from regex counters in a file.
- `AiModelDefinitionSupport` – maps model keys to fully‑configured `AiGenerationConfig`.
- `AiGenerationProviderFactory` – builds `AiGenerationProvider` instances (`mock`, `llamacpp-jni`).
- `LlamaCppJniAiGenerationProvider` – JNI‑backed provider that loads `LlamaModel`, runs `chatCompleteText`, parses responses, and records per‑token throughput.
- `MockAiGenerationProvider` – deterministic summary generator with synthetic timings.
- `AiCompletionParser` – removes internal chain‑of‑thought blocks from Gemma‑4 completions.
- `AiGenerationTimings` – immutable holder of generated text, token counts, and throughput metrics.

#### Data flow
1. **Configuration** – Maven XML → `AiFieldGenerationConfig`, `AiFactDefinition`, `AiModelDefinition`.
2. **Setup** – Fact counters injected via `AiFactDefinitionSupport`; model configs built by `AiModelDefinitionSupport`.
3. **Runtime per file**  
   - Build `AiFileContext`.  
   - `AiFieldGenerationSelector` + `AiConditionEvaluator` pick the best rule.  
   - If facts required, `AiFactExtractor` generates fact block.  
   - `AiGenerationProviderFactory` supplies the appropriate provider.  
   - Provider runs inference → raw completion.  
   - `AiCompletionParser` cleans output.  
   - `AiGenerationTimings` records metrics.  
4. **Result** – Cleaned AI completion returned to plugin; timings optionally used for calibration.

#### Dependencies
- Internal: `AiPromptSupport`, `Java8CompatibilityHelper`, `ConvertToRecord`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`.
- External: Maven plugin framework; native `net.ladenthin.llama` bindings (`LlamaModel`, `InferenceParameters`, `ChatResponseParser`); Java standard libraries (`java.time`, `java.util.regex`, `java.nio.file.Path`).

#### Cross-cutting
- **Immutability & thread safety** – configuration POJOs and timing records are immutable; provider factories are stateless.
- **Validation & error handling** – centralized in `AiConditionEvaluator` and `AiFieldGenerationSelector`; `AiCompletionParser` throws `IOException` for malformed responses.
- **Configuration normalization** – `LlamaCppJniConfig` turns nullable lists into immutable empty lists; all fields are final.
- **Consistent response parsing** – `AiCompletionParser` ensures all providers return the same cleaned text format.
