# srcmorph (core library)

`net.ladenthin:srcmorph` is the framework-free Java API behind the
[srcmorph](../README.md) project: a prompt-driven source-tree transformer built on local llama.cpp
(GGUF) models. It has **no dependency on the Maven Plugin API** (enforced by an ArchUnit rule —
see `CoreArchitectureTest#coreIsMavenFree`), so it can be embedded in any JVM tool: a Maven plugin (see
[`../srcmorph-maven-plugin/`](../srcmorph-maven-plugin/README.md)), a standalone CLI
(see [`../srcmorph-cli/`](../srcmorph-cli/README.md)), a Gradle plugin, a test harness, or plain code.

Package root: `net.ladenthin.srcmorph`.

## The shared configuration object: `SrcMorphConfiguration`

`net.ladenthin.srcmorph.config.SrcMorphConfiguration` is one mutable JavaBean holding everything a
run needs, bound by Jackson or set in plain Java. The CLI and the Maven plugin read it from one
configuration file, JSON or YAML, through `SrcMorphConfigurationFileLoader`, into a
`SrcMorphConfigurationFile` (`srcMorph`, `packageFieldGenerations`, `projectFieldGenerations`, and the
CLI's `command` / `modelFilter`): the extension picks the parser, a `${name}` in a value is the
caller's property, an unknown key fails, and relative paths resolve against the file's directory:
the directories and `runRecordFile` directly, a `modelPath`, `templateFile` or `factsFile` through
`modelDirectory` / `profileDirectory`, which the loader sets to the file's directory when they are
unset. A `SrcMorphConfiguration` built in code resolves its relative paths against the working
directory.

Major field groups:

| Group | Fields |
|---|---|
| Source selection | `baseDirectory`, `subtrees`, `excludes`, `fileExtensions`, `fileNames` (globs over the file name, in any case, for files without an extension), `minFileSizeBytes`/`maxFileSizeBytes`; a glob takes `*`, `**`, `?` and `{a,b}` (`support.AiGlob`) |
| Output | `outputDirectory`, `force`, `regenerateOnProfileChange` (default `true`: a file summary whose header's `P` or `N` differs from its rule's prompt or fact counters is rewritten; `false` records them only), `planOnly`, `runRecordFile` (the JSON record of a file phase, `engine.RunRecord`) |
| Routing & prompts | `promptDefinitions` (`List<AiPromptDefinition>`: a `key` and a `template` or a `templateFile`), `profileDirectory` (the profile: a rule's `promptKey` / `factsKey` without a definition is read from `<key>.md` / `<key>.tsv` there, and a relative `templateFile` / `factsFile` resolves against it), `aiDefinitions` (`List<AiModelDefinition>`), `modelDirectory` (a relative `modelPath` resolves against it, an absolute one is used as it is), `fieldGenerations` (`List<AiFieldGenerationConfig>` — the routing rules), `factDefinitions` (`List<AiFactDefinition>`: a `key` and `facts` or a `factsFile`) |
| AI backend | `generationProvider` (`"mock"`, `"llamacpp-jni"` or `"openai-compatible"`; a model definition's own `provider` overrides it); every model parameter is a field of an `aiDefinitions` entry |
| Header metadata | `pluginVersion`, `aiVersion`, `projectName` |

Every other `config/` class is a plain, Maven-annotation-free JavaBean too: `AiModelDefinition` (a
named, complete set of GGUF sampling parameters), `AiPromptDefinition` (a named prompt template —
its `template` string is the system instructions, used verbatim; `AiPromptSupport` automatically
appends the file/package name and the source text as a separate user message, so a template needs no
placeholder), `AiFieldGenerationConfig` (one routing rule: a
`condition` tree, `priority`, and either a route, a `skip`, or the explicit `fallback`),
`AiCondition`/`AiConditionGroup`/`AiRangeCondition` (the composable and/or/not condition tree over
extensions/size/lines/modified-time/path-glob), `AiFactCounter`/`AiFactDefinition` (deterministic
regex-count "facts" prepended to a generated body), `AiOversizeStrategy` (`fail`/`sample`/
`mapReduce`/`sampleChunks`/`deterministic` — what a rule does when a file is larger than its model's
context window), `AiCalibration` (per-machine measured throughput, pasted onto an `AiModelDefinition`).

## The five engines

Each engine is constructed from one `SrcMorphConfiguration` and owns its own AI provider lifecycle
(try-with-resources; one model resident in memory at a time). All five throw the checked
`net.ladenthin.srcmorph.engine.SrcMorphException` on misconfiguration (an invalid rule set, an
unmatched file with no fallback, an oversized file with `onOversize=fail`, a bad prompt/model
reference) and, at the end of a run, when a file, package or overview got an incomplete answer (empty,
cut off or none; see `onIncomplete`), which is never written; a plain `java.io.IOException`
propagates for genuine I/O failures — callers (the
Maven plugin's mojos, the CLI's `Main`) translate these into whatever their own surface expects.

| Engine | Purpose | Result |
|---|---|---|
| `GenerateEngine` | Phase 1: plans the whole run (which model + prompt each file gets, or skip/unmatched, and whether it fits its routed model's context window), fails fast on an unmatched file or a hard oversize failure, stops after the plan when `planOnly` is set, otherwise loads each distinct routed model once and indexes its files. | `GenerateResult` (`planOnly()`, `written()`, `unchanged()`, `skipped()`) |
| `AggregatePackagesEngine` | Phase 2: aggregates every package beneath the output directory into a `package.ai.md` per directory, with the one rule of `fieldGenerations` (exactly one: its prompt and model). | `int` — package index files written |
| `AggregateProjectEngine` | Phase 3: harvests every package's one-line lead into a single `project.ai.md` table of contents. Fully deterministic by default (no model call); with a rule in `fieldGenerations` (at most one), one extra AI call writes a short `#### Overview` paragraph. | `int` — `1` if written, `0` otherwise |
| `CalibrateEngine` | Loads each distinct model a `GenerateEngine` run would load, measures its prefill/decode throughput via a couple of representative generations, and returns a paste-ready report. | `CalibrationReport` (`measurements()`, a paste-ready YAML `calibration:` block per model for its `aiDefinitions` entry, and the JSON / YAML report files) |
| `ListModelsEngine` | Asks each OpenAI-compatible server the `aiDefinitions` name for its models (`GET <baseUrl>/models`), optionally filtered by id. | `ModelListReport` (`listings()`, `render()` — a model table as YAML comments and an `aiDefinitions` block with one entry per model) |

**One configuration, three meanings of `fieldGenerations`.** `GenerateEngine` *routes* each file to
exactly one matching rule (by `condition` + `priority`, with the explicit `fallback` catching the
rest). The aggregation engines route nothing: `AggregatePackagesEngine` takes exactly one rule and
`AggregateProjectEngine` none or one, and both fail on more, so a routing table meant for the files
never reaches a package or the project. Give each phase its own rules: the top-level
`packageFieldGenerations` / `projectFieldGenerations` of the configuration file, which the CLI and
the plugin's aggregate goals read.

## Use as a library

```java
import java.io.File;
import java.util.Collections;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.GenerateEngine;
import net.ladenthin.srcmorph.engine.GenerateResult;
import net.ladenthin.srcmorph.engine.SrcMorphException;

SrcMorphConfiguration config = new SrcMorphConfiguration();
config.setBaseDirectory(new File("."));
config.setOutputDirectory(new File("target/srcmorph-ai"));
config.setGenerationProvider("mock"); // or "llamacpp-jni" with a real GGUF model below

// the profile: srcmorph's profiles/v3/ copied into the project; a rule's promptKey names
// <key>.md there (an AiPromptDefinition with setTemplate("...") writes a prompt inline instead)
config.setProfileDirectory(new File("src/srcmorph/profile"));

AiModelDefinition model = new AiModelDefinition();
model.setKey("coder");
model.setModelPath("/path/to/model.gguf"); // unused by the mock provider
config.setAiDefinitions(Collections.singletonList(model));

AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
rule.setPromptKey("file-body-fallback");
rule.setAiDefinitionKey("coder");
rule.setFallback(true);
config.setFieldGenerations(Collections.singletonList(rule));

try {
    GenerateResult result = new GenerateEngine(config).execute();
    System.out.println("written=" + result.written() + " unchanged=" + result.unchanged());
} catch (SrcMorphException | java.io.IOException e) {
    // handle: misconfiguration vs. I/O failure
}
```

## Provider abstraction

`net.ladenthin.srcmorph.provider.AiGenerationProvider` (a `Closeable`) abstracts the AI backend.
`AiGenerationProviderFactory` looks up an implementation by name:

- `"mock"` (default) — `MockAiGenerationProvider`, deterministic canned responses; no model, no JNI,
  no native library required. Used throughout every module's test suite.
- `"llamacpp-jni"` — `LlamaCppJniAiGenerationProvider`, backed by
  [`net.ladenthin:llama`](https://central.sonatype.com/artifact/net.ladenthin/llama) (the llama.cpp
  JNI binding). This is the **only** package in this library that touches the JNI binding
  (`CoreArchitectureTest#jniConfinedToProvider`).
- `"openai-compatible"` — `OpenAiCompatibleAiGenerationProvider`, HTTP to
  `<baseUrl>/chat/completions` of any OpenAI-compatible API (OpenRouter by default, `llama-server`,
  jllama's `NativeServer`), with the key from the environment variable a model's `apiKeyEnv` names.

## Package layout

```
net.ladenthin.srcmorph/
├── config/     mutable JavaBeans; SrcMorphConfiguration is the shared root object
├── engine/     GenerateEngine, AggregatePackagesEngine, AggregateProjectEngine, CalibrateEngine,
│               ListModelsEngine, SrcMorphException, GenerateResult, CalibrationReport,
│               ModelListReport, EngineSupport
├── indexer/    the walk/plan/write orchestration engines delegate to
├── document/   the .ai.md model + codecs (AiMdDocument, AiMdHeader, AiMdDocumentCodec, ...)
├── prompt/     prompt template lookup + preparation (trimming to a model's context window)
├── provider/   the AI backend abstraction (see above)
└── support/    foundation helpers with no dependency on anything above them
```

See [`../CLAUDE.md`](../CLAUDE.md) for the full architecture rules (`CoreArchitectureTest`), the
layered-package dependency direction, and the PIT mutation-testing scope (currently 78 classes at
100%).

## Testing

Every test in this module is model-free by default (`MockAiGenerationProvider`); a handful of
real-model tests are gated on `src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf` and self-skip
when the native library is unavailable. See the root [`TEST_WRITING_GUIDE.md`](../TEST_WRITING_GUIDE.md)
for conventions and [`../CLAUDE.md`](../CLAUDE.md) for how to run a single test class.
