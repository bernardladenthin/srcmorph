# TODO — llamacpp-ai-index-maven-plugin

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
plugin-specific or this repo's slice of a cross-cutting initiative. Completed work is
recorded in git history and `crossrepostatus.md`, not here.

## Open

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory.

- **`@VisibleForTesting` audit.** No usages currently. Walk the production tree for package-private/protected methods or fields that exist purely so tests can reach them, and either annotate (`com.google.common.annotations.VisibleForTesting`) or move into the test source tree.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode (see `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Expand PIT mutation scope (optional).** `pom.xml` wires `<mutationThreshold>100</mutationThreshold>` over an explicit `<targetClasses>` list (config / document / prompt / provider / support value+logic classes, plus `indexer.AiInputWindowCalculator` and `support.AiProgressBar`), all killed at 100%. Still out (optional, need careful fixtures): `document.AiMdDocumentCodec` / `AiMdHeaderCodec`, `prompt.AiPromptPreparationSupport`, and the newer `indexer.AiIndexPlan` / `config.AiConditionGroup`. The orchestration layers (`indexer.*` walk, `mojo.*`) and the JNI provider stay out of PIT — they need a Maven/native context rather than pure-unit mutation (see crossrepostatus "Deliberate non-parity").

- **Files beyond the big-window preset — handled via `<onOversize>`.** The `granite-4.0-h-tiny-bigwindow` preset (384K context) covers source files up to ~1 MB. Larger files (or any file over its routed model's window) are handled per rule by `<onOversize>`: `fail` *(default — hard abort)*, `sample` (head only), `mapReduce` (chunk at line boundaries + combine, `<maxChunks>`-capped), or `deterministic` (model-free metadata body). See `AiOversizeStrategy` / `AiSourceChunker` / `AiDeterministicSummary` and the README context-window note. Optional follow-up: a real end-to-end Granite `mapReduce` smoke test (orchestration is unit-tested + planOnly-verified today), and a still-larger 512K preset is wired in the POM only as an unused example.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.

- **No LogCaptor smoke test needed (note, not an action).** This module has no logging code (`org.slf4j.*` not used in `src/main/java/`); production uses Maven's `Log` interface. If SLF4J logging is ever introduced, add a LogCaptor smoke test at the same time so the binding/configuration is exercised in tests.
