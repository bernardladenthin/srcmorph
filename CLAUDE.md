# CLAUDE.md — srcmorph (reactor)

This document provides guidance for AI assistants working on this codebase.

---

## Project Overview

**srcmorph** is a prompt-driven source-tree transformer: it walks a source tree and processes each
file through a configurable LLM prompt (by default local via llama.cpp / GGUF models; per model
optionally an OpenAI-compatible API such as OpenRouter), producing
layered output — per-file, then per-package, then per-project. Today it emits structured `.ai.md`
Markdown summaries for AI-assisted code navigation; the same rule-routed pipeline is generic enough
to eventually emit wikis, architecture docs, diagrams, or source-to-source transformations.

**This repository completed its migration to a 3-module Maven reactor.** It started as a single
Maven plugin (`net.ladenthin:llamacpp-ai-index-maven-plugin`) and was restructured into: a
framework-free core library, a standalone CLI, and the original plugin (now a thin wrapper around
the library, **renamed** to `net.ladenthin:srcmorph-maven-plugin`). **The plugin rename is done**:
coordinates, package, goal prefix, and every `@Parameter` property changed in this step — do not
write `aiIndex.*` properties, the `ai-index` goal prefix, or the
`net.ladenthin.maven.llamacpp.aiindex` package in new documentation or code; use `srcmorph.*`,
`srcmorph`, and `net.ladenthin.maven.srcmorph.mojo` instead (see the plugin module's own section
below). The `1.3.0` reactor release was published to Maven Central from the `v1.3.0` tag, and `main`
has since been moved on to `1.4.0-SNAPSHOT` (step 6 of `docs/RELEASE.md`). **A `-SNAPSHOT` on `main`
is the normal state between releases** — `main` sits at a plain release version only in the short
window between the release bump and the tag, which is exactly why that step exists: `main` must
never again carry a version matching something already published.

**A fourth module existed temporarily**: a tiny relocation-stub POM
(`net.ladenthin:llamacpp-ai-index-maven-plugin`, pinned at `1.0.4`, only a
`<distributionManagement><relocation>` pointing at `srcmorph-maven-plugin:1.1.1`) so existing
consumers of the old coordinates get transparently redirected. It was published once, verified
working end-to-end (a clean-environment Maven resolution of the old coordinates correctly followed
the relocation through to the real artifact and its full dependency graph), and then **removed from
this reactor** — the published `1.0.4` artifact is permanent on Maven Central regardless of what
this repo's module list contains, and it will never need another release, so there was no reason to
keep carrying it (and its `versions:set` exclusion footgun) in active development.

- **Group ID:** `net.ladenthin`
- **Java:** target bytecode 1.8 (production code), Java 21 test sources, built with JDK 25
- **License:** Apache 2.0
- **Author:** Bernard Ladenthin (Copyright 2026)
- **Reactor version:** `1.4.0-SNAPSHOT` (single shared version across `srcmorph`, `srcmorph-cli`,
  and `srcmorph-maven-plugin`). Last released version: `1.3.0`.

---

## Repository layout — Maven reactor

```
llamacpp-ai-index-maven-plugin/            (repo root; reactor parent)
├── pom.xml                                net.ladenthin:srcmorph-parent:1.3.0 (packaging=pom)
│                                           shared build plugins + dependencyManagement + release profile
├── srcmorph/                               CORE LIBRARY  net.ladenthin:srcmorph  (Java 8, Maven-API-free)
│   └── src/main/java/net/ladenthin/srcmorph/
│       ├── config/      18+ POJOs (incl. the shared root SrcMorphConfiguration)
│       ├── engine/      GenerateEngine, AggregatePackagesEngine, AggregateProjectEngine,
│       │                CalibrateEngine, SrcMorphException, GenerateResult, CalibrationReport, EngineSupport
│       ├── indexer/      SourceFileIndexer, PackageIndexer, ProjectIndexer, AiFieldGenerationSupport, ...
│       ├── document/  prompt/  provider/  support/
│   └── src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf   (real-model tests live here)
├── srcmorph-cli/                           CLI  net.ladenthin:srcmorph-cli  (thin jar; users build the runnable one)
│   └── src/main/java/net/ladenthin/srcmorph/cli/
│       ├── Main.java                       BitcoinAddressFinder cli/Main.java pattern
│       └── configuration/                  CConfiguration + CCommand (BAF public-field style)
├── srcmorph-maven-plugin/                   Maven plugin  net.ladenthin:srcmorph-maven-plugin, goalPrefix srcmorph
│   └── src/main/java/net/ladenthin/maven/srcmorph/mojo/   (4 goal mojos + the abstract AbstractAiIndexMojo base; renamed package/properties)
├── examples/                               config_*.json/.yaml + run_*.sh/.bat + simplelogger.properties
│   └── build-your-own-jar/                 pom.xml + build/run scripts: srcmorph-cli + this machine's natives -> srcmorph.jar
├── docs/                                   RELEASE.md + the ai-index model-benchmark writeups
└── .github/workflows/                      CI adapted to the 3-module reactor
```

Every module inherits its `<version>` from the parent (`net.ladenthin:srcmorph-parent`), so
`srcmorph`/`srcmorph-cli`/`srcmorph-maven-plugin` ship in lockstep by construction. Bump their
version reactor-wide with `mvn versions:set -DnewVersion=X -DgenerateBackupPoms=false` from the
repo root — no exclusions needed today (the relocation-stub module that once required one was
removed after its `1.0.4` release; see "Project Overview" above).

### `srcmorph` — the core library

Framework-free: **no dependency on `org.apache.maven..`** anywhere (enforced by
`CoreArchitectureTest#coreIsMavenFree`, the load-bearing ArchUnit rule for this module). Depends on
`net.ladenthin:llama` (the llama.cpp JNI binding, used only by the `provider` package), SLF4J, jspecify
+ checker-qual, Lombok (provided). Package root: `net.ladenthin.srcmorph`.

