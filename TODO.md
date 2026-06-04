# TODO — llamacpp-ai-index-maven-plugin

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md);
items here are plugin-specific or are this repo's slice of a
cross-cutting initiative.

## Open

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory.

- **`@VisibleForTesting` audit.** No usages currently. Walk the production tree for package-private/protected methods or fields that exist purely so tests can reach them, and either annotate (`com.google.common.annotations.VisibleForTesting`) or move into the test source tree.

- **Null-safety refinement.** JSpecify + NullAway are now enforced at compile time in **strict JSpecify mode** with the extra options `CheckOptionalEmptiness`, `AcknowledgeRestrictiveAnnotations`, `AcknowledgeAndroidRecent`, `AssertsEnabled` (see `pom.xml`); `@NullMarked` on the package via `package-info.java`; JDK module exports in `.mvn/jvm.config`. Maven `@Parameter` / `@Component` fields are excluded from initializer checks; framework-populated POJOs (`AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiGenerationConfig`) carry class-level `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **SpotBugs `effort=Max` + `threshold=Low`** — currently default effort/threshold. Raising both surfaces more findings (and takes longer per build). Worth a one-off experiment to triage what appears before committing. Cross-cutting (tracked in `crossrepostatus.md`).

- **Mutation-testing threshold enforcement (PIT)** — currently uses the "single class, full plumbing" pattern: PIT is wired in `pom.xml` with `<mutationThreshold>100</mutationThreshold>`, `<targetClasses>` narrowed to `AiCompletionParser`. Expand `<targetClasses>` incrementally as additional classes reach parity.

- **Additional ArchUnit rules to consider** — layered-architecture rules (`layeredArchitecture().consideringAllDependencies()`) and per-module banned-imports lists. `noPackageCycles` and the standard ban set are already wired; layered-architecture and per-module banned-imports are still open.

- **No LogCaptor smoke test needed** — this module has no logging code (`org.slf4j.*` not used in `src/main/java/`); production uses Maven's `Log` interface. If SLF4J logging is ever introduced, add a LogCaptor smoke test at the same time so the binding/configuration is exercised in tests.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.

## Done (kept for history)

- **Error Prone bug-pattern promotions to `ERROR`** — `034b553` (12 patterns promoted; `-Xlint:all` enabled).
- **`javac -Werror` + `-Xlint:all,-serial,-options,-classfile,-processing`** — done. EqualsGetClass warnings on the 7 `@ConvertToRecord` classes were fixed by switching to `instanceof` checks; `Java8CompatibilityHelper.formatted` carries an inline `@SuppressWarnings("AnnotateFormatMethod")`; generated `HelpMojo` is excluded from Error Prone via `-XepExcludedPaths`; `listOf` carries `@SafeVarargs` + `@SuppressWarnings({"unchecked","varargs"})`.
- **`-parameters` javac arg** — `7ae3279` (`<parameters>true</parameters>`; dropped for the test execution where jcstress / Lincheck reflection would otherwise break).
- **`--release N`** instead of `-source N -target N` — `7ae3279` (`<release>${maven.compiler.release}</release>` (release 8) for main sources, `<release>9</release>` for `module-info.java`).
- **Checker Framework as a second static-nullness pass** — wired in `pom.xml` (4.1.0) alongside NullAway. `HelpMojo` is skipped via `-AskipDefs`; framework-populated POJO classes carry `@SuppressWarnings("initialization.fields.uninitialized")`; record-style equals overrides use `@Nullable Object`.
- **JPMS `module-info.java` with module-level `@NullMarked`** — exports the single hand-written package `net.ladenthin.maven.llamacpp.aiindex`; the auto-generated `HelpMojo` package is deliberately NOT exported because Maven loads plugins classpath-only and never consults the descriptor for Mojo discovery. Two-execution `maven-compiler-plugin` pattern. Module-level `@NullMarked` IS set on the module descriptor; JSpecify is pulled in via `requires static org.jspecify;` so the annotation does not become a runtime dependency.
- **Banned-API enforcement** — Maven Enforcer (`d654442`), ArchUnit `System.exit` / `new Random` / `Thread.sleep` (`fd8cf80`), `sun.*` / `com.sun.*` / `jdk.internal.*` (`ad37355`).
- **ArchUnit additions** — public-fields-final (`d2b1af9`), `noPackageCycles` (`26a4f7b`).
- **Abstract the Java and test writing guidelines to a workspace-level shared layer.** Workspace version chain at [`../workspace/guides/src/CODE_WRITING_GUIDE-8.md`](../workspace/guides/src/CODE_WRITING_GUIDE-8.md) and [`../workspace/guides/test/TEST_WRITING_GUIDE-8.md`](../workspace/guides/test/TEST_WRITING_GUIDE-8.md); canonical TDD skill at [`../workspace/.claude/skills/java-tdd-guide/SKILL.md`](../workspace/.claude/skills/java-tdd-guide/SKILL.md). This repo's `CODE_WRITING_GUIDE.md` / `TEST_WRITING_GUIDE.md` now hold only plugin-specific supplements.
- **Standardised CLAUDE.md template** — [`../workspace/templates/CLAUDE.md.template`](../workspace/templates/CLAUDE.md.template).
