# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative. Completed work is
recorded in git history and `crossrepostatus.md`, not here.

## Open

- **Migration step 8 — CI full pass + first reactor release.** `.github/workflows/publish.yml`
  is now adapted to the 3-module reactor (module names as of that step; the plugin module has
  since been renamed `srcmorph-maven-plugin` in step 9 below, and CI was updated to match): the
  `build` job uploads jars from all three modules
  (`srcmorph` / `srcmorph-cli`, including its `jar-with-dependencies` fat jar / the plugin module);
  crash-dump globs are repo-wide (`**/hs_err_pid*.log` etc., since a forked surefire JVM can crash
  in any module's own working directory); the PIT step is scoped to `-pl srcmorph -am` (the only
  module with a `pitest-maven` execution) with its report glob at `srcmorph/target/pit-reports/**`;
  the `vmlens` job is scoped to `-pl srcmorph-maven-plugin -am` (where
  `VmlensInterleavingSmokeTest` and the `vmlens` profile actually live — not relocated during the
  core extraction) and additionally passes `-Dsurefire.failIfNoSpecifiedTests=false` (the
  `-DfailIfNoTests=false` flag alone does not suppress the "-Dtest pattern matched nothing"
  failure that `-am` now triggers in the upstream `srcmorph` module); jdeps prints a graph per
  module; and Coveralls/Codecov are pointed at `srcmorph`'s jacoco report only (the
  single-primary-module precedent already used by the sibling java-llama.cpp reactor — `srcmorph-cli`
  and the plugin module's own coverage is not currently aggregated or uploaded). `mvn -q clean
  verify` and a `-P release verify -DskipTests -Dgpg.skip=true` dry run (package/sources/javadoc
  jars for all three modules + the CLI fat jar, no `.asc` files since signing was skipped) both
  pass locally. **Still open: actually cutting the first real `1.1.1` release** (tag + `mvn -P
  release deploy` with real credentials) — that action was deliberately left to the user, not
  performed as part of this CI-adaptation step. This is the gate the user asked for before step 9
  ("if all is working stat I can safely do the final rename").

- **Migration step 9 — plugin rename + Maven Central relocation. DONE (structurally; publishing is
  still the user's own later action).** The former `llamacpp-ai-index-maven-plugin` module was
  renamed to `srcmorph-maven-plugin` (`net.ladenthin:srcmorph-maven-plugin`, goal prefix `srcmorph`,
  package `net.ladenthin.maven.srcmorph.mojo`, properties `aiIndex.*` → `srcmorph.*`), and a new,
  independent relocation-stub module `llamacpp-ai-index-maven-plugin/` (pom-only, no `<parent>`,
  pinned at version `1.0.4`, only `<distributionManagement><relocation>` pointing at
  `net.ladenthin:srcmorph-maven-plugin:1.1.1`) was added back to the root `<modules>` list so
  existing consumers resolving the old coordinates get redirected once it is actually published.
  This is the last, isolated step of the migration in terms of code/POM structure — actually
  publishing both the `1.1.1` reactor release and the `1.0.4` relocation stub to Maven Central is
  still the user's own action (this task never ran `mvn deploy` or signed anything).

  **Caveat — exclude the stub from reactor-wide version bumps.** Because the relocation stub is
  listed in the root `<modules>`, a `mvn versions:set -DnewVersion=X -DgenerateBackupPoms=false` run
  from the repo root walks every module reachable from that list — including the stub — and would
  overwrite its frozen `1.0.4` unless explicitly excluded:
  ```bash
  mvn versions:set -DnewVersion=X -DgenerateBackupPoms=false \
      -Dexcludes=net.ladenthin:llamacpp-ai-index-maven-plugin
  ```
  The same warning is documented in the stub's own `pom.xml` comment and in the root `CLAUDE.md`
  reactor-layout section.

- **PIT mutation-testing gate for `srcmorph-cli` and the plugin module.** Only `srcmorph`
  (`srcmorph/pom.xml`) is PIT-gated today — `<mutationThreshold>100</mutationThreshold>` over an
  explicit `<targetClasses>` list of 47 classes across config/document/engine/indexer/prompt/
  provider/support, all killed at 100%. Neither `srcmorph-cli` nor
  `srcmorph-maven-plugin` has a `pitest-maven` execution of its own yet (both poms document
  this explicitly with a comment at the spot a PIT plugin block would go). For the CLI: the pure
  helpers worth mutation-gating are the config copy/round-trip (`Main#copyWithPlanOnlyForced`) and
  command dispatch; `Main`'s I/O-heavy entry points (`main`, `loadConfiguration`) are better served by
  integration tests than unit-mutation gating. For the plugin: the 5 mojo classes are Maven-lifecycle
  orchestration (typically integration-tested via Maven invoker/executor, not unit-mutation-tested) —
  confirm that reasoning still holds before assuming it's a permanent exemption rather than a gap.

- **Expand `srcmorph`'s own PIT mutation scope (optional).** `srcmorph/pom.xml` wires
  `<mutationThreshold>100</mutationThreshold>` over an explicit `<targetClasses>` list (config /
  document / prompt / provider / support value+logic classes, plus `indexer.AiInputWindowCalculator`
  and `support.AiProgressBar`), all killed at 100%. Still out (optional, need careful fixtures):
  `document.AiMdDocumentCodec` / `AiMdHeaderCodec`, `prompt.AiPromptPreparationSupport`, and the newer
  `indexer.AiIndexPlan` / `config.AiConditionGroup`. The orchestration layers (`indexer.*` walk,
  the plugin's `mojo.*`) and the JNI provider stay out of PIT — they need a Maven/native context
  rather than pure-unit mutation (see crossrepostatus "Deliberate non-parity").

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory (declared in `srcmorph/pom.xml`, the only reactor module with a jqwik test dependency).

- **`@VisibleForTesting` audit.** No usages currently in any module. Walk each module's production tree for package-private/protected methods or fields that exist purely so tests can reach them, and either annotate (`com.google.common.annotations.VisibleForTesting`) or move into the test source tree.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.

- **SLF4J gap CLOSED (core + CLI).** `srcmorph`'s `indexer.*` classes (`SourceFileIndexer`, `PackageIndexer`, `ProjectIndexer`, `AiFieldGenerationSupport`) and `engine.*` classes all log via `org.slf4j.Logger` instead of a Maven `Log`; `srcmorph-cli`'s `Main` does the same. Only the plugin module's 5 mojos (and `PluginArchitectureTest`'s Maven-confinement rules) still touch the Maven Plugin API directly — that boundary is deliberate (see `CLAUDE.md`), not a gap. Maven's `maven-slf4j-provider` binds `slf4j-api` inside plugin executions, so every `LOGGER.info(...)`/`.warn(...)`/`.debug(...)` call in the delegated-to `srcmorph` code surfaces as a normal `[INFO]`/`[WARN]`/`[DEBUG]` line in `mvn` output with zero glue; the CLI ships its own logback binding for the same log lines outside Maven. `AiFieldGenerationSupportTest` (in `srcmorph`) exercises the binding/configuration directly via a logback `ListAppender` attached to the class logger.