- **`config/`** — mutable JavaBeans (no Maven annotations) bindable structurally from Maven plexus XML,
  a Jackson `ObjectMapper`/`YAMLMapper`, or plain Java code. The root object is
  **`SrcMorphConfiguration`**: one bean holding everything a run needs (`baseDirectory`,
  `outputDirectory`, `subtrees`, `excludes`, `fileExtensions`, the size band, `force`, `planOnly`,
  `generationProvider`, `profileDirectory`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`, the
  `llama*` fallback params, `pluginVersion`/`aiVersion`/`projectName`). **Field names intentionally
  mirror the Maven plugin's current `@Parameter` names** so a JSON/YAML key reads identically to the
  matching `<configuration>` XML element — see `SrcMorphConfiguration`'s own Javadoc.
- **`engine/`** — one class per phase, each constructed from a `SrcMorphConfiguration` and owning its
  own AI provider lifecycle (try-with-resources; one model resident at a time):
  `GenerateEngine` (plan → validate → planOnly early-out → per-model-group indexing loop + progress
  bar), `AggregatePackagesEngine`, `AggregateProjectEngine` (deterministic listing + optional one-call
  AI overview), `CalibrateEngine` (per-model preflight + timing), `ListModelsEngine` (`GET
  <baseUrl>/models` per OpenAI-compatible server, rendered by `ModelListReport` as a table and a
  paste-ready `aiDefinitions` block; no prompt, no model). All five throw the checked
  `SrcMorphException` on misconfiguration, never a Maven `MojoExecutionException` — callers (the
  plugin's mojos, the CLI's `Main`) wrap it into whatever their own surface expects.
- **`indexer/`** — the walk/plan/write orchestration (`SourceFileIndexer`, `PackageIndexer`,
  `ProjectIndexer`, `AiFieldGenerationSupport`, `AiIndexPlan`, `AiCalibrationRunner`, ...). Logs via
  `org.slf4j.Logger` (a private static final field per class), not a Maven `Log` — this is what makes
  the module Maven-free; Maven's own `maven-slf4j-provider` (ships since Maven ≥ 3.1) makes these lines
  surface as ordinary `[INFO]`/`[WARN]` output inside a plugin execution with zero glue, and the CLI
  ships an SLF4J binding for the same log lines outside Maven.
- **`document/`** — the `.ai.md` model + codecs (`AiMdDocument`, `AiMdHeader`, `AiMdDocumentCodec`,
  `AiMdHeaderCodec`, `AiMdHeaderSupport`, `AiMdChildEntryLineFormatter`, `AiMdLeadExtractor`,
  `AiGenerationRequest`/`AiGenerationResult`).
- **`prompt/`** — `AiPromptDefinition` (a `key` and either an inline `template` or a
  `templateFile`), `AiPromptTemplateLoader` (reads every `templateFile`, relative to the
  configuration's `profileDirectory`, and every prompt key a rule uses without a definition from
  `<profileDirectory>/<key>.md`, before the prompts are registered), `AiPreparedPrompt`,
  `AiPromptSupport`, `AiPromptPreparationSupport`. The fact counters work the same way:
  `config.AiFactDefinitionLoader` reads a `factsFile` or `<profileDirectory>/<key>.tsv` (`label<TAB>regex`
  per line) for a rule's `factsKey`; `support.AiProfileFileSupport` is the shared file convention.
  The measured profile (prompts + fact counters) lives once in the repo-root `profiles/v2/`
  (`profiles/README.md`); the plugin's self-test profile, the model-card benchmark, the examples and
  the plugin-it fixture all name only keys and read it from there, so do not paste a prompt text or
  a regex into a configuration again. `SqlFactPatternsTest` / `JavaStructureFactPatternsTest` read
  the shipped `.tsv` files, never copies.
- **`provider/`** — the AI backend abstraction: `AiGenerationProvider` (`Closeable`),
  `AiGenerationProviderFactory` (looks up `"mock"` / `"llamacpp-jni"` / `"openai-compatible"`; a model
  definition's own `provider` overrides the run's `generationProvider`), `MockAiGenerationProvider`,
  `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider` (HTTP to
  `<baseUrl>/chat/completions`, and `listModels()` from `<baseUrl>/models` into `RemoteModel`s, key
  from the env var `apiKeyEnv` names; its pure logic lives in the PIT-gated
  `OpenAiCompatibleProviderSupport`; with `requestsPerMinute` every request first passes the
  `RequestRateLimiter` shared by all providers of one server and limit, which waits on a
  `Condition` instead of `Thread.sleep`), `LlamaCppJniConfig` (built through `LlamaCppJniConfig.builder(modelPath)`; the
  positional constructor is private, and every value the caller does not name defaults to the matching
  `AiGenerationConfig.DEFAULT_*`) + `LlamaCppJniConfigFactory` (the pure 37-field
  mapping from a resolved `AiGenerationConfig` to the native binding's parameter objects — extracted
  from the old mojo so it is unit- and PIT-testable without a Maven runtime), `AiCompletionParser`.
- **`support/`** — foundation helpers with no dependency on anything above them: `AiChecksumSupport`,
  `AiTimeSupport`, `AiPathSupport`, `AiSourceExcludeFilter`, `AiProgressBar`, `AiSourceChunker`,
  `AiDeterministicSummary`, `AiGenerationTimeEstimator`, `Java8CompatibilityHelper`, `ConvertToRecord`,
  `ReplaceWithLibrary` (marks a copy of a library class whose release is not out yet;
  `ReplaceWithLibraryTest` fails once the library class is on the classpath, see `TODO.md`).

**Architecture rules** (`CoreArchitectureTest`, ArchUnit): `coreIsMavenFree` (the load-bearing rule
above), `layeredArchitecture` (`engine` on top → `indexer` → `provider`/`document`/`prompt` → `config`
→ `support`), `noPackageCycles`, `loggersArePrivateStaticFinal`, `noSystemExit`,
`noTestFrameworksInProduction`, `noJavaUtilLogging`, `noSystemOutOrErrInProduction`,
`noInternalJdkImports`, `noPublicMutableFields`, `noNewRandom`, `noThreadSleep`,
`jniConfinedToProvider` (only the `provider` package may touch the llama.cpp JNI binding).

**Test suite** (`srcmorph/src/test/java/net/ladenthin/srcmorph/`, package-renamed 1:1 with production):
~64 test files, incl. jqwik properties, an ArchUnit suite, a Lincheck race test
(`AtomicCounterLincheckTest`), and the model-backed real tests gated on
`src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf`. **`LlamaCppJniKnobSweepTest` drives every
model knob at a non-default value through a real generation** (one case per knob, ~35 s for all of
them): a knob is exercised only when the native binding *accepts* the value derived from it, which is
what the mapping unit tests and the `mock`-provider suite structurally cannot see -- both provider
defects fixed in 1.2.0 were of exactly that shape. Its reflective completeness check fails when a knob
is neither swept nor listed in `NOT_SWEPT` with a reason, so a knob added later reds the class until
somebody decides how it is covered. **PIT mutation testing**: `mutationThreshold`
100 over an explicit `targetClasses` list in `srcmorph/pom.xml` — currently 62 entries (nested classes counted) across
config/document/engine/indexer/prompt/provider/support, all killed at 100%. **All three modules are
PIT-gated now**: `srcmorph-cli` (22/22) and `srcmorph-maven-plugin` (62/62) carry their own
`pitest-maven` executions at the same threshold, and CI runs the goal reactor-wide. The natives come from `net.ladenthin:llama-platform` (a pom
naming the CPU natives jar of every desktop platform — since llama 6.0.0 `net.ladenthin:llama` is the
classes only); the `gpu-cuda`/`gpu-vulkan` profiles and `-Dllama.classifier=<natives jar>` (profile
`gpu-natives`) **add** a GPU module jar next to them (since llama 6.0.0 one ggml backend module, which
ggml loads when its vendor runtime is installed; otherwise the CPU modules run). They live here; the `jcstress` and
`vmlens` profiles/tests currently still live in the **plugin** module (they were not moved in the
extraction — see that module's section below), not here.

### `srcmorph-cli` — the standalone CLI

`net.ladenthin:srcmorph-cli`, packaging `jar`, package root `net.ladenthin.srcmorph.cli`. A BAF-style
CLI driven by a single JSON or YAML configuration file:

- **`cli/Main.java`** — extension-dispatched loader (`.json`/`.js` → Jackson `ObjectMapper`,
  `.yaml`/`.yml` → `YAMLMapper`, both with `FAIL_ON_UNKNOWN_PROPERTIES` enabled so a config typo fails
  fast, mirroring what plexus does on the Maven XML side); echoes the parsed configuration back
  (re-serialized as both JSON and YAML) for review before anything runs; no `System.exit` anywhere — a
  failure propagates as an unchecked exception out of `main(String[])`. Dispatches on `CConfiguration`'s
  `command` field to one or more `net.ladenthin.srcmorph.engine.*` engines.
- **`cli/configuration/CConfiguration`** / **`CCommand`** — public-mutable-field JavaBeans (the BAF
  `cli.configuration.CConfiguration` convention; carved out of the `noPublicMutableFields` ArchUnit rule
  via this package's explicit exception). `CConfiguration.srcMorph` is the **same**
  `net.ladenthin.srcmorph.config.SrcMorphConfiguration` the Maven plugin's mojos build from their own
  `@Parameter` fields — a JSON/YAML key under `srcMorph` reads identically to the matching plugin XML
  element. `CCommand` is `Plan | GenerateFileIndex | AggregatePackages | AggregateProject | All |
  Calibrate | ListModels`; `ListModels` reads the top-level `modelFilter` too, and the package and
  project phases take the top-level `packageFieldGenerations` / `projectFieldGenerations` when set
  (a copy of `srcMorph` with those rules, as the plugin's aggregate executions override
  `<fieldGenerations>`), otherwise `srcMorph.fieldGenerations`. `examples/build-your-own-jar/srcmorph.yaml`
  is the plugin's `srcmorph-selftest` profile as one CLI configuration (gpt-oss-20b + the two
  Granite presets only, like the profile); `BuildYourOwnJarSelfTestParityTest` reads both and fails
  when they drift. The default is `Plan` (safe: no model load, nothing written).
- **No runnable jar is built or published here** (since 1.4.0). `mvn package` produces only the thin
  `srcmorph-cli` jar (its manifest names `net.ladenthin.srcmorph.cli.Main`, so `jbang` can run it).
  Users build the runnable jar with `examples/build-your-own-jar/pom.xml`: `srcmorph-cli` from
  Central, `llama-platform` excluded, the one natives jar of the OS/arch picked by an OS-activated
  profile (`-Dnatives=` overrides), an optional GPU module via `-Dgpu=<classifier>`, packed by
  `jar-with-dependencies` into `target/srcmorph.jar`. Up to 1.3.0 this module built a fat jar on
  every `package` and CI released 15 of them on GitHub; there are too many OS/CPU/GPU combinations
  to keep doing that.
- Declares its own SLF4J binding (`org.slf4j:slf4j-simple`, runtime scope) — unlike the library
  (consumer picks any SLF4J binding) and the plugin (gets one for free from Maven's own
  `maven-slf4j-provider`), a standalone `java -jar` process needs to bring its own or every log line is
  silently dropped. It is **not** logback: see "Java 8 bytecode floor" below. The logging defaults
  (INFO, stdout, timestamps) are `examples/build-your-own-jar/simplelogger.properties`, which that
  pom packs into the user's jar; the published `srcmorph-cli` jar carries none, so an embedder's own
  configuration is never overridden.
- **Architecture rules** (`CliArchitectureTest`): `cliIsLeaf` (nothing else in the reactor may depend on
  this module — it is the leaf-most consumer), `noPublicMutableFields` (with the `configuration`
  package carve-out), `noSystemExit`, `mavenFree` (must never depend on the Maven Plugin API — that
  boundary belongs to the plugin module), `noTestFrameworksInProduction`, `noInternalJdkImports`,
  `loggersArePrivateStaticFinal`.
- **Tests**: `MainTest`, `configuration.ConfigBindingTest` (round-trips a private
  `src/test/resources/test-fixtures/minimal-generate.{json,yaml}` pair through both parsers),
  `ExamplesConfigBindingTest` (sweeps every shipped `examples/config_*.{json,yaml}` fixture — the
  public, documented examples — through the same strict mappers), `CliEndToEndTest` (drives the `All`
  and `Plan` commands against the mock provider end to end, no forked process, and `ListModels`
  against a loopback stub, binding the printed template back into a configuration).

### `srcmorph-maven-plugin` — the Maven plugin (renamed; formerly `llamacpp-ai-index-maven-plugin`)

**Renamed in the final migration step**: coordinates `net.ladenthin:srcmorph-maven-plugin` (was
`net.ladenthin:llamacpp-ai-index-maven-plugin`), package `net.ladenthin.maven.srcmorph.mojo` (was
`net.ladenthin.maven.llamacpp.aiindex.mojo`), goal prefix `srcmorph` (was `ai-index`), every
`@Parameter` property now spelled `srcmorph.*` (e.g. `srcmorph.skip`, `srcmorph.file.skip`,
`srcmorph.planOnly`, `srcmorph.generationProvider`, `srcmorph.llama.modelPath` — was `aiIndex.*`).
The old coordinates were kept alive on Maven Central via a one-time relocation-stub publish (see
"Project Overview" above and its own paragraph below) — never describe the plugin using the old
coordinates/package/properties in new documentation or code. The plugin's *contents* stay thin: it
depends on `net.ladenthin:srcmorph` (compile scope) for everything except the 5 mojo classes
themselves.

- **`AbstractAiIndexMojo`** — shared `@Parameter` fields + `buildConfiguration()`, which maps them onto
  a new `SrcMorphConfiguration` for the matching engine to run. Concrete mojos
  (`GenerateMojo`/`AggregatePackagesMojo`/`AggregateProjectMojo`/`CalibrateMojo`) each add their own
  goal-specific `@Parameter`s (e.g. `skipFile`/`skipPackage`/`skipProject`, `planOnly`, `fileExtensions`,
  `excludes`, `factDefinitions`), build the configuration, and delegate the whole run to one
  `net.ladenthin.srcmorph.engine.*` engine — mojos are now thin (≤ ~30 lines of actual logic each) and
  translate a caught `SrcMorphException`/`IOException` into a `MojoExecutionException`. The class
  itself keeps its historical name (`AbstractAiIndexMojo`) even though the package/goal-prefix/
  properties around it were renamed — only the Maven-facing surface (coordinates, package, goal
  prefix, `@Parameter` property strings) was in scope for the rename.
- **Skip flags stay mojo-side** (`skip`, `skipFile`, `skipPackage`, `skipProject`) — a Maven lifecycle
  concern, not part of `SrcMorphConfiguration`; an engine built from a configuration always executes
  when asked. See `MojoPhaseSkipTest`.
- **Two property namespaces — do NOT confuse them (this has tripped audits).** The **published mojo
  `@Parameter`s** are the `srcmorph.*` set (`srcmorph.skip`, `srcmorph.force`, `srcmorph.planOnly`,
  `srcmorph.generationProvider`, `srcmorph.llama.*`, …). The `ai.*` names in the plugin `pom.xml` +
  README (`ai.model`, `ai.gpuLayers`, `ai.mainGpu`, `ai.devices`, `ai.index.output.directory`) are
  **repo-local Maven build properties** wired into this module's own gpt-oss self-test/benchmark
  executions (`<gpuLayers>${ai.gpuLayers}</gpuLayers>`, `<aiDefinitionKey>${ai.model}</aiDefinitionKey>`)
  and overridable with `-Dai.*` — they are **not** mojo parameters, and downstream consumers set the
  same knobs as `<configuration>`/model-definition elements. So `-Dai.gpuLayers=12` etc. are **correct**
  as documented; do not "fix" them to `srcmorph.*`.
- **Architecture rules** (`PluginArchitectureTest`): Maven-annotation confinement to `mojo`, every mojo
  extends `AbstractMojo`, plus this module's slice of the shared conventions.
- **jcstress** (`jcstress/AiOversizeStrategyRace.java`) and **vmlens**
  (`vmlens/VmlensInterleavingSmokeTest.java`) tests/profiles currently live in this module, not in
  `srcmorph` — they were not relocated during the core extraction.
- **The plugin is exercised AS A PLUGIN only by the `plugin-it` CI job** (`.github/plugin-it.sh` +
  the fixture in `.github/plugin-it/`), never by this module's unit tests. That distinction matters
  when adding a `@Parameter`: `MojoConfigurationMappingTest` and friends set the *field*, so they
  cannot see the `property` string, the plexus XML binding, the goal prefix or the generated
  descriptor — a renamed property used to ship green. The IT installs the reactor
  (`-pl srcmorph-maven-plugin -am`, which provably excludes `srcmorph-cli` and its ~80 MB assembly),
  then runs the fixture through a real Maven lifecycle with the `mock` provider: no GGUF, no GPU, no
  network. Its fixture pom mirrors the worked example in this module's `README.md`, so a README that
  drifts from the working XML fails it. It gates `publish-snapshot` and `publish-release`.
  Two traps when extending it, both found by falsifying assertions rather than by reading:
  an explicit value in the fixture's `<configuration>` **beats** a `-D` property, so a
  property-binding check has to drive a parameter the fixture pom leaves unset; and the fixture must
  **not** get a `settings.xml` with a `<pluginGroups>` entry — `mvn srcmorph:generate` resolves
  without one (Maven matches the prefix against the descriptor of the plugin the fixture declares),
  while adding one opens a second path through repository metadata that maps the prefix by
  artifactId and thereby **masks a changed `<goalPrefix>` completely**, on a pristine runner as
  well. The general lesson for a fixture-based test: an element the fixture sets to a value that
  equals the production default, or a pattern that matches no file that exists, is not covered by
  the run — it is invisible to it. `<subtrees>`, `<excludes>` and the per-execution overrides all
  started out that way here.
- Full goal/parameter reference: `srcmorph-maven-plugin/README.md`.

### `llamacpp-ai-index-maven-plugin` — the retired relocation stub (no longer in this repo)

Not a module in this reactor anymore. It existed briefly as a minimal 4th reactor module — **not**
a renamed copy of the plugin above, and not a child of `srcmorph-parent` (no `<parent>` at all).
Its entire `pom.xml` was `groupId` + `artifactId` (`llamacpp-ai-index-maven-plugin`) + a version
pinned independently at `1.0.4` + `<distributionManagement><relocation>` pointing at
`net.ladenthin:srcmorph-maven-plugin:1.1.1`. No source, no tests, no dependencies. Its sole purpose
was so a consumer still declaring the old Maven coordinates gets redirected by Maven to the renamed
plugin — published once, verified working end-to-end from a clean environment, then removed from
active development: the published `1.0.4` artifact is permanent on Maven Central regardless of
this repo's module list, and it will never need another release. Anyone still depending on the old
coordinates continues to be redirected correctly; nothing here needs to change again for that to
keep working.

---

## Build Commands

### Whole reactor (repo root)

```bash
mvn compile          # Compiles every module (Java + generates nothing native; pure Java reactor)
mvn test             # Runs every module's tests
mvn package          # Builds all four reactor projects: parent pom + 3 jars
mvn install          # Installs all four into ~/.m2 (needed before iterating on a single module — see below)
```

### Iterating on one module

Maven resolves inter-module dependencies (`srcmorph-maven-plugin` and `srcmorph-cli` both
depend on `net.ladenthin:srcmorph`) via the local repository, not in-reactor classes, unless you use
`-pl`/`-am`:

```bash
# Build/install the core first if you're iterating on the CLI or the plugin against local core changes:
mvn -pl srcmorph -am install -DskipTests

# Then work on just one module:
mvn -pl srcmorph-cli test
mvn -pl srcmorph-maven-plugin test
```

### Offline / restricted-network environments

```bash
mvn test -o                 # requires a warm ~/.m2/repository cache
mvn package -o -DskipTests
```

### Run the self-test profile (plugin module only)

```bash
mvn -pl srcmorph-maven-plugin srcmorph:generate -P srcmorph-selftest
```

### Run the CLI

```bash
mvn install -DskipTests
mvn -f examples/build-your-own-jar/pom.xml package -Dsrcmorph.version=1.4.0-SNAPSHOT
java -jar examples/build-your-own-jar/target/srcmorph.jar examples/config_All.json
```

See `examples/` (repo root) for ready-to-run `config_*.json`/`.yaml` + paired `run_*.sh`/`.bat`
launcher scripts, all using the `mock` provider (no GGUF model required).

---

## Testing

Every module uses JUnit Jupiter + Hamcrest; `MockAiGenerationProvider` gives fully deterministic tests
with no model or JNI dependency. Model-backed tests (in `srcmorph`) are gated on
`srcmorph/src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf` and self-skip when the native library
is unavailable.

- `srcmorph` — the bulk of the test suite (framework-free logic); see that module's section above.
- `srcmorph-cli` — `MainTest`, `ConfigBindingTest`, `ExamplesConfigBindingTest`, `CliArchitectureTest`,
  `CliEndToEndTest`.
- `srcmorph-maven-plugin` — `PluginArchitectureTest`, `MojoPhaseSkipTest`, plus the jcstress/
  vmlens tests noted above.

See `TEST_WRITING_GUIDE.md` (repo root, applies to every module) for full conventions.

---

## Code Conventions

### Logging

`srcmorph` and `srcmorph-cli` log via `org.slf4j.Logger` (`private static final Logger LOGGER = ...`,
enforced by each module's own `loggersArePrivateStaticFinal` ArchUnit rule). The plugin module's mojos
still use `AbstractMojo#getLog()` (a Maven `Log`) at the mojo boundary only — everything they delegate
to below that boundary is SLF4J.

### Null Safety

- JSpecify `@Nullable`/`@NonNull` (default) annotations; NullAway + Checker Framework enforce this at
  compile time in every module. Mark nullable return types and parameters explicitly.
- Prefer early null/empty guards with a logged warning over silent skips.

### Named Constants

Every meaningful literal (string keys, header field names, node types, version strings) must be a
`public static final` or `private static final` named constant with Javadoc.

### License Headers

All source files must include the Apache 2.0 license header wrapped in `// @formatter:off` /
`// @formatter:on` (or the file type's equivalent comment syntax — see `examples/` for the `#`/`REM`/
XML-comment conventions used there; JSON/YAML example fixtures carry no inline header, see
`REUSE.toml`).

### Records

Immutable value types are Java `record`s where practical (e.g. `AiMdDocument`, `AiMdHeader`,
`AiPreparedPrompt`, `AiGenerationRequest`, `GenerateResult`).

### `useModulePath=false` (all three modules)

Every module's `maven-surefire-plugin` configuration forces `<useModulePath>false</useModulePath>`.
Each module ships a `module-info.java`, but it is release metadata for module-path *consumers* only —
this reactor's own build, tests, and every real consumer today load these jars on the plain classpath
(the production bytecode targets Java 8, where `module-info.class` is inert). Leaving Surefire's
module-path auto-detection on would patch test classes into the named module and then also require
every test-only dependency (e.g. `archunit-junit5`) to be explicitly module-readable, which
`module-info.java` intentionally does not declare — so classpath mode is not a workaround, it is the
actually-representative test environment.

---

## CI/CD Pipelines (`.github/workflows/`)

| Workflow | Trigger | Purpose |
|---|---|---|
| `publish.yml` | Push, PR, manual dispatch | Unified build/test/coverage/package pipeline; publishes snapshots and Maven Central releases |
| `codeql.yml` | Schedule/Push | GitHub CodeQL security scanning |
| `scorecard.yml` | Schedule / Push | OpenSSF Scorecard supply-chain security analysis |
| `osv-scanner.yml` | Schedule / Push / PR | Google OSV-Scanner dependency vulnerability scan |
| `reuse.yml` | Push / PR | FSFE REUSE license-compliance check (`fsfe/reuse-action`) |
| `claude-code-review.yml` | PR | AI-powered code review |
| `claude.yml` | Issue/PR comment with `@claude` | Claude Code interactive assistant |

`publish.yml` still reflects the pre-reactor single-module layout in places (report globs, artifact
paths); adapting it fully to the 3-module reactor is a separate, later step (see `TODO.md`) — do not
assume it has already been updated.

---

## Dependencies Summary

| Dependency | Version | Used by |
|---|---|---|
| `net.ladenthin:llama` + `llama-platform` (pom) | 6.0.0 | `srcmorph` (`provider` package only) — llama.cpp JNI binding (classes) + the CPU natives jars; its own SLF4J binding is excluded transitively (see "Java 8 bytecode floor") |
| `org.slf4j:slf4j-api` | 2.0.20 (converged in the parent) | `srcmorph`, `srcmorph-cli`, the plugin |
| `org.slf4j:slf4j-simple` | 2.0.20 (converged in the parent) | `srcmorph-cli` (runtime binding) |
| `ch.qos.logback:logback-classic` | 1.6.6 (converged in the parent) | `srcmorph` (**test scope only** — `ListAppender` capture) |
| `com.fasterxml.jackson.core:jackson-databind` | pinned in parent | `srcmorph-cli` (JSON config) |
| `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml` | pinned in parent | `srcmorph-cli` (YAML config) |
| `org.apache.maven:maven-plugin-api` | 3.9.16 | `srcmorph-maven-plugin` (provided) |
| `org.apache.maven.plugin-tools:maven-plugin-annotations` | 3.15.2 | `srcmorph-maven-plugin` (provided) |

Test-only (every module): `org.junit.jupiter:junit-jupiter`, `org.hamcrest:hamcrest`,
`com.tngtech.archunit:archunit-junit5`. `srcmorph` additionally uses jqwik (pinned ≤ 1.9.3 — see the
jqwik policy link below), JMH, jcstress, Lincheck, vmlens.

---

## Test / Code Writing Compliance

After modifying or creating any `.java` file, in whichever module it lives:

- For `*Test.java` files, follow the workspace version chain:
  [`../workspace/guides/test/TEST_WRITING_GUIDE-8.md`](../workspace/guides/test/TEST_WRITING_GUIDE-8.md)
  (baseline) **and** this repo's own `TEST_WRITING_GUIDE.md` (repo-wide supplement; applies to every
  module — there is one guide file at the repo root, not one per module).
