### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:15:38Z
- G: 
- A: 0.0.0
- X: package
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [SrcMorphConfiguration.java](SrcMorphConfiguration.java.ai.md)
---
> A configuration‑driven source‑code morphing engine that evaluates file‑level AI conditions, extracts deterministic facts, and orchestrates AI generation for code transformation.

#### Purpose
- Central configuration holder for srcmorph runs.
- Evaluates AI‑condition trees against file metadata.
- Extracts deterministic language‑agnostic facts for AI summaries.
- Configures AI generation parameters and oversize handling.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` exposes mutable properties for Maven, JSON/YAML, or code binding.
- **Condition Evaluation** – `AiConditionEvaluator` validates and matches `AiCondition` trees against `AiFileContext`.
- **Fact Extraction** – `AiFactExtractor` builds a facts block from regex counters.
- **AI Generation Settings** – `AiGenerationConfig` stores all model, inference, and DRY parameters.
- **Oversize Strategy** – `AiOversizeStrategy` defines handling for files exceeding context windows.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for run‑time settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions in logical combinators.
- `AiFactExtractor` – utility for deterministic fact counting.
- `AiGenerationConfig` – mutable bean for AI model and inference parameters.
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (Maven/JSON/YAML).
2. **File Selection**: Files are filtered by extensions, size bounds, and exclude globs.
3. **Condition Evaluation**: For each file, `AiConditionEvaluator.matches` checks `AiCondition` trees; `usesLines` determines if source text is needed.
4. **Fact Extraction** (if required): `AiFactExtractor.factsBlock` generates a facts header.
5. **AI Generation**: `AiGenerationConfig` supplies model, prompt, and sampling parameters to the AI provider.
6. **Oversize Handling**: `AiOversizeStrategy` dictates whether to fail, sample, map‑reduce, or deterministically split oversized files.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: Java standard libraries (`java.time`, `java.util`, `java.io`, `java.util.regex`), Lombok (`@ToString`), JSPECIFY annotations, and the srcmorph support package.

#### Cross‑cutting
- **Thread Safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; configuration beans are not synchronized.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on misconfiguration.
- **Immutable Enums**: `AiOversizeStrategy` is a thread‑safe enum with a `fromConfig` parser.
- **Configuration Defaults**: `AiGenerationConfig` and `SrcMorphConfiguration` provide sensible defaults and guard against `null` inputs.

---
