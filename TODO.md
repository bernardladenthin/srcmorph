# TODO — llamacpp-ai-index-maven-plugin

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md);
items here are plugin-specific or are this repo's slice of a
cross-cutting initiative.

## Open

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory.

- **`@VisibleForTesting` audit.** No usages currently. Walk the production tree for package-private/protected methods or fields that exist purely so tests can reach them, and either annotate (`com.google.common.annotations.VisibleForTesting`) or move into the test source tree.

- **Null-safety refinement.** JSpecify + NullAway are now enforced at compile time in **strict JSpecify mode** with the extra options `CheckOptionalEmptiness`, `AcknowledgeRestrictiveAnnotations`, `AcknowledgeAndroidRecent`, `AssertsEnabled` (see `pom.xml`); `@NullMarked` on the package via `package-info.java`; JDK module exports in `.mvn/jvm.config`. Maven `@Parameter` / `@Component` fields are excluded from initializer checks; framework-populated POJOs (`AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiGenerationConfig`) carry class-level `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **SpotBugs `effort=Max` + `threshold=Low`** — ✅ **enforced at the gate** (`0bddf2a`). `pom.xml` `<effort>Max</effort>` + `<threshold>Low</threshold>`; `spotbugs:check` is part of `mvn verify` and fails on any unsuppressed finding. The full clearing chain is recorded in [`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md) under "SpotBugs Max+Low". `spotbugs-exclude.xml` carries narrow `<Match>` blocks with rationale: Lombok-USBR, HelpMojo auto-gen family, Maven `@Parameter` SPP, identity-IMC, prompt-template `FORMAT_STRING`, fb-contrib flow-coarseness sites, NPE→`MojoExecutionException` bridge.

- **Mutation-testing threshold enforcement (PIT)** — currently uses the "single class, full plumbing" pattern: PIT is wired in `pom.xml` with `<mutationThreshold>100</mutationThreshold>`, `<targetClasses>` narrowed to `AiCompletionParser`. Expand `<targetClasses>` incrementally as additional classes reach parity.

- **Additional ArchUnit rules to consider** — the full **`layeredArchitecture()`** rule is now DONE (the flat plugin package was split into layered packages — see "Done" below). Per-module banned-imports lists remain open.

- **No LogCaptor smoke test needed** — this module has no logging code (`org.slf4j.*` not used in `src/main/java/`); production uses Maven's `Log` interface. If SLF4J logging is ever introduced, add a LogCaptor smoke test at the same time so the binding/configuration is exercised in tests.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.

## Done (kept for history)

### Layered package restructure (flat plugin package → layered hierarchy)

The 34 classes that sat flat in `net.ladenthin.maven.llamacpp.aiindex` were split
(via `git mv`, history preserved) into layered packages enforced by a new
`layeredArchitecture()` ArchUnit rule (Mojo → Indexer → Provider → Format →
Foundation):

- **mojo** (entry): AbstractAiIndexMojo, GenerateMojo, AggregatePackagesMojo
- **indexer** (orchestration): SourceFileIndexer, PackageIndexer, AiFieldGenerationSupport
- **provider**: AiGenerationProvider(+Factory), Mock/LlamaCppJni providers, AiCompletionParser, LlamaCppJniConfig
- **document** (Format): AiMd* model/codecs + the AiGenerationRequest/AiGenerationResult carriers (they hold an AiMdHeader)
- **prompt** (Format): AiPromptDefinition, AiPreparedPrompt, AiPromptSupport, AiPromptPreparationSupport
- **config** (Foundation): AiGenerationConfig, AiGenerationKind, AiFieldGenerationConfig, AiModelDefinition, AiModelDefinitionSupport
- **support** (Foundation): AiChecksumSupport, AiTimeSupport, AiPathSupport, Java8CompatibilityHelper, ConvertToRecord

Cycle-breaking placement: the generation carriers went to `document` (not
`provider`) because the `prompt` classes take an `AiGenerationRequest` parameter —
keeping them in `provider` created a `prompt ↔ provider` cycle. Test classes
mirrored into their subjects' packages; cross-package Javadoc `{@link}` references
fully-qualified; `module-info` exports the new packages.

**jllama consumption updated in the same change** (this is what surfaced the need):
bumped `net.ladenthin:llama` 5.0.1 → 5.0.2-SNAPSHOT and adapted to its new shape —
package moves (`InferenceParameters`/`ModelParameters` → `…parameters`, `Pair` →
`…value`), the immutable-wither API (`InferenceParameters` `set*` → `with*`,
folding `withStopStrings` into the builder chain), corrected `setChatTemplateKwargs`
Javadoc links to `ModelParameters`, and a `<dependencyManagement>` pin converging
`slf4j-api` to 2.0.18 (logback pulled an older patch). All 10 ArchUnit rules green;
54 tests pass (1 JNI integration test skipped without the native lib); `javadoc:jar`
clean.

**Note**: 5.0.2-SNAPSHOT is the local jllama dev version carrying the layered
packages; pin the exact release version when jllama publishes the breaking change.

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