- For production sources, follow the workspace version chain:
  [`../workspace/guides/src/CODE_WRITING_GUIDE-8.md`](../workspace/guides/src/CODE_WRITING_GUIDE-8.md)
  (baseline) **and** this repo's own `CODE_WRITING_GUIDE.md`.
- Apply all fixable violations automatically; report only those that cannot be resolved without a
  large refactor.

---

## Pull Request Workflow

See [`../workspace/workflows/pull-request-workflow.md`](../workspace/workflows/pull-request-workflow.md).

---

## Key Design Principles

1. **Local-first** — by default all AI inference runs locally via llama.cpp; no cloud API calls, no
   data leaves the machine. A remote model is an explicit per-model opt-in (`provider`
   `openai-compatible`), and its API key is only ever read from an environment variable.
2. **Deterministic indexing** — the same source produces the same `.ai.md` skeleton (deterministic
   header); only the AI-generated body varies.
3. **Incremental updates** — files with existing summaries are skipped unless `force=true`; checksums
   detect source changes.
4. **One shared configuration object** — `net.ladenthin.srcmorph.config.SrcMorphConfiguration` is
   bindable from Maven plexus XML, Jackson JSON/YAML (the CLI), or plain Java code, so a JSON/YAML key
   always reads identically to the matching Maven `<configuration>` XML element.
