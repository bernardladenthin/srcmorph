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

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.

- **SLF4J gap CLOSED.** The four `indexer.*` classes (`SourceFileIndexer`, `PackageIndexer`, `ProjectIndexer`, `AiFieldGenerationSupport`) now log via `org.slf4j.Logger` instead of the constructor-injected Maven `Log`; only the 5 mojos (and the `PluginArchitectureTest` `mavenMojoAnnotationsConfinedToMojo`/`nonMojoIsMavenFree` rules) still touch the Maven Plugin API. Maven's `maven-slf4j-provider` binds `slf4j-api` inside plugin executions, so every `LOGGER.info(...)`/`.warn(...)`/`.debug(...)` call surfaces as a normal `[INFO]`/`[WARN]`/`[DEBUG]` line in `mvn` output with zero glue (verified end-to-end with the `generate` goal against a scratch project). `AiFieldGenerationSupportTest` exercises the binding/configuration directly via a logback `ListAppender` attached to the class logger (the LogCaptor-smoke-test role this note used to flag as future work).