5. **Provider abstraction** — AI backends are pluggable through `AiGenerationProvider`; the mock
   provider enables fully deterministic tests everywhere.
6. **Configuration-driven prompts & rule-based routing** — prompt templates and the `<fieldGenerations>`
   routing rules (composable `<condition>` tree, priority, skip, exactly one fallback) are data, never
   hardcoded in Java; see `SrcMorphConfiguration`'s Javadoc and each engine's own Javadoc for the
   `generate`/`aggregate-packages`/`aggregate-project`/`calibrate` semantics.
7. **Staged, always-green migration** — the plugin's public coordinates never change out from under an
   existing consumer mid-migration; the rename to `srcmorph-maven-plugin` is a deliberately isolated,
   later step (see `TODO.md`).

## Java 8 bytecode floor — what may ship

Production code in all three modules targets **Java 8** (`release 8`), so **every class a
consumer's JVM can load must be class-file major 52 or lower**. Two entries exist only for that,
and both are easy to undo by accident:

- **`slf4j-simple`, not logback, is `srcmorph-cli`'s shipped SLF4J binding.** Every logback release
  from 1.4.0 on is Java 11 bytecode, so `LogbackServiceProvider` cannot load on Java 8 — SLF4J's
  `ServiceLoader` finds it at startup and the JVM throws `UnsupportedClassVersionError` before a
  single log line is written. The Java 8 line (1.3.x) is end-of-life and every logback CVE disclosed
  since has been fixed only in 1.5.x/1.6.x with no backport, so it is not an option either.
  `slf4j-simple` is six classes from the same release train as `slf4j-api`, with no configuration or
  socket layer for a CVE to live in. logback stays, but **test scope only**, for
  `AiFieldGenerationSupportTest`'s `ListAppender`.
- **`checker-qual` is `provided` scope, not a pinned old version.** It is major 55 from 4.0.0 on and
  its annotations are `@Retention(RUNTIME)`, so anything reflecting over an annotated element (Jackson
  does — the CLI binds its whole configuration with it) loads them. **Pinning the shipped copy to the
  last Java 8 line (3.55.1) does not work**: the Nullness Checker resolves its own qualifiers through
  javac's symbol table, i.e. the *compile classpath*, so a 3.x checker-qual under the 4.x processor
  fails every build with `Could not load type:
  org.checkerframework.framework.qual.DoesNotUnrefineReceiver`. Processor and qualifiers must share a
  major version. `provided` satisfies both: 4.3.0 where the checker needs it, and excluded from
  consumers' graph **and** from any `jar-with-dependencies` built from it (that descriptor takes
  scope `runtime`).
  `<optional>true</optional>` alone would not have been enough — that descriptor filters on scope
  only. Safe because no source in this reactor imports `org.checkerframework`.

**The gate: the `java/verify-bytecode` action of [bernardladenthin/actions](https://github.com/bernardladenthin/actions)** (its `verify-bytecode-version.sh`).
It opens every `.class` in every jar it is given and fails on any whose class-file major version
exceeds `max-major`:

```yaml
- uses: bernardladenthin/actions/java/verify-bytecode@<commit> # v1.x.y
  with:
    max-major: "52"
    paths: |          # jars or directories, one per line
      smoke-jars
    allow: |          # optional: '<jar-basename>:<entry-path>' globs to tolerate
```

Paths may be jars or directories (searched recursively for `*.jar`), so one invocation covers a
whole artifact set. `module-info.class` and `META-INF/versions/**` are skipped unconditionally —
a classpath JVM never loads either, which is why a `release 9` `module-info` is fine. `allow`
takes globs matched against `<jar-basename>:<entry-path>` for anything else that must be
tolerated. Exit codes: 0 clean, 1 violations, **2 nothing to scan** (an empty input is a failure,
never a pass). Wired into the `smoke-jar` job at `max-major` 52, over the user-built `srcmorph.jar`
(the CLI with its whole runtime classpath) and the plugin jar.

## Javadoc Conventions

See [`../workspace/policies/javadoc-conventions.md`](../workspace/policies/javadoc-conventions.md).

## SpotBugs Suppressions

See [`../workspace/policies/spotbugs-suppressions.md`](../workspace/policies/spotbugs-suppressions.md).
Each module has its own `spotbugs-exclude.xml` (`srcmorph/spotbugs-exclude.xml`,
`srcmorph-cli/spotbugs-exclude.xml`, `srcmorph-maven-plugin/spotbugs-exclude.xml`).

## Spotless Formatting

See [`../workspace/policies/spotless-formatting.md`](../workspace/policies/spotless-formatting.md).
Run `mvn spotless:apply` before every commit that touches `.java` files (reactor-wide from the root, or
scoped to one module with `-pl`).

## jqwik Policy

See [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md).
jqwik is a test dependency of `srcmorph` only.

## Lombok Config

See [`../workspace/policies/lombok-config.md`](../workspace/policies/lombok-config.md).
`lombok.config` lives once at the repo root and is inherited by every module (Lombok walks up the
directory tree from each source file looking for `lombok.config`).

## CI Test Diagnostics

See [`../workspace/policies/ci-test-diagnostics.md`](../workspace/policies/ci-test-diagnostics.md).

## PIT Mutation Testing

See [`../workspace/policies/pit-mutation-testing.md`](../workspace/policies/pit-mutation-testing.md).
Run PIT with the lifecycle prefix. Reactor-wide (what CI does):
`mvn test-compile org.pitest:pitest-maven:mutationCoverage`; or scoped to one module with
`-f srcmorph/pom.xml`. All three modules gate at `mutationThreshold` 100 — `srcmorph` (1107 mutations),
`srcmorph-maven-plugin` (62, the five mojo classes) and `srcmorph-cli` (22). The CLI's
`Main.main(String[])` is the one documented exclusion: it is the process entry point, and the
`smoke-jar` release-gating job already runs the real `java -jar` artifact and asserts
`Main#run end.` in its output, which is an end-to-end check a unit mutant cannot reach.

**`LlamaCppJniAiGenerationProvider` is split so the gate can reach it, and the split is load-bearing.**
Everything the provider does that does not touch the native handle lives in
`provider.LlamaCppJniProviderSupport`, which is on the gate; the provider itself is not. That is not
tidiness. PIT re-runs every test covering a mutated line, and while the pure logic sat next to
`model()` the only way to gate any of it was to gate `model()` too -- a ~100-line `ModelParameters`
chain whose sole exerciser is `LlamaCppJniKnobSweepTest`, 36 cases that each load a GGUF (22.9 s).
Excluding those tests instead does not work: `model()`'s mutants would then have no coverage, and a
`NO_COVERAGE` mutant fails a threshold-100 gate exactly like a survivor. **Keep the boundary at
"touches the native handle"** -- moving a pure method back into the provider silently drops it off
the gate, and moving an impure one into the support class reds it.

The extension paid for itself immediately: the class contributed 23 mutations and **five survived**,
all real gaps (the two `known*Values` separator boundaries, both `seed` guards, and the
`drySequenceBreakers` branch). None could have been seen before, because a class that generates no
mutants cannot move the number -- which is exactly why the gate read a stable 775/775 straight
through the 1.1.0-era `dry_penalty_last_n` regression and through its fix.

Cost, measured on one machine rather than estimated: 11:34 min at 807 mutations before, **12:14 min
at 830 after** -- about 40 s for 23 mutations. `excludedTestClasses` was considered and is **not**
used: it would remove a test class from mutant matching for *every* target class, not just this one,
and the measurement says it is not needed. (The intermediate 15:57 min reading was inflated by the
five survivors: PIT exhausts every covering test for a mutant that never dies, while a killed one
stops at the first failure.)

**Two classes are permanently off the gate, and this is worth not re-litigating.** Both have
survivors that are *equivalent mutants*, unkillable through the public API rather than merely
untested. `document.AiMdHeaderCodec`: the colon guard in `read` is reached only after
`startsWith(HEADER_FIELD_PREFIX)`, so `colonIndex` is either `-1` or `>= 2` — the `< 0` boundary
mutant differs only at the unreachable `0`, and the `+1 -> -1` mutant only at an empty field key,
which `values.put("", value)` swallows because no header field is keyed `""`. `support.AiSourceChunker`
(28/34): its three observable boundaries *are* pinned (`maxChars == 1`, the `end < length` guard,
the `lastNewline > pos` guard), but six mutants have no observable effect at all — the ArrayList
capacity hint, `return chunks` vs `emptyList()` on empty input, two clamps the surrounding
`Math.max` absorbs, the loop head the `end >= length` break already guarantees, and `select`'s
`total <= maxChunks`, where the equality branch computes the identity mapping. Reaching 100% on
either would mean exposing internals or deleting a deliberately defensive guard. The orchestration
layers (`indexer.*`, the plugin's `mojo.*` walk) and the JNI provider stay out for a different
reason: they need a Maven/native context rather than pure-unit mutation.

## JPMS Module Descriptor

Each module ships a `module-info.java` compiled in a separate `release 9` execution, and each module's
Javadoc runs in **classpath mode** (`<source>` resolves to `8`), which is the *only* thing keeping it
clear of the JPMS module-mode javadoc trap that bit BAF. **Before raising the Java / javadoc source
level to ≥ 9 in any module, read**
[`../workspace/policies/jpms-module-descriptor.md`](../workspace/policies/jpms-module-descriptor.md).

## Release assets

srcmorph releases **only the Maven artifacts**: the four POMs and the three modules' jar, sources
and javadoc, each with a detached GPG `.asc`, on Central and as GitHub release assets.
`.github/release-assets.json` is `{"schema": 1}`: no extra file rules. Up to 1.3.0 the publish jobs
also built, signed and attached one `srcmorph-cli` fat jar per desktop GPU module of
`net.ladenthin:llama` (15 per release, never on Central); 1.4.0 dropped them together with
`sign-fatjars.sh`, `verify-classifier-fatjars.sh` and the license collector that only they needed.
Every publication goes through the one gate of [bernardladenthin/actions](https://github.com/bernardladenthin/actions) (full text: [docs/publication.md](https://github.com/bernardladenthin/actions/blob/main/docs/publication.md)).
`check-snapshot` / `check-tag` run `java/check-release-ref`: a release publishes only from the tag
`v<version>` of a non-SNAPSHOT version, a snapshot only a `-SNAPSHOT` version and never from a tag.
The publish jobs stage and sign the Central files locally (`-DskipUpload=true deploy`) and end in
`java/finalize-publication`, which collects what `.github/release-assets.json` names (Maven modules
are discovered from the POMs), checks it -- exact membership, every signature with `gpg --verify`,
each Central file byte-equal to its GitHub twin -- and freezes it into one package. Only after that
package is uploaded does the job output `assets_complete=true`; a missing file or signature fails
there, and nothing is published. Three pairs of consumers each need only that output, never each
other: `github-snapshot` / `github-release` (`java/publish-github`: the rolling `snapshot`
pre-release is serialized, never lets an older commit or run replace a newer one, prunes only after a
complete upload and starts as a draft; a versioned tag is refused), `central-snapshot` /
`central-release` (`java/publish-central`, no rebuild, no re-signing) and `attest-snapshot` /
`attest-release` (`java/attest-publication`). The jobs themselves stay in this workflow
(environment, permissions, concurrency); `java/check-publication` checks their wiring in every pull
request.

The publish jobs collect the deploy-signed Maven outputs with `--maven-only` into
`central-assets/`, and `finalize-publication` reads them from there (`--maven-assets`).

**srcmorph-specific smoke.** The `smoke-jar` job (`needs: startgate`, gates both publish jobs) takes
the path a user takes: `mvn install` of the reactor, then `examples/build-your-own-jar` with
`srcmorph.version` and `llama.version` read from the reactor, so the example pom itself is under test
on every pull request. It then runs the `java/smoke-fatjar-cli` action of [bernardladenthin/actions](https://github.com/bernardladenthin/actions)
(BAF runs it too) from `examples/` against `config_Plan.json`, asserting exit 0 plus `Main#run end.` in the output.
`Plan` with the `mock` provider needs no GGUF, no GPU and no network, which makes this the cheapest
possible real launch of the CLI. **Do not "strengthen" it to `config_All.json` over a real source
tree:** the example configs are tuned for a small demo tree, so `All` against this repo's own
sources fails by design (19 files exceed the demo model's context window with `onOversize=fail`) —
that would make the smoke non-deterministic, not more thorough. Note the shipped `Plan` example
plans 0 files when run from `examples/` (its `subtrees: ["src/main/java"]` does not exist there);
that is fine for a smoke, which is testing that the artifact launches and completes, not the
indexer.

## Dependency Convergence Pinning

`dependencyConvergence` is enabled (maven-enforcer) in each of the 3 reactor modules;
`jspecify`/`checker-qual` are pinned in the reactor parent's `dependencyManagement` (next to the
existing `slf4j-api`/`logback-classic`/`jackson` pins) because `net.ladenthin:llama` brings both
transitively. Convention + the `excludedScopes` gotcha + merge-discipline guidance (this repo's
`main` was actually broken by exactly this pattern once — Dependabot PR #169) are in
[`../workspace/policies/dependency-convergence-pinning.md`](../workspace/policies/dependency-convergence-pinning.md).

## Shared CI: bernardladenthin/actions (`repository-checks` job)

The CI code the four Java repositories (java-llama.cpp, BitcoinAddressFinder, srcmorph,
streambuffer) share lives once in [bernardladenthin/actions](https://github.com/bernardladenthin/actions): composite actions for single steps (`uses:
bernardladenthin/actions/<common|java>/<action>@<commit> # v1.x.y`), reusable workflows for whole
jobs (CodeQL, Maven dependency submission, REUSE, OSV-Scanner, SonarQube -- the workflow files here
only name the triggers), and templates for the files this repository has to carry itself.
Dependabot moves the pins with every release of that repository. Until 2026-10 the same code was
four byte-identical copies held together by a `.github/shared-files.sha256` manifest per repository.

The `repository-checks` job of `publish.yml` (gating both publish jobs) runs its checks, configured
in **`.github/actions-config.json`**:

- **templates**: every file this repository keeps equal to a template there (community files,
  licenses, `.editorconfig`, `.gitattributes`, `.java-version`, `.mvn/`, Dependabot, the workflow
  callers, `scorecard.yml`, the Claude workflows) still equals it, up to the repository's name and
  the action pins. To change one, change the template in bernardladenthin/actions; `python3 -m
  actionlib.common.templates --write` (with its `lib/` on `PYTHONPATH`) writes it here;
- **release gate**: every job gates both publish jobs unless `.github/release-gate-exemptions.txt`
  names it with a reason;
- **run scripts**: `bash -n` over every bash `run:` script of the workflows and local actions, so a
  broken script (a lost line continuation, say) fails here instead of in the job running it;
- **Maven versions** (warnings only): a dependency or plugin (incl. the Spotless formatter version)
  used here in another version than in a sibling of the `java-maven` family (each sibling's POMs come
  from its own configuration), and Kotlin Gradle plugins behind the POMs' Kotlin;
- **publication**: the publication jobs are wired as the actions expect, and a dry run of
  `.github/release-assets.json` against the real POMs collects exactly what it promises.

The JDK every workflow uses is `.java-version` (setup-java's `java-version-file`), a template of
the `java` set. Why a repository instead of copies:
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md).

## Open TODOs

Open TODOs for this repo live in [`TODO.md`](TODO.md). Cross-repo status
tracking lives in [`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md).
