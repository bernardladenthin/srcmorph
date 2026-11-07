# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

The release procedure (prompt template and step-by-step instructions) lives in [docs/RELEASE.md](./docs/RELEASE.md).

---

## [Unreleased]

### Upgrading from 1.4.0

The entries below hold the details; what a 1.4.0 user changes:

- **A rule naming `promptKey: file-body-java` names `file-body-oop`:** profile v3 has no
  `file-body-java` any more.
- **Configuration values that read differently now:** a `{` in an `excludes`, `pathGlob` or
  `fileNames` glob opens an alternative (`{a,b}`) and an unclosed one fails the run (a glob has no
  escape for a literal brace; `?` matches it); `$${name}` writes a literal `${name}` (it was left as written); a key
  defined twice in `aiDefinitions`, `promptDefinitions` or `factDefinitions` fails the run instead of
  the last one winning.
- **The plugin's `G` is srcmorph's version**, no longer the project's: a reader or a script that took
  the header's `G` for the project version reads `projectName` and the build instead.
- **The first run rewrites every summary**, one model call each: a summary of 1.4.0 records no `M`
  and no `K`, the project's `C` is built differently, and every file prompt of profile v3 changed (the
  secrets rule). To keep the old summaries instead, set all five `regenerateOn` switches to `false`
  and keep them there: a field is recorded only when its summary is written, so a run with the
  switches on later still finds it missing and rewrites then. `force: true` rewrites everything at once.
- **Java callers of the library** (the CLI and the plugin need nothing): `AiMdHeader.withHashes`
  takes `(prompt, facts, model, children)`; the `PackageIndexer` and both `ProjectIndexer`
  constructors take a `RegenerateOn` after `force`; the `SourceFileIndexer` constructor takes
  `fileNames` and `fileInterpreters` (after `fileExtensions`), a `RegenerateOn` (after `force`)
  and `contentHeadChars` (last); `SourceFileIndexer.indexFile` takes a `modelHash` (last);
  `AiMdHeaderSupport`, now `AiMdRewritePolicy`, takes the five switches as booleans (without
  arguments, all on).
  `GenerationTimeEstimator` (was `AiGenerationTimeEstimator`) is linear: `REQUEST_OVERHEAD_MS`, `PREFILL_MS_PER_TOKEN` and
  `DECODE_MS_PER_TOKEN` replace the four CPU coefficients, and `estimateDecodeMillis` takes only the
  output tokens. `IncompleteAnswerException.Reason` has a fourth value, `ABORTED`.
  `AiGenerationUsage.add` is `GenerationUsage.withCall` (one call's usage). `GenerationProvider` has
  `takeLoadNanos()`.
- **Java callers of the library, the package restructure** (the configuration file, the CLI's
  commands and the plugin's goals and properties stay as they are; see the entry under Changed):
  fix the imports of the moved classes and rename `LlamaCppJniConfig` to `ProviderConfig`,
  `LlamaCppJniConfigFactory` to `ProviderConfigFactory`, `AiFieldGenerationSupport` to
  `SummaryGenerator`.
  `GenerationRequest` takes three arguments (no header); `SummaryGenerator.generate` takes four
  (no header). `AiConditionEvaluator.CONTENT_HEAD_CHARS` is
  `SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS`, `AiFactExtractor.READS_PER_CHAR` and
  `READ_ALLOWANCE` are `ReadBudget`'s, and
  `OpenAiCompatibleAiGenerationProvider.withoutUserInfo(text)` is `UrlUserInfo.mask(text)`. A
  plugin subclass extends `AbstractSrcMorphMojo` instead of `AbstractAiIndexMojo`.
- **Java callers of the library, the null-safety review** (the configuration file, the CLI and the
  plugin stay as they are): `RoutingRule.getPromptKey()` / `getAiDefinitionKey()` (was `AiFieldGenerationConfig`) are
  `@Nullable` (a skip rule has none); a rule that routes gives them through `requirePromptKey()` /
  `requireAiDefinitionKey()`. `SrcMorphConfiguration.getBaseDirectory()` is `@Nullable` until set, and
  an engine without one throws a `SrcMorphException` instead of a `NullPointerException`.
  `ProviderConfig.builder()` takes no argument: `.modelPath(path)` sets the model file, and
  `modelPath()` is `null` rather than `""` for a model without one (a remote model); the llama.cpp JNI
  provider refuses such a configuration in its constructor. The `getKey()` of `ModelDefinition`,
  `PromptDefinition` and `FactDefinition` and `FactCounter.getLabel()` / `getPattern()` are
  `@Nullable`, and a definition without a key throws an `IllegalArgumentException` instead of a
  `NullPointerException`. The overview constructor of `ProjectIndexer` takes one `ProjectOverview`
  (provider, rule, prompt and model lookups) instead of the four values; the four-argument
  `SourceFileIndexer.classify` takes both lookups non-null (the two-argument one skips the window
  check). `GenerationProviderFactory.effectiveProvider` takes a non-null run provider and returns a
  non-null key, and `PromptTemplateLoader.load(List)` is gone (`load(List, Collection)` remains).
- **A `size` condition measures the source as srcmorph reads it**: the bytes the checksum is taken
  over, `\n` line endings and no byte order mark (a UTF-16 file: the UTF-8 length of its text), not
  the bytes on disk. On a CRLF checkout, or for a file with a byte order mark, a file near a tier's
  bound can reach another rule than in 1.4.0. The run's `minFileSizeBytes` / `maxFileSizeBytes` band
  stays on the bytes on disk: it filters during the walk, before a file is read. Java callers: `SourceLines.normalize(byte[])` is not public any more
  (`SourceLines.checksumBytes` and `SourceLines.size` are).
- **A `baseUrl` with user info (`https://user:key@host/v1`), a query or a fragment fails the check.**
  srcmorph never sent the user info (`HttpURLConnection` does not authenticate with it), so such a
  model ran in 1.4.0 against a server without a key; put the key in the variable `apiKeyEnv` names
  and the path alone in `baseUrl`.
- **The self-test's gpt-oss rules map-reduce a file over the window** (`onOversize: mapReduce` on
  every rule, the default kinds too), so a configuration copied from it no longer fails a dense file
  just below a size tier.
- **Every configuration file renames three keys**: `fieldGenerations` is `rules` (under `srcMorph`),
  `packageFieldGenerations` is `packageRules` and `projectFieldGenerations` is `projectRules` (the
  loader refuses the old names as unknown keys). The beans' accessors follow (`getRules`,
  `getPackageRules`, `getProjectRules`; the CLI's `CConfiguration` fields too).
- **Java callers of the window and the parts**: `InputWindowCalculator.maxInputChars(config)` takes
  one argument and is the budget of the whole rendered prompt (it took the template out a second time);
  `SummaryGenerator.parts()` is `takeParts()`, which also starts the next file's answers anew.
- **A configuration that sets `pluginVersion` names it `generatorVersion`** (it is srcmorph's own
  version, the header's `G`, in the CLI and the plugin alike); the run record's `pluginVersion` is
  `generatorVersion` too, and so are the bean's getter and setter.
- **Java callers of the library, the naming review** (`docs/code-review-2026-11-05.md` section 2):
  `AiGenerationTimings` is `TimedGeneration`; `AiPromptPreparationSupport.getBasePromptLength` is
  `PromptPreparer.basePromptLength`; `RemoteModel.UNKNOWN_CONTEXT_LENGTH` is `-1` (it was `0`).
  `GgufModelInfo` has no sentinels (null-safety finding S10): it is made by `parsed(name,
  contextLength)` or `unreadable(failure)`, `modelName()` and `failure()` are `null` where they were
  `""`, `contextLength()` is an `OptionalLong`, and `UNKNOWN_CONTEXT_LENGTH` is gone. The CLI's
  `Main.logConfigurationTransformation`
  is `logConfigurationEcho`, `configurationToYAML` is `configurationToYaml`, and `readString`,
  `fromJson`, `fromYaml`, `configurationToJson` and `configurationToYaml` are package-private.
  `AiMdHeader`'s accessors are named (the file keeps its letters): `h()` is `headerVersion()`, `c()`
  `checksum()`, `d()` `sourceDate()`, `t()` `generatedAt()`, `g()` `generatorVersion()`, `a()`
  `aiVersion()`, `x()` `nodeType()`, `p()` `promptHash()`, `n()` `factsHash()`, `m()` `modelHash()`
  and `k()` `childrenHash()`.
  `AiIndexPlan.Entry` is `IndexPlan.Route`, and
  `SourceFileIndexer.recordedHashes` returns a `RecordedHashes` (`promptHash()`, `factsHash()`) instead
  of a `Map.Entry`.
  `AiGenerationConfig` is gone: `ModelRegistry.definition` (was `AiModelDefinitionSupport.getConfig`)
  returns a copy of the `ModelDefinition` with its model path resolved, `ProviderConfigFactory` builds
  a `ProviderConfig` from it, and the `DEFAULT_*` constants are `ModelDefinition`'s (`maxInputChars` was never
  configurable: `DEFAULT_MAX_INPUT_CHARS` is the fallback without `charsPerToken`).
  The `Ai` prefix is gone but on the `AiMd*` classes of the `.ai.md` format, and the `*Support`
  names that hid a role say it: `AiChecksumSupport` `ChecksumSupport`, `AiGeneratorVersion`
  `GeneratorVersion`, `AiGlob` `Glob`, `AiPathSupport` `SourcePaths`, `AiProfileFileSupport`
  `ProfileFileSupport`, `AiShebang` `Shebang`, `AiSourceExcludeFilter` `SourceExcludeFilter`,
  `AiSourceLines` `SourceLines`, `AiTimeSupport` `TimeSupport`, `AiCalibration` `Calibration`,
  `AiCondition` `RuleCondition`, `AiConditionGroup` `ConditionGroup`, `AiFactCounter` `FactCounter`,
  `AiFactDefinition` `FactDefinition`, `AiFieldGenerationConfig` `RoutingRule`,
  `AiIncompleteStrategy` `IncompleteStrategy`, `AiModelDefinition` `ModelDefinition`,
  `AiModelDefinitionSupport` `ModelRegistry`, `AiOversizeStrategy` `OversizeStrategy`,
  `AiPromptDefinition` `PromptDefinition`, `AiRangeCondition` `RangeCondition`, `AiRegenerateOn`
  `RegenerateOn`, `AiFactDefinitionLoader` `FactDefinitionLoader`, `AiFactDefinitionSupport`
  `FactGroups`, `AiFactExtractor` `FactExtractor`, `AiConditionEvaluator` `ConditionEvaluator`,
  `AiFieldGenerationSelector` `RuleSelector`, `AiFileContext` `FileContext`, `AiGenerationRequest`
  `GenerationRequest`, `AiPreparedPrompt` `PreparedPrompt`, `AiPromptPreparationSupport`
  `PromptPreparer`, `AiPromptSupport` `PromptCatalog`, `AiPromptTemplateLoader`
  `PromptTemplateLoader`, `AiMdHeaderSupport` `AiMdRewritePolicy`, `AiCompletionParser`
  `CompletionParser`, `AiContextOverflowException` `ContextOverflowException`,
  `AiGenerationProvider` `GenerationProvider`, `AiGenerationProviderFactory`
  `GenerationProviderFactory`, `AiGenerationUsage` `GenerationUsage`, `AiIncompleteAnswerException`
  `IncompleteAnswerException`, `LlamaCppJniAiGenerationProvider` `LlamaCppJniGenerationProvider`,
  `MockAiGenerationProvider` `MockGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
  `OpenAiCompatibleGenerationProvider`, `AiCalibrationMeasurement` `CalibrationMeasurement`,
  `AiCalibrationRunner` `CalibrationRunner`, `AiHeaderHashes` `HeaderHashes`, `AiIndexOutcome`
  `IndexOutcome`, `AiIndexPlan` `IndexPlan`, `AiIndexTally` `IndexTally`, `AiDeterministicSummary`
  `DeterministicSummary`, `AiGenerationResult` `SummaryResult`, `AiGenerationTimeEstimator`
  `GenerationTimeEstimator`, `AiInputWindowCalculator` `InputWindowCalculator`, `AiMapReduceSupport`
  `MapReduceMath`, `AiOverflowBackoff` `OverflowBackoff`, `AiRouteEstimator` `RouteEstimator`,
  `AiSourceChunker` `SourceChunker`, `AiTokenWindow` `TokenWindow`, `AiProgressBar` `ProgressBar`.
  `ModelRegistry.getConfig` is `definition`, `AiMdRewritePolicy.shouldWrite` is `shouldRewrite`,
  `ProviderConfigFactory.fromGenerationConfig` is `from`.
- **Module-path consumers of the library see `config` and `engine` only**: `module-info.java` exports
  those two (and `support` to the CLI alone), no longer `document`, `facts`, `indexer`,
  `indexer.generation`, `prompt`, `provider` and `routing`. The engines' results name none of their
  types: `CalibrationReport` is read through its three renderings (its constructor, `measurements()`
  and `ModelMeasurement` are package-private), `ModelListReport` through `render()` (its constructor,
  `listings()` and `Listing` likewise), and `RunRecord`, which no engine returns, is package-private;
  `CoreArchitectureTest#exportedApiNamesNoInternalType` keeps it that way. On the classpath (the CLI,
  the plugin, a Java 8 consumer) nothing changes but these visibilities.
- **A script that reads srcmorph's log lines**: the file phase names a file by its path below the
  base directory (it was the absolute path), and the plan's model header and its per-file estimate
  line have a new form (the entries under Changed).
- **The plan, the run record and the progress list files of several subtrees in one path order**
  (`src/extra/...` before `src/main/...`), no longer subtree by subtree in configured order.
- **Plan times change:** lower for map-reduce files whose rounds end in a single partial, lower for
  multi-byte (CJK) sources, and 0 s for a chunked file whose window leaves less than 1000 chars for a
  chunk. **Such a file fails the plan and `check`** (the "Over window" section says `FAILS BUILD: N
  chars for a chunk`), as an `onOversize=fail` file does; the run used to fail it file by file. A
  context overflow of a prompt of 1000 chars or fewer is not
  retried; the retry warning's "(A -> B chars)" names the refused prompt's length as A.
- **Java callers of the generation classes:** `RouteEstimator.estimateRouteSeconds(...)` has no
  `fileSizeBytes` parameter, `MapReduceMath.sourceBudget` is gone; `SourceFileIndexer.sortByPath` and
  `SourceLines.cutIndex` are new.
- **`.jspx` and `.tagx` take `factsKey: jspx-facts`** (jsp-facts with XML comments blanked); a copied
  configuration splits its jsp rule the same way. A configuration that copied the `secrets` rule
  takes its new form (the self-test's), which keeps CI workflows, Helm templates and Android
  settings.
- **A `baseUrl` that holds an `@` anywhere fails the check** (user info before the host, a password
  with a `/`, an `@` in the path), and **a 3xx answer is an error**: srcmorph follows no redirect, so
  `baseUrl` names the final server.

### Added

- **`partsDirectory` keeps the map-reduce answers of each file:** every map answer and every reduce
  batch's answer of a file read in chunks, in `<partsDirectory>/<path below the base>.parts.md`, so a
  measurement can see where a summary loses what a part said (TODO row 32). Unset, nothing is kept.
- **A rule for `package.json`: `file-body-ops` with `npm-facts`.** The counters count the entries of
  `scripts`, the four dependency objects and `engines`, the strings of `workspaces`, and a VS Code
  extension's contributed commands and language model tools; `file-body-ops` has a line for an npm
  manifest (scripts, dependencies, engines, workspaces, an extension's commands, chat participants and
  tools), so every ops file's prompt hash changes once. `run.py` routes `package.json` there. Measured
  on the manifest of a VS Code extension (internal, three runs each): of its 45 names gpt-oss-20b named
  45, 28 and 34 (with the fallback prompt 45, 13, 28 and 15; with the rule but the ops prompt as it was
  21, 21 and 28, without the extension's 17 tools), GPT-6 Luna 45 every time (43 to 45).
- **The fact counters are measured linear on Java 8 as well:** the `linearity-jdk8` job of
  `publish.yml` runs `FactPatternLinearity`, the measure of `ProfileFactPatternsLinearityTest` written
  in Java 8, on a Java 8 JDK, since the counters' time limit counts the reads of a regex engine that
  differs between JDKs. It gates both publish jobs.

- **`$${name}` writes a literal `${name}`** in a configuration value, whether a property of that
  name exists or not: an inline prompt can show a shell variable (`$${HOME}`) that `-DHOME=...` or a
  Maven property would otherwise replace. In a path it still fails the load, as an unreplaced
  reference does.
- **TypeScript takes `file-body-oop`:** a rule for `.ts`, `.mts` and `.cts` in `run.py` and in the
  Luna example (no counters of its own). Measured on 2026-11-05 on a TypeScript repository,
  internally: with the oop prompt both models named more of the exported API than with the
  fallback, gpt-oss-20b clearly more, also of the private helpers and the tests.
- **`examples/copilot-api-gateway/srcmorph-luna.yaml`:** GPT-6 Luna through the GitHub Copilot API
  Gateway for every file, package and the project, with the whole profile v3: a rule per kind of
  source with its prompt and exact fact counters, and `onOversize: mapReduce` on every rule, the
  package and project rules too: an input larger than the window is read chunk by chunk, never
  sampled. It takes every extension of the self-test profile and leaves out `target/`, `.git/`,
  `.idea/`, `.vscode/`, `.gradle/`, `node_modules/` and `.claude/` (agent worktrees, a second copy
  of the sources). `ExamplesConfigBindingTest` pins the routing, the facts, the strategy and the
  chunk prompts. It routes every kind profile v3 knows (below): Java with Jakarta EE imports
  or annotations written with their package (`contentMatches`) to the EE counters, Kotlin, Groovy
  and Scala to the shared OOP prompt, the EE descriptors and mappings by name (`fileNames`), MyBatis
  mappers and integration routes by their DTD or namespace, BPMN, DMN, WSDL, JSP, Facelets, OpenAPI
  and AsyncAPI by their first key, Protobuf, Avro and GraphQL, GitHub Actions by path, Kubernetes by
  `kind:`, Compose files by name, Ansible playbooks and roles, Terraform, Camel's YAML DSL by its
  `- route:` / `- from:`, Dockerfiles and Jenkinsfiles by name, Gradle build scripts of both DSLs
  and `.properties` files to the ops prompt, PowerShell and batch files to the fallback prompt with
  their counters, and scripts without an extension by their `#!` line (`fileInterpreters`).
  `ExamplesConfigBindingTest` routes one file of each.
- **Benchmark: the enterprise corpus** ([`docs/ai-index-benchmark/corpus-enterprise/`](docs/ai-index-benchmark/corpus-enterprise/README.md)),
  seventeen files of those kinds around one shop backoffice, with `modelcard/facts-enterprise.json`.
  `run.py` routes the kinds its corpora hold as the Luna example does (not AsyncAPI, Ansible, Camel
  YAML, PowerShell, batch or scripts by their `#!` line, which none of them has) and takes
  `--java-prompt`, so the Java
  prompt and the shared OOP prompt run side by side on the card corpus; `score.py` reports
  `secretsLeaked`, the summaries that copy a value the facts mark as a secret.
- **`ProfileFactPatternsLinearityTest` measures that every pattern of every facts file of the
  profile reads linearly**, the `[ignore]` lines included, over 121 adversarial lines, each also repeated
  within one line (unclosed
  openers and tags, heredoc markers, unbalanced parentheses, runs of one character). It counts the
  characters the matcher reads instead of timing it: four times the input may read at most six
  times as many (linear reads four times, quadratic sixteen), so the result is the same on any
  machine. A facts file added to the profile is measured without a change to the test. Against the
  facts files of 1.4.0 it reports six quadratic patterns, all fixed above.
- **Files without an extension can be indexed and routed** (`fileNames`, under `srcMorph` and as a
  condition leaf): globs over the file name without its directory, matched in any case
  (`Dockerfile`, `Jenkinsfile`, `Dockerfile.*`); a name with a `/` fails the run in both places (it
  could never match: use `pathGlob`). Up to now only `fileExtensions` selected a file, so a Dockerfile
  or a Jenkinsfile could not be indexed at all.
- **Routing by content** (`contentMatches`, a condition leaf): a Java regular expression found in the
  first `contentHeadChars` characters of the file (under `srcMorph`, 16,384 by default, at least 256
  so the `interpreters` condition sees a whole `#!` line; raise it for a marker behind a long license
  header: the head is never read past the file's end), so a Kubernetes manifest, an OpenAPI document and a Camel
  route, all `.yaml`, take different rules (`(?m)^apiVersion:`). Planning reads each candidate once, whole;
  an invalid expression fails the check. The pattern is
  compiled once and has the read budget of a fact pattern: one that backtracks past it does not match,
  with a warning, so it cannot hang the plan.
- **Scripts without an extension can be indexed and routed by their `#!` line**
  (`fileInterpreters` under `srcMorph`, `interpreters` as a condition leaf): `[bash, sh, perl]`
  selects a `bin/deploy` that starts with `#!/usr/bin/env bash`. The interpreter is the program the
  line names, or what `env` runs (options and assignments skipped); a name matches its versions too
  (`python` matches `python3.12`; a name ending with a digit only from a dot on: `python3.1` does not
  match `python3.12`), and the arguments of `env`'s options (GNU `-u`, `-C`, `-a`, BSD `-P`, `-L`,
  `-U`) and its `-S` command line are read as `env` reads them. The line is read only for a file the subtrees, excludes and size band let
  through; one that cannot be read is skipped with a warning, the run goes on. `support.Shebang` holds the rule; an interpreter given with its directory
  (`/bin/bash`) fails the run.
- **Fact counters can count before the blanking and count names** (`[raw]` and `[distinct]` in a
  facts file, `raw` and `distinct` in a configuration): a raw counter counts the source as written,
  so a `TODO` in a comment counts next to counters that skip comments; a distinct counter counts the
  different texts it matched, its first group where that took part (twelve `@Inject Clock` are one
  type). A summary written before keeps its facts hash; the markers enter it only where set.
- **Every fact pattern has a time limit**: it may read 2,000 characters per character of the source
  plus 1,000,000, counted by the matcher's reads, so the limit is the same on every machine. A
  pattern of a user's own that backtracks past it is stopped: its counter is left out of the facts
  line, an ignore counter blanks nothing, and a warning names it. The profile's patterns read far
  less (`profiles/README.md` gives the measured maximum); the linearity test asserts that none
  reaches the limit.
- **Fact counters for the kinds that had a prompt and none**: `scala-facts`, `proto-facts`,
  `avro-facts`, `graphql-facts`, `asyncapi-facts`, `terraform-facts` (OpenTofu and Terragrunt too),
  `ansible-facts`, `compose-facts` and `camel-yaml-facts`; `dmn-facts` for DMN decisions (inputs,
  outputs, rules, hit policies other than UNIQUE, the decision requirements); `gradle-facts` and
  `properties-facts`, and `powershell-facts` and `batch-facts` for the fallback prompt.
  `ContractFactPatternsTest`, `OpsFactPatternsTest`, `JvmFactPatternsTest` and (DMN)
  `EnterpriseXmlFactPatternsTest` pin each on a sample
  of its kind with a commented-out or quoted declaration that must not count. The YAML ones blank
  the body of a block scalar from the indentation of its first line, as YAML reads it, and keep its
  key (`ansible.builtin.shell: |` counts).
- **`{a,b}` alternatives in every glob** (`excludes`, `pathGlob`, `fileNames`), nested too:
  `**/*.{yml,yaml}`, `**/{target,build}/**`. The syntax lives once in `support.Glob`; a glob that
  opens a `{` it does not close fails the run, naming it.

- **Profile v3 covers the kinds of an enterprise Java code base**, with four prompts and fifteen
  fact files, so one generic prompt serves every language of a kind:
  - `file-body-oop`: one prompt for the class-based languages (Java, Kotlin, Groovy, Scala); it
    replaces `file-body-java` (below), with
    their facts per language: `kotlin-facts` (types, companion objects, data and sealed types,
    functions, extension and suspend functions, constructor properties, nested comments and string
    templates blanked), `groovy-facts` (types, typed and `def` methods, Spock features and blocks,
    closures, GStrings and slashy strings blanked) and `java-ee-facts` (`java-facts` plus the EJB,
    JPA, CDI, JAX-RS, JAX-WS, Servlet, JTA and security annotations).
  - `file-body-xml` defines Jakarta EE descriptors, JPA and Hibernate mappings, MyBatis mappers,
    Camel, Spring Integration, Mule and Synapse routes, BPMN and DMN, and WSDL, each counted by its
    own file: `jee-facts`, `persistence-facts`, `mybatis-facts` (which reads the SQL in CDATA),
    `integration-facts`, `bpmn-facts` (the diagram blanked) and `wsdl-facts`.
  - `file-body-view` for JSP, tag files and Facelets (`jsp-facts`, `facelets-facts`): the data a
    page reads, the actions it sends, its template and its server code.
  - `file-body-contract` for OpenAPI, AsyncAPI, JSON Schema, Avro, Protobuf and GraphQL
    (`openapi-facts`, YAML or JSON, block scalars blanked) and `file-body-ops` for CI pipelines,
    Dockerfiles, Compose, Kubernetes, Helm, Ansible and Terraform (`k8s-facts`, `actions-facts`,
    `dockerfile-facts`).

  `JvmFactPatternsTest`, `EnterpriseXmlFactPatternsTest`, `ViewFactPatternsTest` and
  `OpsFactPatternsTest` pin every counter on a sample of its kind with its traps; the linearity
  test measures every pattern.
- **A facts file can include another** (`[include]<TAB><key>`): its counters, `[ignore]` lines
  included, at the place of the line, so `jee-facts` is `xml-facts` plus the descriptor counters and
  copies none. A cycle fails the run naming the chain; a label two of the files use fails as before.
- **Secrets stay out of the index.** Every file prompt names a password, private key, token, API key
  or credentials in a URL without copying it (`DB_PASSWORD=***`): the shell, XML and fallback
  prompts asked for "every value", so a `<password>` of a data source or a token in a script went
  into the summary, and the default output directory `src/site/ai` is published with the Maven
  site. The self-test, `examples/build-your-own-jar/srcmorph.yaml` and the Luna example skip the
  files that hold credentials (`id: secrets`: `.env`, `*.tfvars`, `*.tfstate`, Maven's
  `settings.xml`, keys, `*secret*` and `*credential*` configuration files), so they never reach a
  model; `ExamplesConfigBindingTest` pins which names it skips and that no source file is among
  them.

- **The CLI runs on the module path in CI:** the `smoke-jar` job plans the demo tree with
  `java -p ... -m net.ladenthin.srcmorph.cli/...`, the only run that reads the `module-info.java`
  files (what the core exports, what the CLI opens to Jackson).
- **`modelcard/correlate.py`** computes the model card's correlation of an index's size with its
  search scores from `scores/` (Pearson and Spearman, the card's 32 runs by default).

### Changed

- **The self-test's large tiers start at ~260 KB, where gpt-oss-20b's window really ends.** They
  started at 275,000 bytes, but the 96K window leaves about 270,000 characters next to the XML
  prompt, so a 272 KB Java, SQL, XML or POM file failed the run's plan (`onOversize: fail`). And
  every other kind the self-test routes (shell, Perl, TypeScript, JSP, BPMN, DMN, Protocol Buffers,
  GitHub Actions, Kubernetes, Dockerfiles, Terraform, npm, `ops` and the fallback) map-reduces a
  source over the window now instead of failing the run: one 400 KB OpenAPI file did. The CLI's
  `build-your-own-jar/srcmorph.yaml` the same; its header says what its one subtree indexes and how
  to widen it.
- **Counters closer to the source (the facts hash of these kinds changes, so their summaries are
  written again):** the integration counter's property placeholders no longer count Camel Simple
  expressions (`${body}`, `${header.id}`), Facelets input fields no longer count
  `<f:selectItem(s)>`, Dockerfiles blank BuildKit heredoc bodies, PowerShell counts a quoted
  dot-source and no `Mandatory = $false`, Gradle reads the Kotlin DSL's bare plugins and
  `tasks.<name> { }`, Kotlin no longer counts a `when (val x = ...)` subject as a constructor
  property, Java counts an annotation element with a `default` as a method, GitHub Actions no
  longer counts a reusable-workflow call as an action, and Compose no longer counts the `$${X}`
  escape as a variable.
- **Every fact counter reads linearly within one line too, and more of them count what their label
  says** (the facts hash of these kinds changes, so their summaries are written again). The linearity
  test also repeats each adversarial fragment on one long line (`\'`, `#{`, `${`, `rpc a(b`, ...),
  which found string literals full of escaped quotes, nested placeholders and Java declarations
  under thousands of annotations reading quadratically or overflowing the stack; a literal not closed
  on its line is blanked to the line end now (java, kotlin, groovy, gradle, scala, proto, terraform,
  graphql), an unclosed SQL `'` to the end of the file, a PowerShell string across lines, and the
  rest of a shell or Perl heredoc-opener line that cannot be read to its end. Counters:
  integration property placeholders count Mule's and Spring's `${env}`, `${file.dir}` and
  `${type.handler}` again (only Camel's Simple forms are left out); a Dockerfile heredoc opens only
  where `<<` starts a word on a RUN, COPY or ADD line (also a continued one); Java methods and
  constructors count a wrapped `throws` and a brace on the next line, and `@java.lang.Override`;
  Kotlin extension functions take a nested generic receiver; properties count a secret key that
  starts with the keyword (`password=`); Gradle's included builds are `include` at a line start,
  its dependencies count after `{` or `;`; GitHub Actions count every secret in an expression;
  Compose counts `$NAME`; Ansible counts a quoted `become`; batch blanks `& rem`; proto's streaming
  rpcs read CRLF files; MyBatis and properties placeholders count the innermost of nested ones.
  SQL's `'\'` (T-SQL, PostgreSQL, Oracle) and `ESCAPE '\'` close their literal instead of blanking
  the rest of the file (a backslash escapes a quote only where no delimiter follows, as MySQL's
  `'it\'s'`); a Dockerfile `<<` inside quotes opens no heredoc, and a second heredoc on one line is
  blanked too; Compose counts the substitutions in a block scalar (`command: >`), which it
  interpolates, and none in a comment.
- **The model card's scorer reads what the model answered.** `score.py` strips a leading reasoning
  block before recall, invented names, numbers and the format checks (the leak is still counted),
  matches a path- or colon-like name whole (`/orders` is not found in `/orders/{orderId}`; a Perl
  arrow, `JSON::PP->new`, still ends a name), opens a code fence only where CommonMark does (at a
  line start, or indented to its list item's content), reads the methods of a static interface and
  annotation elements, takes no digits stuck to a letter or a date part as a stated number, splits a
  name after an acronym (`XMLParser`), and takes `order.c` or `item.tag` for a member, not a file. A
  complete answer after a closed reasoning block is a leak, not a leak-only failure; one whose block
  never closes is. `run.py` maps a package call's log name to its `package.ai.md` again, so a cut
  package counts as cut, and runs the project phase after some packages failed; `search.py` reads
  the answer the same way and keeps a leading dot of a path (`.github/...`). The card's numbers do
  not move (its outputs have none of these cases). Two published numbers elsewhere differ from a
  re-score: the generic-prompt languages run 3 gives gpt-oss-20b 94.9 % instead of 95.3 % (the mean
  95.5 % instead of 95.7 %), because `invoice-service.jar` no longer credits the name
  `invoice-service`; and the large-corpus run 2 of 2026-11-01 gives gpt-oss-20b 49.1 % instead of
  55.2 % (a stricter format check of an earlier round), so the gpt-oss mean over the three large runs
  re-scores to 76.0 %, published 78.1 % (run 3 keeps its 88.3 %). The writeups keep the numbers they
  were published with. `scores/search.json` predates `search.py` reading the text without the
  facts and coverage lines: an offline BM25 re-run moves five runs' MRR by up to 0.016 (hit@1 and
  hit@3 stay), and the reader's numbers were taken on the old text too; renewing them is a paid
  re-run, so the file stays as published.
- **The model card's search prose and caveats match its data:** BM25 over the summaries finds 67 to
  91 % first (it said 67 to 86 %), 81 % on average against 84 % over the source, and nine of the 35
  runs beat the source; the index length goes with the reader's hit@1 at
  −0.18 over the 32 scored runs (it said −0.46, from an older set of runs); Muse's best search ties
  gpt-oss-20b on hit@1 and leads on the MRR. The caveats add that recall does not measure
  faithfulness (a bare name list scores 100 %), that three runs against three reach p = 0.1 at best
  in Fisher's test and that search was compared unpaired (McNemar per question was not run), and
  that the prompts were tuned on the corpora they are scored on. No table or number of
  `report.py` moved.
- **The self-test routes the new kinds measured on real projects:** TypeScript, the Jakarta EE
  descriptors, MyBatis mappers, BPMN, DMN, JSP, Protocol Buffers, GitHub Actions, Kubernetes
  manifests, Dockerfiles, Terraform, npm manifests and other YAML (`ops`), with the Luna example's
  rules on the self-test's model, and the extensions and file names they need. Measured on
  jpetstore-6, microservices-demo and camunda-bpm-examples (218 files of 13 kinds, 1072 names
  derived from the sources, two runs each): with the kinds' rules gpt-oss-20b names 97.3 % of the
  names, with every file through the fallback prompt 81.2 %; GPT-6 Luna 97.7 % against 92.2 %. No
  kind is worse with its rule for gpt-oss-20b; for Luna the Spring XML files through the `xml` rule
  are (90 % against 100 %). The kinds not measured on a real project (persistence and integration
  XML, WSDL, Facelets, OpenAPI, AsyncAPI, Avro, GraphQL, Camel YAML, Compose, Ansible, Gradle,
  properties, PowerShell, batch, Kotlin, Groovy, Scala, Java EE classes) stay out of the self-test.
  The Luna example routes `package.json` too.

- **The self-test's `big-pom` rule takes `file-body-xml`** instead of the generic prompt: on a real
  590 KB POM Granite H-Tiny invents no names with it and about half of them with the generic one, at
  the same low recall (6 and 4 of 99; `profiles/README.md`). `huge-pom` is unchanged.

- **The core library's packages say what their classes do** (the review of 2026-11-05,
  `docs/code-review-2026-11-05.md`, moves M1 to M10 and renames N1 to N3). `config` holds only the
  beans and the file loader: the routing (`AiConditionEvaluator`, `AiFieldGenerationSelector`,
  `AiFileContext`) is the new package `routing`, the fact counters (`AiFactExtractor`,
  `AiFactDefinitionLoader`, `AiFactDefinitionSupport`) the new package `facts`, and
  `AiPromptDefinition`, a bean like the others, moved from `prompt` into `config`. The generation of
  one summary body is the new sub-package `indexer.generation`: `SummaryGenerator` (was
  `AiFieldGenerationSupport`) with `AiMapReduceSupport`, `AiTokenWindow`, `AiOverflowBackoff`,
  `AiInputWindowCalculator`, `AiRouteEstimator`, `AiGenerationResult` (from `document`) and
  `AiSourceChunker`, `AiDeterministicSummary`, `AiGenerationTimeEstimator` (from `support`).
  `AiGenerationRequest` moved from `document` to `prompt` and lost the header it carried and no
  provider read; `AiProgressBar` moved from `support` to `engine`. `support` gained `ReadBudget` (the
  read limit of a pattern, from `AiFactExtractor`) and `UrlUserInfo` (the masking of a URL's user
  info, which the CLI reached through a provider class). `LlamaCppJniConfig` is `ProviderConfig`,
  since it configures every provider, and the plugin's base class `AbstractSrcMorphMojo`. The ArchUnit
  layering names every package with the exact layers that use it: `config` no longer depends on
  `prompt`, `prompt` and `provider` no longer on `document`, and a new rule keeps
  `indexer.generation` from depending on the walkers.
- **The map-reduce decisions are on the PIT gate.** `SummaryGenerator` kept the chunk plan, the
  packing and shortening of a reduce round and the decision on an incomplete answer next to its
  provider calls, off the gate; they are the package-private `ChunkPlan`, `ReduceRound` and
  `IncompleteAnswerAction` now, 46 mutations, all killed. The behavior is unchanged.
- **The benchmark's runs and tools are pinned better.** `run.py`'s meta records the srcmorph commit
  (`+dirty` with local changes), the Python version and a SHA-256 over the corpus; a test pins the
  bytes `large.py` writes at `--scale 1.0`, so a Python whose `random` draws differently fails it.
  `score.py` scores only the files srcmorph indexes and reads a source as srcmorph decodes it (UTF-8,
  UTF-16 after a byte order mark, U+FFFD for an invalid byte); `report.py` takes `--card` and a
  `--help` that does not render. A mutation pass over the tools found most of their logic untested:
  `score.py` now has 20 of 114 mutants surviving (41 before), `report.py` 5 of 17 (15).
- **The run record and the plan say more of where the time and the tokens go.** The record's totals
  name the files the plan skipped (`skipped`), the run's wall time (`wallSeconds`) and each model's
  load time (`modelLoadSeconds`): a local model loads on its first file, whose own seconds leave the
  load out now (`GenerationProvider.takeLoadNanos`, `0` by default). The plan's model header adds
  the tokens the model is sent (`~N source tokens (~M sent)`): none of a file `deterministic`
  summarizes, one window of a `sample` or `fail`, `maxChunks` windows of a `sampleChunks`. Its
  map-reduce estimate packs as many part summaries into a reduce call as fit the window, as the run
  does, instead of always 16 (`MapReduceMath.expectedFanIn`), so a narrow window estimates more
  reduce calls.
- **`requestsPerMinute` keeps one window per server**: two models of one server (same `baseUrl` and
  `apiKeyEnv`) with 20 and 10 requests a minute had two windows and sent up to 30 together. Now every
  request waits until fewer than its own model's limit started in the server's window, whichever
  model started them, so the model at 10 waits for the other's requests too. The window lives as
  long as the process, so a later run of a long-lived JVM (mvnd, an IDE, an embedder) that names a
  higher limit sends at it.
- **`G` is srcmorph's own version, in the CLI and the plugin alike.** The plugin recorded the
  version of the user's project as the generator and the CLI recorded none; a summary now names the
  srcmorph release that wrote it (`support.GeneratorVersion`, which the core's build fills in), and
  `generatorVersion` in the file still overrides it. `G` never causes a rewrite, so no summary is
  rewritten for this.
- **The examples, the self-test and `run.py` ask once more on an incomplete answer**
  (`onIncomplete: retryOnce` on every rule, the package and project rules too). gpt-oss-20b ran
  away to the output budget on one big TypeScript file in six runs and summarized the same file in
  the next one; a larger budget only made the runaway dearer (measured on 2026-11-05: finished
  answers took 1229 to 1618 tokens, the runaway 16384). `run.py` records it in the meta
  (`onIncomplete`); the runs before failed such a file at once.
- **The time estimate is based on GPT-6 Luna**, the benchmark's remote model, where it was a local
  CPU (gpt-oss-20b on a Ryzen 7 5800H): `2.1 s + 0.105 ms` per prompt token `+ 6.45 ms` per output
  token, fitted over 1423 calls of the 2026-11 measurements and linear up to their largest prompts
  (98K tokens), 4.0 characters per token, 1000 tokens of prompt around the source, 950 output tokens
  assumed. For a 24K-token prompt the CPU model said 25 min, Luna takes about 12 s, so a remote run's
  plan was off by more than an order of magnitude. The per-file log line ends in
  `(rough; based on GPT-6 Luna)`,
  and the plan names the basis in every model's header; a model with a `calibration` reads `from
  the model's calibration` in both instead. A local model is far slower than the estimate:
  `Calibrate` measures it.
- **`SourceFileIndexer` is final**, and its public constructor takes `fileNames`,
  `fileInterpreters`, `regenerateOn` and `contentHeadChars` (Upgrading from 1.4.0). The file phase's log lines name a file by its path below
  the base directory (as the model reads it), with the system's separators, where they named the
  absolute path.
- **Java takes `file-body-oop`; `file-body-java` is gone from profile v3.** A rule naming
  `promptKey: file-body-java` fails the run (no such file in the profile): name `file-body-oop`.
  Three runs each with both prompts on the model-card corpus gave the same Java recall, GPT-6 Luna
  100 % in all three with the shared prompt
  ([`remeasure-2026-11-04/`](docs/ai-index-benchmark/remeasure-2026-11-04/README.md)). The self-test,
  the examples, the plugin-it fixture and `run.py` (`--java-prompt`, default `file-body-oop`) name it.
- **What rewrites a summary is set per cause** (`regenerateOn` under `srcMorph`: `source`,
  `prompt`, `facts`, `model`, `children`, each `true` by default), next to `force`, which rewrites
  every summary whatever the switches say. Each switch compares one header field: `C` (the source; a
  package's files, the project's packages), `P` (the rule's prompts, map and reduce included), `N`
  (its fact counters), the new `M` (the model: definition key, `remoteModel`, model file name) and
  the new `K` (the child summaries a package or the project is written from). A missing or empty
  summary, another header format, node, title or `aiVersion` rewrite it always; `D`, `T` and `G` are
  recorded only. Up to now only a changed source, `aiVersion` or `force` rewrote a summary (1.4.0
  recorded `P` and `N` without acting on them), so a summary of an older prompt or model stayed; and
  a package was never rewritten when its files were re-summarized over unchanged sources, since its
  checksum is built from theirs, nor when its own prompt changed: packages and the project now
  record `P`, `M` and `K` too (the project's checksum is built from its packages and their
  checksums, as a package's from its children, no longer folded with the overview's prompt key and
  model). `P` reads a prompt's line endings as `\n`, so a profile checked out with CRLF on one machine
  and LF on another hashes the same. A field a summary does not record counts as changed, so the
  first run after the upgrade rewrites every summary, one model call each (Upgrading from 1.4.0 says
  how to keep them). `AiMdRewritePolicyTest` pins every switch on and off,
  `PackageIndexerTest` and `ProjectIndexerTest` the children and overview hashes; the plugin README's
  header section says what rewrites a summary, and every example configuration shows the switches.
- **`java-facts` counts code only**: an `[ignore]` line blanks comments, Javadoc, text blocks,
  strings and character literals, so a `public` or `class` in them no longer counts, and
  `TODO/FIXME` is a `[raw]` counter, counted in the comments as before. The facts line of a Java file
  with such text changes (`java-ee-facts` includes it), and so does the facts hash, which with
  `regenerateOn.facts` rewrites the Java summaries that carry facts.
- **DMN has its own counters**: `bpmn-facts` counts BPMN only (its "decisions" and "decision rules"
  went to `dmn-facts`), and the Luna example and `run.py` route `.dmn` to `dmn-facts`.
- **The model reads a file's path below the base directory**, not its name alone, at the head of the
  user message (`billing/src/main/webapp/WEB-INF/web.xml`), so two files of one name in different
  modules read apart; every file prompt of profile v3 says so in its last paragraph. The plan's
  window check counts the same line. A package's message starts with the package's path (see
  Fixed), the project's with `project.ai.md`.
- **Profile v3's file prompts ask for more**
  ([`remeasure-2026-11-04b/`](docs/ai-index-benchmark/remeasure-2026-11-04b/README.md): the enterprise
  corpus 97.9 to 99.7 % with gpt-oss-20b, 97.3 to 99.9 % with GPT-6 Luna; the card corpus 99.5 % and
  100 %, the language corpus 99.0 % over nine gpt-oss runs and 99.5 %): `file-body-oop` names the private
  functions, constants and fields the logic uses and each call on a dependency, which the enterprise
  runs found by chance only (`nextNumber`, `PREFIX`, a `refund` call); `file-body-ops` names a CI
  workflow or pipeline by its name (`release-backoffice` was missing in every gpt-oss run) and
  defines Gradle build and settings scripts (plugins, dependencies by configuration, repositories,
  tasks, included projects, the toolchain), which take it now.
- **`pathGlob` matches in the case written**, also on Windows, so a rule routes the same file on
  every system; `fileNames` and `extensions` match in any case, as names of a kind are written in
  either. Decided and documented, unchanged.
- **A `{` in a glob opens an alternative**; it matched itself before. An exclude or a `pathGlob`
  with a literal brace pair now means its alternatives.
- **A source file with a byte order mark is read without it**, and one with the UTF-16 mark as
  UTF-16. Before, a UTF-8 file saved with a mark (as some Windows editors do) began with U+FEFF, so
  its first line did not start with its first word for the fact counters and the prompt, and a
  UTF-16 file read as replacement characters. The checksum of a UTF-8 file is still over its bytes,
  so such a file is not rewritten for this alone; a UTF-16 file's is over its text (see Fixed).

### Fixed

- **A `size` condition measures the same file on every checkout.** It read the bytes on disk, so a
  file checked out with CRLF line endings was larger than the same file with LF and could reach
  another size tier, model and summary (a 272 KB Java file, 5000 lines). It measures the source as
  srcmorph reads it now: the bytes the checksum is taken over, `\n` line endings and no byte order
  mark (a UTF-16 file: the UTF-8 length of its text); the plan's time estimate of a file that fits
  does too. The run's `minFileSizeBytes` /
  `maxFileSizeBytes` band stays a filter on the bytes on disk.
- **A package's lead in `project.ai.md` is its summary's lead** again for a package read in chunks
  (packages default to `mapReduce`) or with counters: the project listed the coverage or the facts
  line, which stands in front of the summary, as the package's lead.
- **A UTF-16 source has one checksum on a CRLF and an LF checkout.** Its line endings were normalized
  byte by byte, which turns the two bytes of a UTF-16 `\r\n` into two line feeds, so a checkout
  switch rewrote its summary; its checksum is taken over its normalized text now.
- **A leading `Src/` stays in the summary's path on every platform**: `Path.startsWith` ignores the
  case on Windows, so the summary of `Src/main/Foo.java` lost its `Src` there and kept it on Linux.
- **A model id with a line break cannot break out of `ListModels`' output**: control characters and
  YAML line breaks are written as `\uXXXX` in the table and the paste-ready block.
- **A `baseUrl` with a query or a fragment fails the check**: srcmorph appends `/chat/completions`
  to it, which landed in the query, and the query (possibly a key) was printed with the endpoint.
  Every URL srcmorph prints (a message, a log line, `ListModels`, the CLI's configuration echo)
  shows its query and fragment as `?***` now, as it shows user info as `***@`.
- **A subtree that is `src` itself** (`subtrees: ["src"]`, or a POM's `<sourceDirectory>src`) no
  longer fails the package phase: its summaries' root is the output directory.
- **An interrupt ends the package pass**, as it ends the file phase, instead of failing every
  remaining package; a timeout is still one package's failure.
- **A request that failed its retries with a timeout last** says "failed N times, the last with a
  timeout", not "timed out N times" after three 503 answers and one timeout.
- **A password with `'`, `?` or `#` in a `baseUrl` is masked and refused as user info.**
- **A long password in a URL is masked too.** The mask of a URL's user info stopped at 256
  characters, so a longer token (a JWT) stayed in the message that refuses such a `baseUrl`; an `@`
  in the password left its tail visible. It masks up to the last `@` before the host now, of any
  length, and `toString` of a model definition and of a provider's configuration masks the
  `baseUrl` as well.
- **A `lazyMode`, `cacheTypeK` or `cacheTypeV` the binding does not know fails the check**, before a
  model is loaded, instead of the first file's load; and a local model that does not load is tried
  once per run: every later file fails with the same error at once instead of loading the model
  again.
- **One unchecked provider error no longer ends the package pass**: the package is counted as failed
  and the others are written, as in the file phase (the llama.cpp binding throws only unchecked
  exceptions, so one context overflow left every later package unwritten).

- **A package call names the package**, as `package-body` and `profiles/README.md` say: its user
  message started with `package.ai.md` (the file name of the absolute path), now with the package's
  path below the output root (`main/java/com/example`; the root package by the output directory's name).

- **A prompt or facts file checked out with CRLF line endings reads as with LF.** Its hash ignored the
  line endings already, but the template was sent and measured as written, so the same profile gave
  another window and other chunk boundaries on Windows than on Linux.

- **`-Dsrcmorph.planOnly=true` reaches the aggregation goals too.** Only `generate` read it, so a
  build with the self-test profile planned the file phase and then let `aggregate-packages` and
  `aggregate-project` load the model and rewrite every package; they do not run now, as the CLI's
  `All` command never did.
- **The window counted the prompt template twice.** `InputWindowCalculator.maxInputChars` took the
  template out of the budget the whole prompt (template and source) is then compared with, so every
  file lost one template's length of window (3 to 6 KB with profile v3's prompts) and some that fit
  were read in chunks, or failed under `onOversize: fail`. `maxInputChars(config)` is the budget of
  the rendered prompt now (the window less the output and the safety margin), and the source gets it
  less the template, once; the plan and the run still agree. Java callers drop the second argument.
- **A phase rule's counters from an inline `factDefinitions` entry are checked before a model loads**:
  they were resolved after the rule's check, so a pattern that does not compile failed the run at the
  first package instead.
- **`partsDirectory` no longer writes a chunked file's answers under the next file's name** when that
  file was unchanged (or failed before it was read): `SummaryGenerator.takeParts()` returns the
  answers once and starts anew (`parts()` is gone).
- **Files are planned and summarized in path order, on every machine.** The walk of a subtree came
  in the file system's order, so the plan, the progress and the run record listed files differently on
  Linux and Windows, and a test that relied on two files' order failed on one CI runner and not on
  another. The candidates are sorted by their path with `/` separators now.

- **A template that opens the thinking block itself leaves no reasoning in the summary.** Qwen3 and
  DeepSeek-R1 templates put `<think>` into the prompt, so the answer starts with the reasoning and
  holds only `</think>`; `CompletionParser` kept all of it. The first `</think>` alone on its line,
  with no `<think>` before it, now ends the reasoning, and without one the first that ends its line
  glued to the reasoning's last word (`I will write it.</think>`) when no line before it starts
  like an answer (a `>` lead, a code fence); a tag inside a line, or after a
  space, a quote or a backtick (a sentence, quotes, a list item naming it) is text, in the answer or
  in reasoning that names it before its real end.
- **The benchmark's scorer** (`score.py`, `report.py`; the card's published numbers stay until it is
  re-rendered): an everyday word between two backtick spans no longer counts as marked as code, one
  in a column listing with its type (`email VARCHAR(254)`) does; a reasoning marker in backticks is
  no leak, and a complete summary after a reasoning block is a leak, not a leak-only failure; the
  card's invented-name rate sums the names of every kind instead of averaging the kinds' rates
  (`summary.json` carries `ticked` and `invented`; `report.py` refuses one without them);
  `CHECKOUT`, `RETURN`, `Since` and `histogram` are everyday words of their files.
- **The scorer's copy filter and invented names** (TODO row 43; the card stays until it is
  re-rendered): a `~~~` fence, an indented code block and a copy with its whitespace changed copy the
  source as a ``` fence does, and do not count. A name is invented unless it is an identifier of the
  source, a run of an identifier's word parts (split at `_` and at a capital) or their plural, so a
  piece inside a word (`ame` of `getName`) no longer passes as a substring; the names are those in
  backticks and the code-shaped words of the prose (`orderTotal`, `OrderService`, `order_total`), and
  the names of languages, tools and products (`JavaBean`, `PostgreSQL`) and Python's builtin types
  are none. Over the outputs of 2026-10 no recall changes, and the invented-name rate of a model's
  kind rises by at most 1.3 per 100 names; qwen3-coder-30b's SQL falls from 18.6 to 3.7, since its
  few backticked names are joined by the names of its prose.
- **The fact counters read more of what they count, and less of what they do not** (every facts
  hash of the changed files changes, so `regenerateOn.facts` rewrites their summaries once):
  - shell: a `<<` after up to three groups in parentheses (`$(( (a) + ((b)) << n ))`) is a shift,
    and `MSG=${MSG:-a b}` is an assignment, not the environment prefix of a command;
  - Perl: a `<<` after a hash element (`$h{x} <<BITS`, `$r->{k}`) is a shift, one inside `q{}`,
    `qq//`, `qw`, `qr`, `m//`, `s///`, `y` or `tr` opens no heredoc (`print {$fh} <<EOT` still does);
  - SQL: an apostrophe in PostgreSQL's one-line dollar quoting, Oracle's `q'[...]'`, a MySQL
    `"..."` string or a MySQL `#` comment opens no literal that hid the next statements (quoted
    identifiers, T-SQL `#temp` tables and multi-line `$$` function bodies stay); MariaDB's
    `CREATE OR REPLACE TABLE`, PostgreSQL's `CREATE FOREIGN TABLE` and T-SQL's `COLUMNSTORE`
    indexes count; MySQL's `VALUES(col)` of `ON DUPLICATE KEY UPDATE` is no row;
  - Java: annotations on the declaration's own line, type parameters three deep and parameters
    that start on the next line count; `yield f(x);`, a record header and an enum constant with a
    body (`PLUS("+") {`) are no method or constructor;
  - XML: `id` and `name` count inside a tag only, not as `name = 'a'` in element text;
  - a blanked region keeps every line terminator Java's regex reads (NEL, U+2028, U+2029 too), so
    `^` sees the same lines as before.
- **The fallback prompt's example `MAX_RETRIES` is `POOL_SIZE`**: `MAX_RETRIES` is a scored name of
  the language corpus, which a model could copy from the prompt (the prompt hash changes).
- **An answer the provider stopped was written as a finished summary.** OpenRouter answers a
  generation that failed half-way with a 2xx, the text so far and finish reason `error`; only
  `length` counted as incomplete, so the summary ended mid-sentence, without a warning (measured on
  2026-11-05: gpt-oss-20b, 694 tokens, ending in `test paging (`). Finish reason `error` or
  `content_filter` is now an incomplete answer of the new reason `ABORTED`: not written and left for
  the next run (`onIncomplete: retryOnce` asks once more first), never kept as a part or under
  `onIncomplete: keep`; the calibration warns about it. Any other reason (`stop`, a server's own
  word for it, none) is the model's, as before.
- **An ignored region at the start of a line left a space there**, so a counter anchored at the line
  start (`^class`, `^import`, Perl's `^my`) missed the code after it: `/* x */class A {}` counted no
  type, the same code on two lines one. Such a region now leaves nothing; within a line it still
  leaves a space, so the words around it stay apart. The facts hash is unchanged (it hashes the
  counters, not the counts).
- **A JSON configuration file with a UTF-8 byte order mark did not load**: Windows PowerShell 5.1
  writes one (`Out-File -Encoding utf8`), and Jackson read it as a character before the `{`. The
  loader reads a configuration file as it reads a source, without the mark (YAML too).
- **A key defined twice in `aiDefinitions`, `promptDefinitions` or `factDefinitions` was taken
  silently, the last one winning**, so a rule could run with another model or prompt than the one
  read first. It now fails the run, naming the list, the index and the key.
- **`docs/copilot-api-gateway.md`** showed `maxOutputTokens: 2048` for the example, which has 4096.
- **The XML and POM fact counters of profile v3 were quadratic over unclosed markup.** In
  `xml-facts.tsv` the `[ignore]` line scanned to the end of the file again from every `<!--`,
  `<![CDATA[`, `<?` or `<!DOCTYPE` that is never closed (100 KB of unclosed `<?` took 8.4 s; a
  `<!DOCTYPE r [ ]` without `>` repeated ran for minutes), and so did the XSD counter from every
  unclosed `<complexType`/`<simpleType`; in `pom-facts.tsv` the comment and CDATA patterns and the
  plugin-body pattern from every unclosed `<plugin>`. An unclosed region now runs to the end of the
  file, as in `sql-facts.tsv`, a type tag ends at the next `<`, and a nested plugin body at the next
  plugin tag. Well-formed files count as before (the corpus counts are unchanged); 11 new
  `XmlFactPatternsTest` cases pin the unclosed regions and the run time of each pattern.
- **Three more fact counters were quadratic**, found by the new linearity test: in `java-facts.tsv`
  the type arguments of "methods (approx)" and "field declarations" and the field initializer read
  across lines to the end of the file from every declaration without its `>` or `;`, and in
  `shell-facts.tsv` "dash-option case arms" backtracked over a long run of blanks. Type arguments are
  now read within one line, an initializer up to 10,000 characters (a generic type split over lines,
  or a longer initializer, is no longer counted; `JavaStructureFactPatternsTest` pins both), and the
  blanks possessively.
- **search.py no longer overwrites the card's search scores with a partial run.** With the card's
  scores/ directory, `--no-reader` (which nulled every reader result) or a non-default `--out` (which
  replaced the card's runs) is refused with a hint to pass a `--scores` of its own, as repeat.py
  already refuses `--runs` there.
- **run.py hands each model only its own API key.** The CLI's environment drops every `apiKeyEnv`
  named in models.json except the one the model's definition names; `COPILOT_GATEWAY_API_KEY`
  reached every model's subprocess, against the script's docstring.
- **search.py's reader no longer loses or freezes answers.** An empty answer is asked again up to
  four times and never cached (one already in an older cache is asked again too); `"usage": null`
  no longer raises before a paid answer is cached; a 200 whose body is no JSON or is cut off is
  retried like a 5xx.
- **score.py counts a heading indented by one to three spaces**, as CommonMark reads it; no published
  summary has one, so the card's figures do not change. Three of its tests are renamed or split to
  say what they check.
- **SQL: an identifier's closing quote opens no "..." string.** In `INSERT INTO "cfg" ("data")
  VALUES ('{"k": "v"}')` the `[ignore]` alternative for a MySQL string with an apostrophe ran from the
  closing quote of `"data"` to the JSON literal's first `"`, and the literal's apostrophe, left
  unpaired, opened a literal over the rest of the file (INSERT 1, VALUES rows 0, tables 0, indexes 0
  of 2, 2, 1, 1).
- **A SQL*Plus remark or pg_dump's COPY rows open no SQL string literal.** The sql-facts `[ignore]`
  line opened a `'...'` literal at any apostrophe, also on a line that is no SQL: `REM Creates the
  shop's schema` / `PROMPT Don't ...` before two `CREATE TABLE` and one `CREATE INDEX` counted tables 0
  and indexes 0, now 2 and 1; a pg_dump whose `COPY ... FROM stdin` rows hold an `O'Brien` hid the
  `ALTER TABLE` and index after them (0 and 0, now 1 and 1). A `REM[ARK]` / `PRO[MPT]` line and the
  rows of a `COPY ... FROM stdin` up to `\.` are blanked; without the `\.` line the data runs to the
  end of the file, as psql reads it.
- **A Groovy method with a generic return type counts.** "methods (approx)" read the type arguments
  possessively up to the line end, which ate their `>`: `List<Order> all() {`, `Map<String,Integer>
  counts(int x) {`, `public static List<String> names() {` and `String plain() {` counted 1, now 4. An
  annotation on the method's line and a brace on the next line count too.
- **A type-use annotation after a field's modifier no longer hides the field** (java-facts, and so
  java-ee-facts): `@Nullable private String a;`, `private @Nullable String b;` and
  `private final @NonNull List<String> c = ...;` counted 1, now 3.
- **A CDI, Guice or Dagger `@Singleton` is no EJB session bean.** java-ee-facts counted every bare
  `@Singleton` (`import javax.inject.Singleton; @Singleton class Cache {}` counted 1, now 0). An EJB
  singleton counts through its `jakarta.ejb` / `javax.ejb` import or when written with its package;
  one imported through `javax.ejb.*` is no longer seen.
- **Kotlin object expressions, annotated and vararg constructors count as written:** two
  `object : Runnable {` expressions next to `object Registry` and `data object Empty` counted 4 type
  declarations, now 2; `@Inject constructor(...)` and `@JvmOverloads internal constructor(...)` 0
  secondary constructors, now 2; `vararg val` and `final val` 0 of 3 constructor properties, now 3.
- **Scala package objects, infix defs and abstract type members count as written:**
  `package object shop {` counted 0 type declarations, now 1; `infix def max(...)` and
  `private infix def min(...)` 0 methods, now 2; `type T <: AnyRef`, `type U` and
  `type V >: Null <: F[A => B]` next to five aliases 8 type aliases, now 5.
- **A context overflow's retry sends a shorter prompt, not the same one again.** The next window was
  75 % of the window budget, so a prompt far below it (a dense 12 000-char prompt in a 30 000-char
  window, on a provider that cannot count tokens) was sent four times unchanged and the file failed:
  a `mapReduce` rule never chunked it. The next window is 75 % of the refused prompt; a prompt of
  1000 chars or fewer is not sent again.
- **A trimmed prompt reads the long line behind a short one.** The trim backed up to the last line
  break before the window, so a cut map call of a one-line (minified) chunk kept only its label
  ("Chunk 3 of 9, half 2, half 1:"), and the sampled head of "header + one long line" read the header
  alone. A line break that keeps less than half of what fits is no longer the cut.
- **No cut splits a character outside the Basic Multilingual Plane:** trimmed prompts, chunks,
  shortened part summaries, halved chunks and the counted head stop before a surrogate pair.
  `SourceChunker`'s Javadoc no longer claims a `maxChunks` of 1 keeps the last chunk.
- **The whole plan is in path order:** files were sorted within each subtree and the subtrees taken in
  configured order; all candidates are sorted by path together now, as this file said they were.
- **An interrupt ends the file run and the package pass however it surfaces.** A
  `ClosedByInterruptException`, or any `IOException` after the interrupt flag was set, was taken as
  one file's or one package's failure, and every remaining one then failed too.
- **The plan's time counts what the run does:** no reduce call for a one-partial batch that is passed
  on (17 partials: 2, not 3); characters, not bytes (a CJK source was about 3 times too long); 0 s
  for a mapReduce or sampleChunks file whose window leaves less than 1000 chars for a chunk, which the
  run refuses; the plan and `check` fail on such a file now, before a model loads, naming the room
  its chunks would get.
- **A `null` rule in a list built in code fails the run naming it** (`Invalid rules: srcMorph.rules[1]
  is null`) instead of a `NullPointerException`; the configuration file's loader refuses one already.
  The classes that skip a `null` rule or counter say so in their parameter types
  (`? extends @Nullable`), null-safety finding S9.
- **A deterministic body fences a Markdown source safely:** the fence is one backtick longer than the
  longest backtick run in the lines it shows (at least three), so a ``` line in the source no longer
  closes the block.
- **A `#` in a JSON string no longer blanks the rest of an OpenAPI or AsyncAPI spec.** A
  double-quoted string holding a `#` is blanked whole instead of opening a YAML comment: a one-line
  JSON spec with `"Fixes issue #12"` counted 0/0/0 paths, operations and operationIds, now 2/3/3.
- **An empty YAML block scalar no longer blanks its sibling keys** (asyncapi, compose, ansible,
  camel-yaml and openapi facts): the body must be deeper than its key, past a list item's dash, and
  the key line stays. After an empty `command: >` healthchecks and port lists went from 0/0 to 1/1.
- **properties-facts reads keys as `java.util.Properties` does:** a continuation line is no key; a
  key separated by a blank alone (`welcome message`) or with no value counts.
- **batch-facts counts no echoed text and no exit labels:** the text an echo prints is blanked, and
  "exits" counts the exit command, not `:exit` or `goto :exit` (6 to 2 on the pinned sample).
- **gradle-facts counts no plugin declarations, publications or task references:** plugins went from
  5 to 2 (`gradlePlugin { plugins { id } }`, developer ids), repositories from 2 to 1
  (`maven(MavenPublication)`), tasks from 3 to 1 (`dependsOn tasks.named(...)`).
- **JSP documents count their directive elements and blank their XML comments:** jsp-facts counts
  `<jsp:directive.page|include|attribute>`, and the new jspx-facts blanks XML comments (a .jspx went
  from 0/0/1/2 page directives, includes, scriptlets and EL to 1/1/0/1).
- **Counters that counted the wrong items:** GitHub Actions matrix entries are no steps (5 to 2);
  Ansible loop items are no tasks (5 to 2) and template expressions in block scalars count;
  PowerShell Verb-Noun calls count in any case and a function definition is no call; a GraphQL
  schema definition holds no fields and a field with nested argument parentheses counts; WSDL
  counters accept any element prefix and count an operation once; a manifest embedded in a ConfigMap
  counts no container; AsyncAPI message examples are no payloads (3 to 1); npm contributed commands
  count with their keys in any order (0 to 2).
- **file-body-oop names TypeScript, and file-body-xml indexes SQL, scripts and conditions in CDATA.**
- **The secrets skip rule skips credential files, not CI workflows, Helm templates or Android
  settings:** secret-scan.yml, a Helm templates/secret.yaml, vault-deployment.yaml and Android's
  res/xml/settings.xml are routed again; secrets.hcl and *.pkrvars.hcl are skipped. run.py gets the
  same rule and routes build.gradle.kts to gradle instead of kotlin.
- **A password with an unencoded `/` in a `baseUrl` is shown nowhere.** The mask stopped at the first
  `/`, so `https://user:pa/ss@host/v1` reached the CLI's configuration echo, the ListModels log line
  and error and both `toString`s unchanged; an `@` after a URL's first `/` masks everything after the
  scheme now (`https://***`). The check refuses every `baseUrl` that holds an `@`, including
  `https://u:123/abc@host/v1`, which parsed as host `u`, port `123`, and passed.
- **A redirect is not followed with the key.** The models GET followed a 30x with `Authorization` to
  whatever server the `Location` named, and a streamed POST reported a redirect as a refused key; a
  3xx fails the request now, naming the status and the masked `Location`.
- **A `</think>` alone on its line inside an answer is kept:** a summary that quoted the tag in a code
  fence lost its lead and was stored as complete from the fence on. The tag ends the reasoning only
  outside an open fence and before any `>` lead line.
- **A pattern that overflows the stack is stopped like one past its read budget.** A fact counter or
  `contentMatches` such as `(?:a|b)*` over a long text threw a `StackOverflowError` that aborted the
  plan or the run; the counter is left out now, an `[ignore]` line blanks nothing, the condition does
  not match, and a warning names the pattern.
- **Two ignored regions back to back at a line start** leave the line starting with what follows
  (`/* a *//* b */class A {}` counts as a `^class` line again), and **a facts file of `[ignore]` lines
  only can be included** (the file that includes it must still count something).
- **An answer the provider stopped inside open reasoning is ABORTED, not TRUNCATED:** one that ended
  with finish reason `error` or `content_filter` inside `<think>` asked to raise `maxOutputTokens`.
- **`requestsPerMinute` waits once, to the start that frees the window**, instead of once per start
  with a "Holding ..." line each; the server in that line and in `toString` is masked.

## [1.4.0] - 2026-11-02

### Upgrading from 1.3.0

The entries below hold the details; what a 1.3.0 user changes:

- **Maven plugin: the configuration moves into a file.** The goals' XML parameters are gone. What the
  POM's `<configuration>` held (`aiDefinitions`, `fieldGenerations`, `promptDefinitions`,
  `factDefinitions`, the paths, `generationProvider`, ...) goes into `srcmorph.yaml` next to the POM,
  under `srcMorph:`, in the CLI's format; the package and the project rule go into the top-level
  `packageFieldGenerations` / `projectFieldGenerations`. The POM keeps the goals, `configurationFile`,
  the skip switches and `srcmorph.force` / `srcmorph.planOnly`. Without the file every goal fails,
  naming where it looked. The worked example is in `srcmorph-maven-plugin/README.md`.
- **One rule per aggregation phase, no `llama*` run parameters.** `packageFieldGenerations` takes
  exactly one rule and `projectFieldGenerations` none or one; neither falls back to the file rules any
  more. `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature` and
  `llamaThreads` are removed: every model parameter is a field of an `aiDefinition`.
- **Paths resolve against the configuration file's directory**, not the working directory, for the
  CLI too; a `${name}` that no property sets fails the load when it stands in a path.
- **Defaults that changed:** the package and project rules map-reduce a long input
  (`onOversize: mapReduce`); an incomplete answer is never written (`onIncomplete`), so a file whose
  answer stays empty or cut off is reported, fails the run at its end and is tried again by the next
  run.
- **No runnable jar is attached to the release any more:** build one for your machine with
  `examples/build-your-own-jar` (root README).
- **Existing summaries stay:** a new srcmorph version alone regenerates nothing (the plugin version and
  the source's modification time are no longer compared); a summary 1.3.0 wrote from an incomplete
  answer is written again.

### Added
- **The plan shows the source tokens.** Every routed file has an estimated token count (its
  characters over its model's `charsPerToken`), every model section the sum, and a section sums them per
  directory, so the plan says where a run's tokens and cost go before it starts.
- **A record of every run.** With `runRecordFile` set, the file phase writes a JSON record: per routed
  file its rule, prompt, model, outcome, calls, prompt / cached / completion tokens, the finish reason
  of its last call, its wall time and the CRC32 of its prompt and its facts, and the totals
  (`engine.RunRecord`). The providers report each call's tokens and finish reason
  (`AiGenerationProvider.lastUsage()`, `provider.AiGenerationUsage`), also for a call that ended
  incomplete; `AiFieldGenerationSupport.takeUsage()` sums the calls of a file, a map-reduce's
  included.
- **`mapReduce` reads every chunk; sampling is `sampleChunks`, and every summary says what it read.**
  `onOversize: mapReduce` summarizes every chunk and reduces in batches until one summary is left; a
  `maxChunks` cap used to sample a subset silently, so a 1.6 MB dump was summarized from a third of it
  while the configuration read as mapReduce. Sampling is the new strategy `sampleChunks` with
  `maxChunks` (required there, and refused with any other strategy). An over-window summary begins
  with a coverage line: `**Coverage:** 9 of 9 chunks summarized`, `... 6 of 9 chunks summarized
  (sampled)`, or under `sample` `**Coverage:** the first N of M characters (sampled head)`. The
  plan's time estimate of `mapReduce` / `sampleChunks` counts the chunks with the run's budget and
  overlap and the reduce calls by the fan-in. The self-test and example configurations keep their
  six-chunk huge-file rules as `sampleChunks`. A rule-set error of `generate` (two fallbacks, a
  maxChunks without sampleChunks, ...) is a `SrcMorphException`, so the plugin reports it as a
  configuration error.
- **The jar smoke test counts what it planned.** `smoke-jar` runs `config_Plan.json` a second time
  and asserts the plan's one file of the demo tree, so a launch that finds no sources fails. The
  examples say they need 1.4.0 (`examples/build-your-own-jar/pom.xml`, the root README); stale
  comments and references in `publish.yml`, the poms and CLAUDE.md (versions, layout, test lists)
  match the code, and the root pom declares `sonar.organization` once.
- **The Maven plugin is configured by the CLI's file; `check` goal and command.** The plugin reads
  one JSON or YAML file, the same one the CLI reads (`<configurationFile>`,
  `-Dsrcmorph.configurationFile`, default `${project.basedir}/srcmorph.yaml`), through one loader in
  the core (`SrcMorphConfigurationFileLoader`, `SrcMorphConfigurationFile`). **Breaking:** the XML
  parameters of the goals are gone (`aiDefinitions`, `fieldGenerations`, `promptDefinitions`,
  `factDefinitions`, `subtrees`, `excludes`, `fileExtensions`, the size band, `outputDirectory`,
  `generationProvider`, `modelDirectory`, `profileDirectory`, `aiVersion` and their `srcmorph.*`
  properties); the POM names the goals, `configurationFile`, the skip switches and the run switches
  `srcmorph.force` / `srcmorph.planOnly`, which override the file when `true`. What the file leaves
  unset, Maven fills: `baseDirectory`, `subtrees` (the source directory and the resource directories
  that exist below the base directory), `projectName`, `pluginVersion`. A `${name}` in a value of the
  file is a Maven property (the CLI: a system property), filled after parsing, so a Windows path needs
  no escaping. Each goal takes its section: `generate` and `calibrate` the file rules,
  `aggregate-packages` the `packageFieldGenerations`, `aggregate-project` the
  `projectFieldGenerations`. The self-test profile
  is `srcmorph-maven-plugin/srcmorph-selftest.yaml` (its `-Dai.*` still work), with the repository
  root as base, so its two subtrees lie inside it. Relative paths of the file, for the CLI too,
  resolve against the file's directory instead of the working directory (the root README's quickstart
  from `examples/build-your-own-jar` works now); an unset CLI `baseDirectory` is that directory.
  `srcmorph:check` and the CLI's `Check` check the plan, the profile files and every model of the
  three phases (file and GGUF header, `baseUrl`, `apiKeyEnv`) without loading one
  (`GenerateEngine.check()`, `AggregatePackagesEngine.check()`, `AggregateProjectEngine.check()`). A
  rule naming a model no `aiDefinition` defines fails as a configuration error naming the rule, not
  as an `IllegalArgumentException`. The CLI prints its usage with `--help` / `-h` and fails with it
  on a wrong argument count (it used to log and exit 0); an unknown `command` names the commands. The
  plugin descriptor states Maven 3.6.3 and Java 1.8 (it said 3.10.0 and Java 11).
- **A source is read once, with `\n` line endings.** The checksum, the plan's window check, the
  prompt, the facts and the map-reduce chunks see the same text, whether the working copy has
  `\r\n` (Windows with `core.autocrlf`), `\r` or `\n`: a CRLF checkout no longer has another
  checksum than an LF one, and no `\r` reaches the model. The checksum is now taken over that text
  instead of the file's bytes, so a summary written from a CRLF working copy is regenerated once.
  The line count of a `lines` condition and the deterministic body count `\r\n` and `\r` as line
  endings too (`AiSourceLines`); the deterministic body no longer carries `\r` into its fenced lines.
  The pure parts of the field generation moved to classes on the PIT gate: the time estimate of the
  plan and of a run (`AiRouteEstimator`, one code path instead of two copies), the map-reduce sizing
  and reduce batches (`AiMapReduceSupport`), and the smaller window after a context overflow
  (`AiOverflowBackoff`, which also recognizes a remote `AiContextOverflowException` in a cause
  chain). A reduce round whose partials are too long to combine now goes straight to the last-resort
  single reduce instead of first summarizing every batch for nothing. `LlamaCppJniConfig$Builder`
  and `AiIndexPlan$Entry` are on the gate; `AiChecksumSupport.calculateCrc32Hex(Path)` is gone.
- **One rule per aggregation phase; the `llama*` fallback parameters are gone.** `aggregate-packages`
  takes exactly one rule (its prompt and model) and `aggregate-project` none or one; more fail the
  goal before anything is written, as does a missing package rule. Before, the package phase
  generated every package once per rule and kept the last answer, loaded the provider of the first
  rule's model whatever the others named, and the project overview took the first rule, so the file
  rules of a CLI run reached both phases unnoticed. The CLI's `packageFieldGenerations` /
  `projectFieldGenerations` no longer fall back to `srcMorph.fieldGenerations`: without them the
  package phase fails and the project index has no overview (`examples/config_All.json` sets both).
  The aggregation phases check their model before the run like `generate` does (model file, key,
  `baseUrl`). With a rule required, the run-level model parameters were dead: `llamaModelPath`,
  `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads` (`srcmorph.llama.*`)
  are removed from the plugin and from `SrcMorphConfiguration`, a CLI configuration naming one fails
  to bind, and `LlamaCppJniConfigFactory.fromFallbackParameters` is gone; every model parameter is a
  field of an `aiDefinition`. `AiFieldGenerationSupport.processFieldGenerations(List, …)` is
  `generate(rule, …)`. The indexers keep their rule as a full copy (`AiFieldGenerationConfig.copy()`);
  the project overview used to copy only the prompt, the model and `onIncomplete`, so it ignored its
  rule's `onOversize` and facts.
- **The engine plans and runs alike, and checks its configuration before the run.** The source budget
  is computed for every file from the prompt with that file's name, as the plan computes it; before, it
  was cached from the first file of a model group, so a later file with a shorter name could be
  trimmed although the plan said it fits, and `onOversize: fail` was not enforced at run time (it is
  now, as a failed file). A configured subtree that does not exist or lies outside `baseDirectory`
  (another drive included) fails the run instead of being skipped with a warning, which silently
  indexed the default tree; two sources summarized into one `.ai.md` and a prompt that leaves its
  model's window no room for the source fail before the run. Extensions match with their dot and in
  any case (`md` no longer matches `README.cmd`). The incremental check no longer compares the
  source's modification time or the plugin version, and a package's checksum no longer includes its
  children's times: a fresh clone, a touched file or a new srcmorph release regenerate nothing whose
  content is the same.
- **Retries and a context overflow for the `openai-compatible` provider (`maxRetries`).** A request
  that failed with `408`, `429`, a `5xx` status or a read timeout is sent again up to `maxRetries`
  times (default 3), after the server's `Retry-After` in seconds or an exponential backoff from 1 s with
  jitter; other failures are final at once. A prompt the server refuses as longer than the context
  (`413`, or `400` saying so) is an `AiContextOverflowException`, and the window is shrunk and the
  prompt asked again, as for a local model. One file or package whose request still fails no longer
  ends the run: it is counted, the others are indexed, and the run fails at its end. A named
  `apiKeyEnv` that is not set, or a key that would go over plain `http` to another machine, fails the
  run before any request; `apiKeyEnv` is trimmed, and `localhost` and the IPv6 loopback count as
  loopback in any written form.
- **An incomplete answer is never a summary (`onIncomplete`).** A model answer that is empty (also a
  reasoning-only one), cut off at the output budget, or a response without an answer (no choice, an
  HTML page or an `{"error": ...}` object with status 200) is refused by the provider
  (`AiIncompleteAnswerException`) instead of being written as the file's summary. Nothing is written
  for that file, package or project overview, so an older summary stays and the next run tries again;
  every other file is still indexed, and the run fails at its end naming the count. Per rule,
  `onIncomplete` is `fail` (default), `retryOnce` (ask once more) or `keep` (write a cut-off answer
  that has text, the behaviour before). Before, the facts line alone or the package's `Contents`
  list was written in place of the missing summary and counted as up to date, so an incremental run
  never tried it again. A leading `<think> ... </think>` block is stripped like Gemma's, and an
  unclosed one is a cut-off answer. `AggregatePackagesEngine` returns the packages written (no longer
  the unchanged ones too); `SourceFileIndexer.indexFile`, `PackageIndexer.aggregate` and
  `ProjectIndexer.aggregate` return an `AiIndexOutcome` / `AiIndexTally`.
- **Profiles: prompts and fact counters as files, named by key (`profileDirectory`).** A profile is
  one directory with a file per prompt (`<key>.md`, the system prompt verbatim) and per group of
  exact fact counters (`<key>.tsv`: `label<TAB>regex` per line, `#` comments). With
  `profileDirectory` set (in the configuration file: relative to the file's directory; in a
  `SrcMorphConfiguration` built in code: relative to the working directory; absolute always as it
  is), a rule's `promptKey` and `factsKey` that no definition has are read from there, so a
  configuration names keys only and repeats no prompt text and no regex. A key found nowhere fails the run before a model
  loads, naming the file it looked for; a pattern that does not compile fails it with file and line.
  A definition still works and wins over a file of the same key: a prompt is a `template` or a
  `templateFile`, a fact group `facts` or the new `factsFile` (both files relative to
  `profileDirectory`). Every file is read as UTF-8, a leading byte order mark dropped; an empty file is
  an error. In the library: `AiPromptTemplateLoader`, `AiFactDefinitionLoader`, `AiProfileFileSupport`.
- **Fact counters can leave regions out (`[ignore]`).** A counter whose label starts with `[ignore]`
  in a facts file (in a configuration: `ignore: true`) is not reported: its matches are blanked out
  of the source, every character except the line breaks, before the other counters count. A sub
  named in Perl's POD, a function or a `set -e` inside a shell heredoc, a commented-out POM dependency
  or an `INSERT` in a SQL block comment then no longer counts, while `^` anchors see the same lines.
  `AiFactExtractor.counts` returns the counts by label. A configuration's `facts` and a facts file pass
  one validation, `AiFactExtractor.validate(where, counters)`: every counter present, with a label and
  a compiling pattern, no label twice or starting with `[`, and not only ignore counters; the
  `[ignore]` prefix is read in any case, another `[...]` marker is refused.
  Several ignore lines blank in file order, so the profile's files have one per language, an
  alternation of all its regions where the one that starts first wins, as in a lexer (a `<!--` inside
  CDATA, POD inside a Perl heredoc or a `/*` inside a SQL string opens nothing), and their counters
  anchor with `^[ \t]*`, not `^\s*`, which is quadratic over the line breaks a long blanked region
  leaves (a 40 000-line SQL comment: 44 s before, 0.05 s now).
- **Fact files for shell, Perl and XML: `shell-facts`, `perl-facts`, `xml-facts`, `pom-facts`.** Bash,
  POSIX sh and ksh93 (functions, getopts loops and their option letters, dash-option case arms,
  named positional parameters, exported and readonly variables by name, defaulted variables,
  script-level assignments, sourced files, traps, `set -e`, exit statuses, ksh `typeset` and
  discipline functions), Perl (packages, subs, modules
  used and required, `use constant`, `our` and file-scope `my`, `%ENV` keys, `GetOptions`, `die` /
  `croak` / `confess`), XML (elements, namespaces, `id` / `name` attributes, XSD types, XSLT
  templates, Spring beans) and Maven POMs (dependencies, managed sections, plugins, modules,
  profiles, test scope), each with its comments, heredocs, POD or CDATA ignored. SQL `INSERT`
  counts without `INTO`, after an Oracle hint and as `INSERT ALL` / `FIRST`; MySQL's `DEFINER=`
  views and Oracle's `CREATE OR REPLACE TYPE` count, a `TYPE BODY`, `MATERIALIZED VIEW LOG` and
  `DATABASE LINK` do not. Shell comments after code are blanked, a function body may be any compound
  command, `source` counts after `&&` / `then` / `;`, an environment prefix (`LC_ALL=C sort`) is no
  assignment, and `declare` counts with `typeset` (label `typeset and declare declarations`). Perl
  counts 5.38 `class` / `method` and `use parent` / `base`, and `%ENV` reads without its
  assignments. XML blanks the DOCTYPE; POMs blank every plugin body, so a plugin's dependencies and
  configuration do not count, and `managed plugin sections` is new. Routing takes `.zsh`, `.bats`,
  `.t`, `.cgi`, `.psgi`, `.wsdl` and `.xhtml` too, and a POM above 275 KB keeps the Maven counters
  in the Granite tiers (`huge-pom`, `big-pom`).
  `ShellFactPatternsTest`, `PerlFactPatternsTest` and `XmlFactPatternsTest` pin every counter. The
  self-test profile and `examples/build-your-own-jar/srcmorph.yaml` route `.sh` / `.bash` / `.ksh`,
  `.pl` / `.pm`, `**/*pom.xml` and `.xml` / `.xsd` / `.xsl` / `.xslt` to them, and big XML files take
  the Granite size tiers Java and SQL have.
- **Prompts for shell, Perl and XML: `file-body-shell`, `file-body-perl`, `file-body-xml`.** Up to
  now these files took `file-body-fallback`. The shell prompt asks for every argument by its
  variable, every option as typed with its parser, every top-level variable as `name=value` (the
  lower-case and derived ones too), every function and the tables or hosts of its heredocs; the
  Perl prompt for subs, `Getopt::Long` options, `%ENV` keys, constants and imports; the XML prompt
  for what the file defines by kind (POM, Spring context, XSD, XSLT, other data). On the
  benchmark's language corpus gpt-oss-20b went from 95.7 to 97.7 % over six runs (99.1 % without
  one empty answer of the provider), GPT-6 Luna from 99.6 to 99.9 %; on the other two corpora
  gpt-oss lost a few names of unchanged Java and SQL prompts, which three runs cannot tell from
  variation (`profiles/README.md`, model card finding 7). The self-test's `shell`, `perl`, `pom`
  and `xml` rules and the example configuration use them. The Granite tiers for big XML,
  `big-xml` (H-Tiny) and `huge-xml` (H-1B), take `file-body-xml` too: on an 870 KB catalog H-Tiny
  named 7 of 8 names with the XML prompt and 4 with the generic one; on a 1.15 MB catalog, sampled
  as `huge-xml` samples it, H-1B wrote a short summary with the XML prompt and failed the file with
  the generic one, in each of three runs (`docs/ai-index-benchmark/remeasure-2026-11-02/`).
- **Benchmark: a language corpus, a large-file corpus and the fact counters in `run.py`.**
  `docs/ai-index-benchmark/corpus-languages/` holds nine files written for it (Perl, Bash, ksh93,
  POSIX sh, a POM, a Spring context, an XSD, an XSLT), with their expected names in
  `modelcard/facts-languages.json`; `modelcard/large.py` writes a reproducible SQL dump, XML catalog
  and Java enum far beyond a small window, with their facts. `run.py` takes `--facts` (every rule
  names its counters), `--oversize` / `--max-chunks`, a corpus without one of its kinds, and gives a
  language with a prompt of its own in the profile (`file-body-shell`, `-perl`, `-xml`) a rule;
  `models.json` has the two Granite presets of the self-test for the large corpus;
  `--chars-per-token` replaces every model's estimate, for data that tokenizes denser than code.
- **CLI: rules per aggregation phase, `packageFieldGenerations` and `projectFieldGenerations`.**
  Top-level keys beside `srcMorph`, read by the CLI's package phase (`AggregatePackages`, and the
  package step of `All`) and project phase and by the plugin's `aggregate-packages` /
  `aggregate-project`: the package phase takes exactly one rule, the project phase none or one.
  Before, both phases took the first file rule, so `All` summarized packages with a file prompt and
  model.
- **`examples/build-your-own-jar/srcmorph.yaml` is the plugin's self-test profile as a CLI
  configuration**, nearly 1:1: gpt-oss-20b (native MXFP4, 96K window) for every file, package and the
  project overview, Granite 4.0 H-Tiny (64K window, `mapReduce`) for ~275 KB .. 1 MB and Granite 4.0
  H-1B (16K window, `sampleChunks` with `maxChunks: 6`) beyond, with the exact whole-file counts of
  the profile's fact files (Java: types, methods, fields, ...; SQL: INSERT statements and rows,
  tables, views, indexes, routines, triggers, types; and the shell, Perl, XML and POM counters) on
  every summary of those kinds, so a sampled summary of a multi-MB SQL dump still states what the
  data holds. It replaces the one-model, one-placeholder-prompt example; a test compares it with
  `srcmorph-maven-plugin/srcmorph-selftest.yaml`.
- **The plugin's `srcmorph-selftest` profile carries three model definitions only**: gpt-oss-20b
  (`gpt-oss-20B-mxfp4`) and the two Granite presets. The other model definitions (Mistral, Llama,
  Codestral, Qwen, Gemma, Ministral, the `EXP-*` benchmark keys, the other gpt-oss quantizations and
  windows) are gone; their measurements stay in `docs/ai-index-benchmark`.
- **`profiles/`: the production prompts and fact counters once in the repository, now v3.**
  `profiles/v3/` holds the prompts (`file-body-java`, `-sql`, `-shell`, `-perl`, `-xml`, `-fallback`,
  `package-body`, `project-body`) and the fact counters (`java-facts`, `sql-facts`, `shell-facts`,
  `perl-facts`, `xml-facts`, `pom-facts`), which the self-test and the example configurations no
  longer write out; `SqlFactPatternsTest` and `JavaStructureFactPatternsTest` read these files
  instead of copies. The prompts are v1, which the October 2026 model card measured, with targeted additions (the type's
  own name; getters and setters named one by one; SQL constraint and index names and row counts per
  table; constants, configuration variables, helpers and library calls of other files). Three runs
  each on OpenRouter: GPT-6 Luna 100 % in all three (v1 93.6 to 99.0 %), gpt-oss-20b 99.0 % (v1
  96.8 %), search unchanged within its noise (model card, finding 5). v3 adds what gpt-oss-20b still
  lost on a second corpus: a table's row total over several `INSERT`s as an addition, extensions
  and types by name, every index as its own bullet, the entry point `main`, command-line commands
  and options by the name a user types, and a script's arguments by their variable; gpt-oss-20b
  100 % in three runs on the benchmark corpus and 99.8 % on the second (v2: 99.0 % on both), GPT-6
  Luna 100 % on both (finding 6). v1, v2, the June v2 experiment and v1 as the June benchmark ran
  it are in the history (`profiles/README.md`). The plugin's
  `srcmorph-selftest` profile, the model-card benchmark (`run.py --profile <name>`, default v3, replacing
  `modelcard/prompts.json`), the examples (which had one-line placeholder prompts) and the plugin's
  integration-test fixture all read these files by key from the profile directory, so the CI smoke
  of the user-built jar and the plugin IT read the profile as well (the IT also a `templateFile`).
- **`openai-compatible` provider: a model can run on any OpenAI-compatible API.** OpenRouter (the
  default `baseUrl`), a llama.cpp `llama-server`, jllama's `NativeServer` or any server answering
  `POST <baseUrl>/chat/completions`. Four new per-model settings: `provider` (overrides the run's
  `generationProvider` for this model, so one run can mix local and remote models), `remoteModel`,
  `baseUrl` and `apiKeyEnv` (the *name* of the environment variable holding the key, e.g.
  `OPENROUTER_API_KEY`; empty by default, so no key is sent unless a model names its variable and a
  custom endpoint never receives the OpenRouter key by accident; the key never goes into a
  configuration, and it is sent only over `https` or to a loopback `http` server, never in cleartext
  to another machine). The request carries the same
  prompts as the local provider and only the portable sampling knobs; the answer goes through the
  same parsing, so the `.ai.md` comes out the same way. Remote models skip the GGUF checks before a
  run and instead must name an `http(s)` `baseUrl`. Local stays the default: nothing leaves the
  machine unless a model says `openai-compatible`.
- **`openai-compatible` provider: `stream`.** A model with `stream: true` asks for the answer as
  Server-Sent Events (with `stream_options.include_usage`, so the token counts still arrive) and
  assembles it into the same chat response, so the summary does not change; a stream without
  `data: [DONE]` or with an `error` chunk fails the file instead of passing for a short answer.
  Default `false`. Needed for the GitHub Copilot API Gateway VS Code extension, whose
  non-streaming path adds every VS Code language-model tool to the request and runs the tool
  calls the model makes; its streaming path does not.
- **Guide: the models of a GitHub Copilot subscription through the Copilot API Gateway**
  ([docs/copilot-api-gateway.md](./docs/copilot-api-gateway.md)): setup, the settings that keep
  the gateway private, an example configuration (`examples/copilot-api-gateway/srcmorph.yaml`), model
  choice and the limits of the route. The model card
  ([docs/ai-index-benchmark/MODEL-CARD.md](./docs/ai-index-benchmark/MODEL-CARD.md)) adds four runs
  streamed through the gateway: Claude Haiku 5.5 (98.2 %, 6.8 s per file), GPT-6 Luna (97.6 %),
  MAI-Code-1.1-Flash (93.0 %) and Grok 4.7 (76.4 %, its reasoning summary leaks into the answer).
  Two more runs: Mistral Large 4 on OpenRouter (95.0 %, 32.1 s per file, 0.38 USD; its reasoning
  cannot be switched off) and Aleph Alpha's Kolibri-1 locally (92.2 %, 61.8 s per file, Q4_K_M
  with all 50 expert layers in RAM). Nine more on 2026-10-31: Muse Spark 1.3 in Meta's Contributor
  tier (100 %, the best search; Meta trains on its prompts), Nemotron 3.5 Super VL (97.9 % with
  reasoning off, 90.4 % with it), Mercury 3 (96.6 % at 2.6 s per file, the fastest), Claude Haiku 5.5
  on OpenRouter (93.9 %: OpenRouter enforces the 4096-token limit the Copilot gateway ignores), and
  locally gpt-oss-20b (95.8 %, its hosted quality, the best local run), Granite 4.2 8B (94.2 %),
  Gemma 4 26B-A4B (93.2 %) and LFM 2.5 24B-A2B (87.0 %, always reasons).
- **`openai-compatible` provider: `requestsPerMinute`.** At most this many requests to a model's
  server start within any minute; a request beyond it waits until the oldest of the last minute is
  a minute old, instead of being answered with `429`. Models of one server with the same key and
  limit share the window, across the phases of a run. Default `0`, no limit. The Copilot API Gateway example sets 25 against the gateway's `rateLimitPerMinute` 30;
  `ListModels` copies it into every template entry.
- **CLI command `ListModels`**: asks every OpenAI-compatible server the `aiDefinitions` name which
  models it serves (`GET <baseUrl>/models`, with the definition's key and the same refusal to send
  it in cleartext to another machine) and prints a table (id, token limit, OpenRouter's price) and a
  paste-ready `aiDefinitions` YAML block, one entry per model, copied from the server's first
  definition with `contextSize` lowered to a smaller model limit. `modelFilter` keeps the ids that
  contain it. Reads OpenRouter, the Copilot API Gateway, llama.cpp's `llama-server` and jllama's
  server. In the library: `ListModelsEngine`, `ModelListReport`,
  `OpenAiCompatibleAiGenerationProvider.listModels()`.
- **CI tests the provider against three real APIs on every run**, each in addition to the others:
  jllama's `NativeServer` (started in-process by `OpenAiCompatibleLiveTest`), a llama.cpp
  `llama-server` container (both on the committed SmolLM2 GGUF), and OpenRouter with
  `openai/gpt-6-luna` and the `OPENROUTER_API_KEY` secret. A check on the Surefire report fails the
  `test` job when any of them was skipped; only the OpenRouter case may skip, and only in a run
  without the secret (a pull request from a fork or from Dependabot).

### Removed
- **No more fat jars on the GitHub release.** Up to 1.3.0 every release attached 15 signed
  `srcmorph-cli-<version>-jar-with-dependencies[-<classifier>].jar` files (a CPU jar for every
  desktop platform plus one per llama GPU module); they were never on Maven Central. There are too
  many OS, CPU and GPU combinations to ship one per machine, so from 1.4.0 you build the runnable jar
  yourself (see Changed). Gone with them: the `package`-bound assembly in `srcmorph-cli`, its
  `fat-jar.xml` descriptor, the runtime license collector that only those jars needed (and with it
  the build's dependency on a bernardladenthin/actions checkout and `third-party-notices/`), and in
  CI the classifier build loop, `sign-fatjars.sh`, `verify-classifier-fatjars.sh` and its tests.
  `.github/release-assets.json` names no files beyond the Maven artifacts.

### Changed

- **The prompts lose what models copied or tripped over** (the prompt review of the same round). No
  prompt quotes an example lead any more ("Calculates VAT for invoices" came back as Granite's lead of
  an enum and as an invented class in a reduce), and each says not to copy wording from the
  instructions or to write about them; the closing paragraph says that the user message starts with
  the file name, not a path; headings are named plainly (`'#', '##', '###'`); Java and SQL no longer
  ask to recount before finalizing (a reasoning model spends its budget on it). `map-chunk` and
  `reduce-parts` drop their own `##` headings, say that they replace what the rule's prompt says about
  the user message, name the sample and half labels and the overlap, keep part summaries short, and
  merge format-neutrally (the project overview has no lead line or sections): the merged lead is
  about the whole input, a family stays a family, getters and setters stay apart, the facts line's
  counts are used without copying the line, a total added up from overlapping parts is "about N", an
  addition the rule's prompt asks for is written as its total. A reduce over sampled chunks reads a
  note saying so (`AiMapReduceSupport.sampleNote`), and states the sample's counts as the sample's.
- **A local model's windows are measured in tokens, not estimated.** `charsPerToken` is an integer
  estimate, and source code, SQL and data tokenize denser than it assumes: Granite's 64K and 16K
  windows overflowed on every chunk of the large corpus's dump at `charsPerToken: 3`, and each retry
  cut a quarter of its chunk away. `AiGenerationProvider.promptTokens` counts a prompt as the chat
  template renders it, with the model's own tokenizer (`llamacpp-jni`: `applyTemplate` and `encode`;
  a remote API cannot, so it keeps the estimate and the overflow retry). With a count, a source over
  the estimated window is counted whole: it goes in one call when it fits after all, and is otherwise
  chunked by its measured density; and every call is counted before it is sent, so a prompt over
  `contextSize - maxOutputTokens` is not sent but its window shrinks by the density
  (`indexer.AiTokenWindow`). The plan still estimates, since it loads no model.
- **Map and reduce calls say what their input is: `mapPromptKey` and `reducePromptKey`.** Every map
  and reduce call of `mapReduce` and `sampleChunks` took the rule's prompt, which describes a whole
  file: gpt-oss-20b indexed a chunk of INSERT rows of the large corpus's dump as a file and invented
  three tables, a procedure and a trigger, and Granite 4.0 H-Tiny answered the reduce of the catalog
  with the prompt's own example, a `VatCalculator` class, repeated under `Part 8:` .. `Part 18:`
  until its output budget ended. A rule's `mapPromptKey` and `reducePromptKey` name prompts appended
  to its prompt in those calls (`AiPromptSupport.composedKey`, rendered as the two templates and a
  blank line between); the profile's `map-chunk` and `reduce-parts` say to index only the chunk, and
  to merge the parts keeping every name a part states. Every map call's input starts with
  `Chunk i of n:` (`AiMapReduceSupport.chunkLabel`). The keys apply only to the two chunked
  strategies; a prompt key may not contain `+`. The self-test, the CLI example and `run.py` name them
  in every chunked rule and the package and project rules; the header's `- P:` covers them. Every
  reduce call also reads the exact facts line of the whole input in front of the parts, and
  `reduce-parts` says to state its counts as given: gpt-oss-20b spent the whole 4096-token budget of
  the dump's final reduce reasoning, adding up the row counts of nine parts, and answered nothing.
- **The self-test's ~275 KB .. 1 MB tier map-reduces with Granite 4.0 H-Tiny over a 64K window**
  instead of one pass over 384K: in one pass over the large corpus's 870 KB catalog H-Tiny copied the
  XML into a code fence with invented values (every price 972.00, every stock 0) until its output
  budget ended. The definition is `granite-4.0-h-tiny-mapreduce` (was `granite-4.0-h-tiny-bigwindow`),
  its rules `big-java`, `big-sql`, `big-xml` and `big-pom` (were `big-window-*`) with
  `onOversize: mapReduce`; `models.json` keeps the old preset for the earlier measurements.
- **The family rule of the prompts names no analysis channel.** `file-body-java` told the model to
  count a family of near-identical members "in the analysis channel", which only gpt-oss has; it now
  asks for the exact total and the index range only (a clause that also had the model name every
  member breaking the pattern was dropped again: GPT-6 Luna then reasoned at length over one Java
  file in three of six runs, against none of three before; Granite 4.0 H-1B had listed 6 000 enum
  constants one by one until its output budget ended, in five of eleven chunks). `file-body-sql`, `-xml`
  and `-fallback` get a family rule for the long runs of the large corpus's dump and catalog: rows,
  records and numbered constants. In `file-body-sql`
  more than five INSERTs into one table are a family too: the number of INSERTs and the total rows,
  not each count as an addition.
- **The package and project rules default to `onOversize: mapReduce`.** Their input is the summaries
  below them; with the general default `fail` a package over its window failed on every run and its
  parent lost the subtree, `sample` would drop the children behind its head. A rule that sets
  `onOversize` keeps it.
- **A file summary's header records the prompt and facts it was written with**: `- P:` (the CRC32 of
  the system prompt) and `- N:` (of the fact counters), written only when set, so package and project
  headers and older summaries stay as they are. The run record takes both from the header for an
  unchanged file, so after a prompt change it shows which summaries the old prompt wrote
  (`AiMdHeader.withHashes`, `p()`, `n()`; `AiFactExtractor.factsHash` replaces the engine's own).

- **Calibrate prints a YAML `calibration:` block** for the model's entry under `aiDefinitions` of the
  configuration file, not an XML `<calibration>` element no configuration reads any more
  (`CalibrationReport.renderPasteBlock()` replaces `renderXml()`).
- **Unset, `modelDirectory` and `profileDirectory` are the configuration file's directory**, so a
  relative `modelPath`, `templateFile` or `factsFile` resolves against the file's directory like every
  other relative path of the file, never against the working directory.
- **The SQL fact counters count what their labels say.** `INSERT rows` counted `INSERT INTO`
  statements, so a multi-row `INSERT` of 28 rows read as 1; it is `INSERT statements` now, and the
  new `VALUES rows (approx)` counts the row tuples of `VALUES` lists. `views` also counts
  `MATERIALIZED` and `OR ALTER` views, `tables` `TEMP`/`UNLOGGED` tables, `indexes` T-SQL's
  `CLUSTERED`/`NONCLUSTERED`, `functions/procedures` `CREATE PROC`, `OR ALTER` and MySQL's
  `DEFINER=`; `triggers`, `types` and `ALTER TABLE` are new. Then, after the forms a research pass
  found missing: `INSERT statements` counts MySQL's `INSERT IGNORE` / `LOW_PRIORITY` and
  `REPLACE INTO`, views, routines and triggers Oracle's `EDITIONABLE` / `FORCE`, `indexes`
  `FULLTEXT` / `SPATIAL` / `BITMAP`; `sequences`, `extensions`, `schemas and databases` and
  `packages and package bodies` are new, and `--` comments, string literals and `/* ... */` block
  comments are ignored in one pass (MySQL's `/*! */` and Oracle's `/*+ */` are not, they hold SQL;
  an unclosed `/*` comments out the rest of the file).
- **Dependencies and build plugins:** logback-classic 1.6.6 (test scope), NullAway 0.14.3,
  maven-surefire-plugin 3.6.1, jacoco-maven-plugin 0.8.16, palantir-java-format 2.103.0 (no formatting
  change) and `anthropics/claude-code-action` v1.0.264. The core depends on `jackson-databind` and
  `jackson-dataformat-yaml` (with it SnakeYAML) for `SrcMorphConfigurationFileLoader`; up to 1.3.0
  only the CLI did.
- **The GPG signing-key preflight runs only when something is published.** The two jobs
  `verify-signing-key` and `verify-signing-key-gradle` ran on every trigger: red on every pull request,
  because the `maven-central` environment admits only `main` and `v*` tags, and on a push to `main`
  they sat out the environment's wait timer for a run that publishes nothing. They run now on a
  `publish_to_central` dispatch only, still at the start of the run, and both publish jobs need them
  (no longer an exemption of the release gate).
- **`examples/windows-fatjar/` is now `examples/build-your-own-jar/`, for every platform.** Its
  `pom.xml` takes `srcmorph-cli` from Maven Central, picks the natives jar of the build machine's
  OS/arch with an OS-activated profile (`-Dnatives=<classifier>` overrides it, and the enforcer
  names the property when no profile matches), adds one GPU module with `-Dgpu=<classifier>`, and
  packs everything with the logging defaults into `target/srcmorph.jar`. `build.sh`/`build.bat`
  pass their arguments to Maven; `run.sh`/`run.bat` start the jar. The eight `examples/run_*`
  scripts now run that jar instead of `srcmorph-cli/target/*-jar-with-dependencies.jar`. The README
  mentions JBang as a shortcut (`srcmorph-cli`'s jar names its main class); Maven is the supported
  path.
- **CI: `smoke-fatjar` is now `smoke-jar` and tests the user's path.** It installs the reactor,
  builds `examples/build-your-own-jar` against it (`srcmorph.version` and `llama.version` read from
  the reactor), checks the Java 8 bytecode floor over that jar and the plugin jar, and runs `Plan`.
  The example pom is therefore under test on every pull request.

### Fixed

- **The plugin knows `${project.basedir}` and the other project values.** A `${name}` in the
  configuration file was a user, project or system property only, so `${project.basedir}` stayed as
  it was and became a directory of that name. `basedir`, `project.basedir`, `project.build.directory`,
  `project.build.sourceDirectory`, `project.groupId`, `project.artifactId`, `project.version` and
  `project.name` are known now, and a `${name}` that no property sets fails the load when it stands in
  a directory, `runRecordFile` or a subtree (CLI and plugin alike).
- **Nested subtrees no longer fail the run.** A subtree inside another one, or listed twice, collected
  its files twice, and the run failed with "X and X would both be summarized"; it is left out now, and
  every file is collected once. The plugin no longer takes a resource directory that holds the source
  directory (a POM that packs files of the project directory itself) as a subtree: it would index the
  whole project, build output included.
- **Javadoc and messages name the configuration file's keys, not the removed XML.** Comments and error
  messages no longer speak of `<fieldGenerations>`, `<aiDefinitions>` or the plugin configuration; they
  name the keys of the one configuration file (`fieldGenerations`, `packageFieldGenerations`,
  `projectFieldGenerations`, `factDefinitions`). `maxChunks` is documented as the chunk count of
  `sampleChunks` (required there, `0` otherwise; `mapReduce` reads every chunk), not as "0 =
  unbounded"; the `onOversize` and `onIncomplete` lists name `sampleChunks` and `keep`; `SrcMorphException`
  says what it is thrown for. Changed messages: "Invalid configuration:", "No fieldGenerations configured
  (srcMorph.fieldGenerations of the configuration file)", "add a rule with fallback: true".
- **Fact counters: a shift no longer opens a heredoc, and a long Java enum counts in linear time.** In
  shell, a `<<` inside `(( ))`, `$(( ))` or `$[ ]`, or whose line goes on with an unclosed quote, opens
  nothing; in Perl, a `<<` after a variable, a number, `)` or `]`, in a trailing comment or in a string
  opens nothing (`print $fh <<EOT` and `<< "EOT"` are heredocs). An unclosed heredoc runs to the end of
  the file, as bash reads it. 20 000 such lines took 25 to 207 s in shell and 95 to 223 s in Perl, now
  at most 0.2 s. `java-facts` reads a parameter list possessively, one level of parentheses deep: the
  6 000 constants of the large corpus's enum took 8.2 s in `constructors`, now 0.1 s for the whole file.
  `type declarations` anchors with `^[ \t]*` (40 000 blank lines took 65 s, now 0.02 s) and counts
  private and protected nested types and `@interface`s. No count the tests pin changed.
- **An ignored region within one line leaves a space.** The `[ignore]` counters blanked a region to
  its line breaks only, so `FROM t/* x */JOIN u` read `tJOIN` and counted no `JOIN`; a region within
  one line now leaves one space. No count the tests pin changed.
- **The deterministic summary shows the real last lines.** For a source ending with a line break,
  "Last N lines" showed N-1 lines and an empty one, and "Content" an empty last line.
- **A cut-off answer kept by `onIncomplete: keep` says so.** It ends with
  `_(Cut off at the output budget; kept by onIncomplete: keep.)_` instead of stopping mid-sentence
  without a sign.
- **What 1.3.0 wrote from an incomplete answer is written again.** 1.3.0 wrote the facts line alone
  for an empty or cut-off file answer, and a project index without its `#### Overview` for an empty
  overview answer, under the checksum of a complete one, so no later run touched them. A summary
  whose body is the facts line and nothing else now counts as missing, and a project index without
  its overview is written again when an overview rule is configured.
- **The plan sizes a map-reduce's chunks as the run does**: next to the longer of the rule's map and
  reduce prompts with a chunk's label reserve, not next to the rule's prompt alone, so its call count
  and time follow the run (`AiRouteEstimator.estimateRouteSeconds` with a chunk prompt length).
- **Benchmark: cut-off answers and unwritten files are counted, numbers with separators found.**
  `run.py` looked for a provider line srcmorph no longer writes, so every meta's `cutOff` was empty; it
  reads srcmorph's warnings and the run record's finish reasons now. A file phase that leaves some
  files unwritten (exit 1) no longer skips the package and project phases, which made one lost file
  cost a run 14 points of its format share. `score.py` counts `20,000` and `20 000` as 20000 and a
  number before a sentence's period, and scores the large corpus's middle names (`middleFound`, a name
  or the prefix of one: `LEDGER_LOCK` in `LEDGER_LOCK_02701`). A relative `--work` / `--out` / `--meta`
  resolves against the working directory, and a NaN `--chars-per-token` is refused.
- **The map-reduce drops, cuts and resends nothing behind the summary's back** (seven reviews of the
  branch). When no two part summaries fitted one reduce batch, all of them went into one call cut from
  the head, and the final answer read 2 of 22 parts under a coverage line that said 22 of 22; now each
  part is shortened at a line end so two fit, and the line says how many (`3 part summaries shortened
  to fit the reduce`). A chunk denser than its window was sized for is read in two halves instead of
  losing its tail, split at the line end nearest the middle that leaves each half a quarter of the
  chunk (not after a blank line at its very start). A window that leaves fewer than 1000 characters for a chunk next to the map and
  reduce prompts fails the file with that number, instead of cutting every chunk. The answer to a
  single chunk is the final one and has to be complete. A part cut at the output budget is kept at
  once, without the line the cut ended in and marked with `...` so the reduce sees it stops early, also under `retryOnce`, and counted under `keep` too; a call cut to a smaller window counts
  once. In a round that is not the last, a batch of one part passes it on unchanged. A facts line
  longer than a quarter of a batch is cut for the reduce at a counter. A trimmed prompt is never longer
  than its window (the EOF marker took room from it, and a line break at the cut kept one character
  more); the token pre-check aims 2 % below the limit, leaves room for the 4 beginning and end tokens
  a count does not see, has its own budget of four smaller windows and then refuses the prompt instead
  of sending it. The density is measured on a head of four estimated windows, not on the whole file.
  A chunk's overlap starts at a line start. Window arithmetic no longer overflows above 28 M
  characters. A blank `mapPromptKey` / `reducePromptKey` is unset, and the prompt hash tags the map and
  reduce prompts by role.
- **A request the server may already be billing is never sent twice.** A read timeout while the
  answer body was read still resent the POST, and the JDK itself resends a buffered POST once after a
  connection reset (`sun.net.http.retryPost`); a POST now goes in streaming mode with a fixed length,
  and a 401, whose body that mode cannot read, says that the key was refused or none was sent. A
  timeout that ends the retries is the file's failure, not an interrupt of the run (a
  `SocketTimeoutException` is an `InterruptedIOException`; the run ends only when the thread is
  interrupted). Gemini's per-minute 429 waits and retries (its message reads "exceeded your current
  quota" too; only the daily quota is final), and a `Retry-After` given as an HTTP date is honoured, in all three forms RFC 9110 has a
  recipient accept (IMF-fixdate, rfc850-date with its two-digit year, asctime-date). A
  source with a NUL character is not counted by the local tokenizer, which would stop at it.
- **A run leaves the configuration as it was given.** `check()` followed by `execute()`, or two
  `execute()` calls, failed on a rule with `factsKey` ("names its facts twice"), since the run resolved
  the facts into the caller's rules; it works on copies now. A map or reduce prompt without a template
  fails before a model loads, not in every file of the rule after it. An empty list item in the
  configuration file (`- ` in YAML) is refused naming the list instead of reaching the run as a null.
  `planOnly` stops the package and project phases too, so the plugin's aggregate goals load no model
  under it. `SourceFileIndexer.indexFile` without the hashes is gone (it wrote headers without `P` and
  `N`), and `RunRecord` keeps no value equality over its growing list of entries (CodeQL).
- **A part summary cut at the output budget no longer fails its file.** A small model that lists a
  long run of entries one by one (Granite 4.0 H-1B on a 6 000-constant enum: every constant as its
  own line until the 1536 tokens ran out) had its map answer refused as incomplete, and the whole file
  failed at the first such chunk. A part summary is an intermediate the reduce reads, so a cut one is
  kept now, with a WARN, and the coverage line counts it (`5 part summaries cut at the output budget`);
  the final answer still has to be complete.
- **A part that comes back without text is read again in two halves.** A reasoning model can spend
  the whole output budget thinking about one chunk and answer nothing: hosted gpt-oss-20b did on
  chunks of the large corpus's 1.6 MB SQL dump (4093 of 4096 tokens reasoning, on fp4 and bf16
  providers alike, and more often with a larger budget), and the whole file failed. A map answer
  that is empty or cut before any text now has its chunk read again as two halves at a line end
  near the middle, as a chunk too dense for its window is; a piece below twice the minimum chunk
  still fails the file. Measured: the dump is written in three of three runs, 93.0 % against 75.2 %
  on the large corpus (`docs/ai-index-benchmark/remeasure-2026-11-02/`).

- **A read timeout of a request is not sent again.** The `openai-compatible` provider resent a
  `POST` that got no answer within 600 s up to `maxRetries` times, each one possibly generated and
  billed; it now fails the file after the first (a connect timeout, before anything was sent, is still
  retried). A `501`, a `505` and a `429` whose body says the quota or credit is used up are no
  longer retried; a `Retry-After` of any number of seconds waits (capped at 300 s) instead of falling
  back to the short backoff; llama-server's "exceeds the available context size" and Gemini's "input
  token count ... exceeds the maximum number of tokens" count as a context overflow.
- **An edit of a file that is not UTF-8 is noticed.** The checksum was taken over the decoded text, so
  two Latin-1 files that differ only in an umlaut decoded alike and the edit counted as unchanged; it
  is taken over the bytes with only the line endings normalized (for valid UTF-8 the same value as
  before, so no summary is regenerated by the change).

- **The package and project rules are checked like file rules.** Their `onOversize`, `onIncomplete`
  and `maxChunks` were never validated (a `maxChunks` under `mapReduce` was dropped silently, a typo
  failed after the model had loaded), and their `factsKey` was never resolved although the overview
  honours its rule's facts; `check` and the run now refuse such a rule, and a `skip` rule, naming the
  phase, and resolve the facts. The rule is checked before the missing-output early return, so a
  wrong one fails on a fresh tree too.
- **A rule with both `facts` and `factsKey` is refused** instead of losing the inline facts.
- **An unknown provider name fails before the run**, in `check` as well, instead of at its model's
  group after the earlier groups have generated.
- **`All` with `planOnly: true` only plans**: the package and project phases no longer run.
- **A key set to nothing keeps its default.** `srcMorph:` with no value ended in a bare
  `NullPointerException`, and `pluginVersion:` put `null` into a non-null field; both mappers now
  skip null values (`Nulls.SKIP`).
- **The echoed configuration reads back.** `getOversizeStrategy()` was a bean getter, so the echo
  carried `oversizeStrategy`, which the strict reader refuses; it is `oversizeStrategy()` now, like
  `incompleteStrategy()`.

- **One failed file never ends the run, and the run record survives an abort.** The per-file catch
  took `IOException` only, while the llama.cpp binding throws unchecked exceptions, so one native
  error ended the whole run; an unchecked error now fails its file like a failed request (an
  interrupt still ends the run), and the log names the exception's class. The run record is written
  however the run ends; a failed write is added to the run's own failure instead of replacing it.

- **A line longer than the chunk budget no longer multiplies the chunks.** Behind such a line (a dump
  with one `INSERT` per line, minified code) the chunker cut at the previous line end again and moved
  on by one character, so a 50-line header before an 80 000-character line made 504 chunks, 499 of
  them the same overlap, each one a model call; it now cuts such a line hard (6 chunks).
- **A context overflow no longer cuts a source behind the rule's back.** A source that fits by the
  character estimate but not in tokens was cut by the overflow retry and written as a complete
  summary, also under `onOversize: fail`. Now the rule's `onOversize` decides, as for an oversized
  file: `fail` refuses it, `sample` says in its coverage line how much of the head the last call read,
  `mapReduce` / `sampleChunks` / `deterministic` take their path. A map-reduce whose calls had to be
  cut says so too: `**Coverage:** 9 of 9 chunks summarized (2 calls cut to fit the window)`
  (`AiMapReduceSupport.chunkCoverage(summarized, total, cutCalls, cutParts, shortened)`).
- **The benchmark's `score.py` took the facts line for the model's text.** With `--facts` a summary
  starts with srcmorph's exact counts, so the lead line was reported missing and the counts were
  scored as the model's. Every measure is now taken on the body without that line; the model
  card's runs had no facts lines, their scores do not change.
- **The self-test's ~275 KB .. 1 MB rules (then `big-window-java` / `big-window-sql`, now `big-java`
  / `big-sql`) wrote no facts line**, although the smaller and the larger files had one; they name `java-facts` / `sql-facts` now,
  in `srcmorph-maven-plugin/srcmorph-selftest.yaml` and in `examples/build-your-own-jar/srcmorph.yaml`.
- **A local model reasoned with thinking switched off.** The llama.cpp provider sent
  `reasoningEffort` (default `low`, meant for gpt-oss) to every chat template as the
  `reasoning_effort` kwarg, also with `chatTemplateEnableThinking: false`. A template that reads it
  first, as Aleph Alpha's Kolibri-1 does, then reasoned at `low` and spent the output budget on it
  (the model-card run's first file came back empty). Thinking off is now sent the way llama.cpp's
  server handles it, `enable_thinking` `false` and no `reasoning_effort`, reached through either
  knob: with `chatTemplateEnableThinking: false` the effort is not sent, and `reasoningEffort: none`
  sends `enable_thinking` `false` instead of the effort (before, only the template saw `none`, and
  llama.cpp itself took thinking for on). gpt-oss, which has no thinking switch, keeps its effort.
- **A summary that quotes a thinking-block marker lost most of its text.** `AiCompletionParser`
  dropped everything up to the *last* `<channel|>` anywhere in a response, so a model describing a
  Gemma-4 parser, a prompt or anything else that names the marker had its answer cut down to the
  tail (seen on `AiCompletionParser.java` itself in a run against OpenRouter: lead, purpose and type
  were gone). A thinking block is now recognized only at the start of the response, where the model
  emits it, and closes at the first `<channel|>`; a marker later in the text stays part of the
  answer, and a quoted `<|channel>thought` no longer reports an exhausted token budget.

## [1.3.0] - 2026-10-26

### Added
- **`examples/windows-fatjar/`: build your own fat jar, then run it.** Four files:
  - `pom.xml` names srcmorph-cli and the llama classifiers of the machine, with `llama-platform`
    excluded;
  - `build.bat` is one `mvn package` on a JDK 8;
  - `srcmorph.yaml` is a commented configuration that starts with `Plan`;
  - `run.bat` runs it.

  `docs/windows-intel-core-ultra.md` walks through them for an Intel Core Ultra (CPU and Vulkan
  classifiers, checking what loaded, threads). Tested on Windows: built on JDK 8 (32 MiB instead of
  the release fat jar's 111 MiB), `Plan` on JDK 8, and a real index with the Vulkan module through
  `run.bat`. `ExamplesConfigBindingTest` binds its configuration and checks the pom's main class.

### Changed
- **The default thread count is now 4, or fewer on a machine with fewer cores.** It was 8 for a model
  definition (`threads`) and 2 for the fallback parameters (`llamaThreads`, `srcmorph.llama.threads`).
  Both now use `AiGenerationConfig.DEFAULT_THREADS = min(4, cores)`. Eight threads on a four-core
  machine is not a little slower but orders of magnitude slower, because ggml's threads wait on each
  other at every step: on four visible cores the same real-model test class took 644.9 s with 8
  threads and 7.8 s with 4. A configuration that sets `threads` explicitly is unchanged. On a machine
  with more cores, raise it after measuring (`Calibrate`).
- **`bernardladenthin/actions` pinned at v1.1.0.** v1.1.0 adds `java/audit-publication`, an audit of a
  published release from the outside (not used here yet); nothing changes for the actions in use.
  v1.0.3 before it made the snapshot publication survive
  GitHub: the Central snapshot transfer lists the jobs of older runs 50 per page and repeats GitHub
  reads on a server error, and a refused move of the `snapshot` tag deletes and re-creates it (both
  failed in java-llama.cpp's first snapshot dispatch on v1.0.1). v1.0.2 before it carried a
  documentation change only:
  the license collector's `python3 -c` guard must use single-quoted Python string literals, because
  `exec-maven-plugin` drops a double quote out of an argument on Windows. It records the trap this
  repository's own POM just stepped out of (see the Fixed entry); the shared CI behaviour is unchanged.
- **The shared CI code moved to [bernardladenthin/actions](https://github.com/bernardladenthin/actions).**
  The build-check library, the publication gate and its consumers, the signing-key checks and the
  shared scripts and workflows are actions and reusable workflows there, pinned by commit; the
  `shared-files` job and `.github/shared-files.sha256` are replaced by the `repository-checks` job,
  configured in `.github/actions-config.json`: the files this repository must carry are compared with
  the templates there, and the publication wiring and a dry run of the release asset contract are
  checked in every pull request. Packaging srcmorph-cli runs the license collector from there: `ACTIONS_DIR` in CI, locally a checkout at `../actions`.
- **The CLI fat jar carries a license text for every runtime jar, and the build checks it.** Runtime
  jars without legal files of their own (JSpecify, SnakeYAML) get their upstream texts from
  `third-party-notices/`; a runtime jar with neither fails the build. The dependencies' legal files go
  into the fat jar only, no longer also into the published `srcmorph-cli` jar.
- **Releases and snapshots are published through one local gate.** The publish jobs stage and sign
  everything, `publication.py prepare` checks the exact file set, every signature and the version
  against the tag (`v<version>`, no `-SNAPSHOT`; `check-tag` / `check-snapshot` check it first), and
  freezes the result; the GitHub release, the Maven Central transfer and the provenance attestation
  are independent jobs on that frozen package, so a Central outage no longer hides the GitHub files and
  nothing is attached before the gate has passed. Builds run on JDK 25 (`.java-version`); the
  published bytecode target is unchanged. Every published jar carries `LICENSE` and `THIRD_PARTY.md`
  under `META-INF/third-party/`.

### Added
- **`modelDirectory`: relative model paths resolve against one directory, absolute ones stay as they
  are.** A new configuration field (plugin parameter `modelDirectory`, property
  `srcmorph.modelDirectory`; `"modelDirectory"` in a CLI configuration; a relative value is relative to
  the module's `${basedir}` in Maven and to the working directory in the CLI) is the directory every
  relative `modelPath` of the `aiDefinitions` and the `llamaModelPath` fallback is resolved against. An
  absolute `modelPath` (on Windows also a root- or drive-relative one) is used unchanged, so one model
  can live elsewhere while the others are named by file name only. Unset, a relative path stays relative
  to the working directory, as before. The `srcmorph-selftest` profile uses it:
  `<modelDirectory>${ai.modelDir}</modelDirectory>` once, and its 36 `<modelPath>` entries are bare file
  names; before, each entry was the text `${ai.modelDir}/<file>`, which put every model in that one
  directory unless the entry was edited.

### Fixed
- **PIT ran for hours on CI: the real-model tests used eight threads on a four-vCPU runner.** The first
  `main` run to pass its tests since llama 6.0.0 (40316163607) sat in the PIT step for over two hours
  until it was cancelled. Two tests built their configuration with `threads(8)`. ggml synchronises
  its threads on spinning barriers, so more threads than cores is not a little slower but orders of
  magnitude slower: one generation took 431 s on the runner, PIT's coverage pass 904 s against 41 s
  locally, and PIT re-runs these tests for every mutant they cover. All real-model tests now use
  `NativeLlamaAvailability.THREADS`, the new default (see Changed). The PIT step has a 60-minute timeout, so a regression cannot burn the six-hour job limit again.
- **The one PIT mutant without a test:** `AiModelDefinitionSupport.hasRoot` returns `true` for a path
  the platform cannot parse, and nothing tested that. A model path with a NUL character now does; it
  stays unchanged instead of being joined below `modelDirectory`. That brings `srcmorph` back from
  842/843 to the 100 % threshold.
- **Stale prompt-template documentation.** The plugin README described a template as taking two `%s`
  placeholders, and its examples, like the README Quickstart, ended in `File: %s` / `Source: %s`.
  Since the core extraction (`b141d20`) a template is the system prompt, sent verbatim, and the file
  name and source go to the model as a separate user message. A `%s` therefore reached the model
  literally. The text now says so, and the examples carry no placeholders.
- **The plugin README's GPU section described the llama natives before 6.0.0.** It said a GPU natives
  jar holds `jllama.dll`, that `lib.path` points at "the GPU `jllama.dll`", and that a GPU library
  whose runtime is missing fails over to the CPU one. Since 6.0.0 a GPU jar holds one ggml backend
  module that is put next to the CPU library, and `lib.path` names a natives directory. The section
  now describes that, adds `cublasLt64_13.dll` to the CUDA runtime list, and links the Windows guide.
- **Two CI defects that the unresolvable llama 6.0.0 had hidden.** Until 6.0.0 reached Central, every
  run stopped at the first Maven step. The first run that resolved it (srcmorph#243) went further and
  found two more:
  - The `Test` job runs `verify`, which packages `srcmorph-cli` and runs its license collector from
    bernardladenthin/actions. Unlike `build`, the job had no `common/setup`, so `ACTIONS_DIR` was
    unset and the collector stopped the build. The job now runs `common/setup` too.
  - The plugin integration test compares the descriptor's `srcmorph.*` properties both ways against
    a deliberate list. `srcmorph.modelDirectory`, added with the model-directory resolution, was
    never added to that list. It is listed now, and all six steps pass locally (20 properties).
- **`srcmorph-cli` could not be built on Windows at all.** The `collect-runtime-licenses` execution passes a
  one-line Python program to `python3 -c`, and `exec-maven-plugin` drops the double quotes out of that
  argument on Windows: `find_spec("actionlib")` reached Python as `find_spec(actionlib)`, so every
  `mvn package`/`install` died with `SyntaxError: '(' was never closed` before the module was packaged. The
  string literals are single-quoted now, which is valid Python and survives the argument handling on both
  platforms. Measured with this plugin version: `print("x")` arrives as `print(x)` and fails, `print('x')`
  arrives intact. Escaping as `\"` would fix Windows and break Linux, where the argument reaches `execve`
  verbatim, so it is not an option; a comment at the execution says so. The Linux CI never showed this.
- **The `calibrate` goal's paste block mixed line endings on Windows.** `CalibrationReport` wrote its three
  value lines with `String.format("...%n")`, the platform line separator, between lines ending in `\n`, so on
  Windows the `<calibration>` block meant for a `pom.xml` carried 3x CRLF and 3x LF. All lines end in `\n`
  now. `CalibrationReportTest` renders the block in a child JVM started with `-Dline.separator=\r\n`, so the
  Linux CI sees what only a Windows build showed before (red without the fix).
- **...and that fix left `spotbugs:check` red.** SpotBugs reports `VA_FORMAT_STRING_USES_NEWLINE` for every
  `String.format` pattern containing `\n` and asks for `%n` — the platform separator this block must not
  carry, so the rule and the requirement are in direct conflict. Nothing could have shown it: the reactor
  still died earlier on the then unresolvable `net.ladenthin:llama:6.0.0`, so no CI run reached
  SpotBugs, and `main` was red on a check that never executed. `appendPasteBlock` appends the newline as a
  literal through a new `appendXmlNumber` helper now — the shape `appendJsonNumber`/`appendYamlNumber`
  already use for the same reason — and formats the numbers at the call site through the `FORMAT_*` constants
  the JSON and YAML renderers share. No pattern carries a newline, the block keeps its LF endings, and no
  suppression was added.

### Changed
- **The `srcmorph-selftest` profile names its model directory once, as `ai.modelDir`.** All 36
  `<modelPath>` entries of `srcmorph-maven-plugin/pom.xml` carried the absolute `X:/Modelle/...`, a drive
  that no longer exists on the owner's machine, so the profile could not run anywhere as committed — and a
  machine-specific absolute path sat in a published POM. They read `${ai.modelDir}/<file>.gguf` now, the
  property defaults to the owner's model drive, and another machine runs the profile with
  `-Dai.modelDir=/path/to/models`. `docs/ai-index-benchmark/tools/calibrate-bench-pom.xml` keeps its single
  overridable `bench.model` and only follows the drive.
- **`LlamaCppJniAiGenerationProvider` no longer explains `gpuLayers = -1` in terms of a "CPU build" and a
  "GPU build".** That was jllama's pre-6.0.0 artifact layout, where the classifier chose the backend. Since
  6.0.0 the library jar is CPU-only and a GPU backend joins it as an additive module jar, so the comment's
  premise was gone while its conclusion stayed right: `-1` leaves llama.cpp's own default, which offloads
  everything once a GPU device is registered. Measured on an RTX 3070 Laptop (Qwen3-4B-Instruct-2507-Q4_K_M,
  identical config, only the classpath differing): 63 t/s prefill and 7.1 t/s decode without the module jar,
  3474 / 86.8 with it, and `gpuLayers` -1 vs 99 within noise either way.
- Removed four unused constants and one string concatenation from `LlamaCppJniAiGenerationProviderTest`
  (six Error Prone `UnusedVariable` / `StringConcatToTextBlock` warnings). The wire-key assertions they
  belonged to moved to the model-free `LlamaCppJniProviderSupportTest` with the core-module extraction,
  where the same four constants are declared and used; these were fossils, so no coverage is lost.
- Build tooling bumped in step with the sibling repositories: `anthropics/claude-code-action` v1.0.258 and
  `github/codeql-action` v4.39.1. maven-clean-plugin 3.5.0, maven-resources-plugin 3.5.0,
  maven-install-plugin 3.2.0, maven-deploy-plugin 3.2.0 and maven-dependency-plugin 3.11.0 are now declared,
  in a new pluginManagement of the parent POM: no POM named them, so their versions came from whichever
  Maven ran the build. Maven 3.9 runs clean 3.2.0, resources 3.3.1, install and deploy 3.1.x and dependency
  3.7.0; the Ubuntu runners' Maven 3.10.0 already matches, except that it leaves the dependency plugin to
  the newest release. Resource output is byte-identical under resources 3.3.1 and 3.5.0.
- Dependencies and build tooling bumped in step with the sibling repositories: Jackson 2.22.3 ->
  2.22.4 (the CLI's JSON/YAML configuration binding) and Gradle 9.8.2 in the shared
  `verify-signing-key-gradle` job.
- **Build tooling: spotbugs-maven-plugin 4.10.4.1 -> 4.10.5.0** in all three modules, in step with the sibling
  repositories, and `checker-qual` 4.3.1 managed in the parent (it had stayed at 4.3.0 next to the 4.3.1
  Checker Framework processor). With the SpotBugs bump, **Lombok-generated code carries JSpecify nullness** (`lombok.addNullAnnotations = jspecify` in the
  shared `lombok.config`): under `@NullMarked` a generated `equals(Object)` parameter read as non-null
  while `Object.equals` accepts null, which SpotBugs 4.10.5 -- the first release that reads JSpecify --
  reports as `NP_METHOD_PARAMETER_TIGHTENS_ANNOTATION` per class; Lombok now writes `@Nullable` there
  (and `@NonNull` where a generated member cannot return or take null), so a consumer's nullness checker
  sees the real contract too.
- Build and test tooling bumped in step with the sibling repositories: Spotless 3.10.4 and Checker
  Framework 4.3.1 in all three modules. The shared `check-versions.py` now also compares the Kotlin
  Gradle plugins of a repository with its POMs' Kotlin (nothing to compare here) and reads
  java-llama.cpp's `examples/server/pom.xml`.
- **CI prints the runner's CPU** at the top of `test` and `vmlens` (`.github/print-host-info.sh`, a
  shared file of the four repositories), so the knob sweep's and PIT's timings can be read against the
  hardware the job drew.
- **CI: snapshot deploy fails with HTTP 401 on Maven 3.10 (seen in java-llama.cpp).** The runners moved to Maven 3.10, which
  sends a `<server>`'s credentials only to the origins declared for it; for the id `central` that is
  `https://repo.maven.apache.org`, so the upload to `central.sonatype.com/repository/maven-snapshots/`
  goes out without credentials ("Not using credentials of server 'central'"). Every `deploy` step now
  passes `-Dmaven.repository.credentialScope=id`, Maven's own switch back to id-only matching, until a
  setup-java release can write `<repositoryOrigins>` (`mvn-server-repository-origins`, merged upstream
  but not yet released).
- **CI: shared files and the release gate are checked.** The files kept byte-identical with the sibling
  repositories are listed with their SHA-256 in `.github/shared-files.sha256`; a new `shared-files` job
  fails on a copy changed here alone and warns on a sibling's differing copy. The same job runs the
  shared build-check library's tests and `check-release-gate.py`: every job must gate both publish
  jobs unless `.github/release-gate-exemptions.txt` says why (`vmlens` now gates). The crash-log step
  and the signing-key preflight are shared scripts (`print-crash-logs.sh`, `verify-signing-key.sh`)
  instead of copies pasted into the workflow.
- **Workflow jobs kept identical across the repositories are checked too**: a
  `.github/shared-files.sha256` entry `.github/workflows/publish.yml#<job>` hashes one job (`startgate`,
  `shared-files`, `verify-signing-key`, `check-snapshot`, `check-tag`, and where present
  `verify-signing-key-gradle`, `github-snapshot`, `github-release`).
- **Own scripts are licensed `MIT OR Apache-2.0`** like the CI files: `examples/run_*` and
  `docs/ai-index-benchmark/tools/` (the Java sources and the rest of the project stay Apache-2.0; the
  header `generate-fixture.sh` writes into its generated Java fixtures is unchanged).
- **More shared files, and files identical up to the repository name**: a shared-files entry ending
  in `?repo` is hashed with the repository's name replaced by `{repo}`. Added: `.editorconfig`,
  `.gitattributes` (now with `*.gguf binary` everywhere), `FUNDING.yml`, `CODEOWNERS`, the license texts,
  `SUPPORT.md`, `ISSUE_TEMPLATE/config.yml` and further files listed in `.github/shared-files.sha256`;
  the signing self-test now runs on Gradle 9.8.0 in all four repositories.
  `.mvn/settings.xml` lost the unused `github-java-llama` server.
- **The JDK is named once, in `.java-version`**: every workflow reads it through setup-java's
  `java-version-file` (the `JAVA_VERSION` env and the literal `21`s are gone); `.java-version` and
  `codeql.yml` are now byte-identical in all four sibling repositories and in the shared-files manifest.
- **CI files are licensed `MIT OR Apache-2.0`**: every `.github` file carrying only the owner's
  copyright now has the same license header in all four sibling repositories, so the shared ones are
  byte-identical. `claude.yml`, `claude-code-review.yml`, `scorecard.yml`, `reuse.yml`,
  `osv-scanner.yml`, `dependabot.yml` and `CODE_OF_CONDUCT.md` joined the shared-files manifest;
  `osv-scanner.yml` now grants `contents: read` instead of `read-all` (as java-llama.cpp already did).
- **Workflow run scripts are parsed in the `shared-files` job**: `check-run-scripts.py` runs `bash -n`
  over every `run:` script of the workflows and composite actions that runs in bash (shell decided as
  the runner does), so a broken script fails within minutes instead of in the job that runs it.
- **Maven versions are compared with the sibling repositories**: `check-versions.py` (in the
  `shared-files` job) warns where a dependency or plugin -- incl. annotation-processor paths and the
  Spotless formatter version -- is used in another version than in a sibling's default branch.
- **`net.ladenthin:llama` 6.0.0's natives jars.** The binding is now the Java classes only, and every
  native build is its own jar of the same artifact (classifier `<backend>-<os>-<arch>`). `srcmorph`
  depends on `net.ladenthin:llama-platform` (a pom naming the CPU natives of every desktop platform),
  so the plugin and the CLI keep running everywhere with no change for users. A GPU natives jar is now
  **added** next to the CPU natives rather than swapped in: `-P gpu-cuda` / `-P gpu-vulkan` and the new
  `-Dllama.classifier=<classifier>` (profile `gpu-natives`) add one, and the loader falls back to the
  CPU natives when the GPU runtime is missing. The GPU classifier fat jars of the CLI are therefore the
  CPU fat jar plus one backend — they now also run on a machine without that GPU.
  `.github/verify-classifier-fatjars.sh` asserts exactly that (the promised `<OS>/<ARCH>/<backend>/`
  directory, and no CPU native of the default jar lost), and the CI fat-jar set follows llama's
  `natives.csv` (the two `msvc-windows-*` jars added).
- **The CLI fat jar now ships `slf4j-simple` instead of logback, and no `checker-qual` at all.**
  Production code here targets Java 8, but every logback release from 1.4.0 on is Java 11 bytecode:
  SLF4J's `ServiceLoader` finds `LogbackServiceProvider` at startup, so a Java 8 JVM died with
  `UnsupportedClassVersionError` before a single line was logged. The Java 8 logback line (1.3.x) is
  end-of-life and its CVEs are fixed only in 1.5.x/1.6.x with no backport, so downgrading was not an
  option either. `slf4j-simple` is six classes from the same release train as `slf4j-api`, with no
  configuration or socket layer for a CVE to live in.

  `checker-qual` moved to `provided` scope. Its annotations are major 55 from 4.0.0 on and
  `@Retention(RUNTIME)`, so anything reflecting over an annotated element — Jackson, which binds the
  CLI's whole configuration — loads them. `provided` keeps 4.2.2 on the compile classpath, where the
  Checker Framework processor needs it, while shipping none of it: `jar-with-dependencies` filters on
  scope, which is also why `<optional>true</optional>` alone would not have been enough.

  Consequences for users: `examples/logbackConfiguration.xml` is replaced by
  `examples/simplelogger.properties`; the fat jar carries its own defaults (INFO, stdout, timestamps)
  which any `-Dorg.slf4j.simpleLogger.*` system property or a classpath `simplelogger.properties`
  overrides. The published `srcmorph` and `srcmorph-cli` **library** jars are unchanged in this
  respect — they impose no binding and carry no logging configuration; a binding is an application
  concern and the fat jar is the application. `net.ladenthin:llama`'s own binding is now excluded
  transitively, so embedding `srcmorph` no longer forces one on the caller and the Maven plugin no
  longer ends up with two providers alongside Maven's `maven-slf4j-provider`.

### Added
- **CI gates every built jar on the Java 8 class-file floor.** `release 8` governs only the code we
  compile; a dependency built for a newer Java lands in the jar untouched and surfaces as
  `UnsupportedClassVersionError` on a consumer's JVM. The new `.github/verify-bytecode-version.sh`
  (kept byte-identical across the four sibling repos) runs in the `smoke-fatjar` job with
  `--max-major 52` and fails on any class above it, skipping only `module-info.class` and
  `META-INF/versions/**`, which a classpath JVM never loads.

### Added
- **`srcmorph:calibrate` now writes a machine-readable report.** The goal built a `CalibrationReport`
  and printed it as `INFO` lines, and that was the only output — so the numbers a calibration run
  produces (prefill / decode throughput, chars per token per model) could not be diffed across runs,
  committed as a baseline, or fed back into `aiDefinitions` by anything other than a human re-reading
  the console, which is most of the point of measuring them.

  `execute()` now also writes `srcmorph-calibration.json` and `srcmorph-calibration.yaml` into the
  configured `outputDirectory`, creating it if needed. Both carry the same keys in the same order and
  add `loadSeconds`, `midPrefillTokensPerSecond` and `cachedPromptTokens`, which the pasteable
  `<calibration>` block does not. The three figures the XML *does* carry are formatted identically, so
  the JSON and the paste block can never disagree about what was measured.

  The renderers are hand-rolled in `CalibrationReport` rather than delegated to Jackson: the core
  module is framework-free and carries no JSON dependency (only `srcmorph-cli` does), and
  `renderXml()` already set that precedent. Model keys come from user configuration, so they are
  escaped for both formats — an unescaped quote would produce a file neither parser accepts.

### Added
- **`flashAttn` works.** The knob was documented and settable since it was introduced, but could not
  be forwarded: `--flash-attn` takes a mandatory `on|off|auto` and `net.ladenthin:llama` offered only
  a bare-flag setter, which emitted the key alone and made llama.cpp's parser consume the following
  argv token — the load then died naming a flag the user never set. 1.2.0 shipped a refusal at plan
  time rather than that diagnostic.

  `net.ladenthin:llama` **6.0.0** adds `ModelParameters.setFlashAttn(FlashAttn)`, so the provider now
  emits `--flash-attn on` when the knob is set. `false` still emits nothing, which leaves llama.cpp's
  own `auto` default in force — that is the correct behaviour for an unconfigured run, and it is what
  the knob's javadoc now says.

  **The knob is exercised against a real model for the first time.** `flashAttn` moves out of
  `LlamaCppJniKnobSweepTest`'s `NOT_SWEPT` list into a real sweep case; until now only the refusal had
  ever been executed.

### Removed
- The plan-time and provider-side refusals (`EngineSupport.validateFlashAttnIsNotRequested`,
  `LlamaCppJniAiGenerationProvider.FLASH_ATTN_UNSUPPORTED_MESSAGE`) and the three tests that pinned
  them.

### Changed
- **`lazyMode` replaces `tensorReadLazy` — breaking.** llama.cpp b10731 renamed `--tensor-read-lazy`
  to `-lzm` / `--lazy-mode` with no alias, and `net.ladenthin:llama` followed the rename rather than
  papering over it, so the model-definition field, its `srcmorph.llama.*` surface, the
  `LlamaCppJniConfig` accessor and the sweep case move with it. Carrying the old name would leave the
  configuration describing a flag that no longer exists.

  Worth knowing why this is a real hazard and not cosmetics: a **new Java name paired with an old
  native** produces `Failed to parse model parameters` at load time and nothing earlier catches it.
  The knob sweep did — it is what surfaced the mismatch here.

- **`net.ladenthin:llama` 5.1.0 → 6.0.0** (the pin said 5.2.0 until 2026-10-15; jllama never published
  that number and ships the breaking natives split as 6.0.0). Deliberately the *release* version, not `6.0.0-SNAPSHOT`:
  this repo declares no `<repositories>` element (the `distributionManagement/snapshotRepository` is
  deploy-only), so the Central snapshot channel is not resolvable here at all — and a `-SNAPSHOT`
  dependency on `main` would block every srcmorph release, since Central rejects one. The build
  therefore stays red until `net.ladenthin:llama:6.0.0` is published and goes green with no further
  edit the moment it is — recorded here so nobody mistakes it for a regression.

- **BREAKING: `chatTemplateEnableThinking` is a tri-state; the `enable_thinking` kwarg is only sent
  when it was actually set.** It used to be a plain `boolean` defaulting to `true`, so every run put
  `enable_thinking` into the chat-template kwargs -- including runs whose chat template has never
  heard of the kwarg, which llama.cpp's Jinja layer has been moving from "silently ignored" toward
  "warned about". The only way to stop that noise was to set the knob to `false`, which means
  something else entirely.

  The obvious phrasing -- "send it only when it differs from the template's default" -- is not
  implementable: srcmorph would have to parse and evaluate the template to know that default, which
  is exactly the work it delegates to the binding. So the rule is "send it only when the user
  actually set it".

  `AiGenerationConfig`, `AiModelDefinition` and `LlamaCppJniConfig` (and its builder) now carry
  `@Nullable Boolean` instead of `boolean`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING` is `null`
  (unset) instead of `true`, and the two `isChatTemplateEnableThinking()` getters were renamed to
  `getChatTemplateEnableThinking()` to match the wrapper type. `true` and `false` are both still
  forwarded verbatim -- unset is the only value that omits the kwarg. A configuration that never
  mentioned the knob keeps working and simply stops sending it; one that set it explicitly is
  unaffected.

  Guarded by four model-free tests over the extracted `buildChatTemplateKwargs()` -- including the
  `true` case, so a future "send it only when false" shortcut fails rather than silently swallowing
  a configured value.

### Fixed
- **CI: a fat jar without any native library is named in the release check again.**
  `.github/verify-classifier-fatjars.sh` runs under `set -euo pipefail`, and its `natives_of` helper
  piped `unzip -Z1` through `grep`: for a jar with no native entry `grep` exits 1, the assignment of
  the helper's output failed and the script ended right there -- red, but with no `::error::` line and
  no jar name, so its two "carries no native library at all" checks (default jar and classifier jar)
  could never run. The helper now tolerates only grep's "no match"; an `unzip` failure or a grep error
  still fails it. The script had no test at all: the new srcmorph-local
  `.github/buildcheck/tests/test_verify_classifier_fatjars.py` builds fixture jars and asserts exit code
  and `::error::` message for every check the script makes itself (a matching classifier jar passes;
  CPU-only under a GPU name, a lost default native, an unmapped classifier shape, a jar without natives
  -- classifier and default -- and a single-OS default jar fail), and for a corrupt jar unzip's own exit
  code 9 without the script's diagnosis (a helper whose failure were masked, `|| true` on its call or a
  lost pipefail, would report the file as a jar without natives and is caught; a `|| true` on the grep
  branch alone is not observable through a corrupt jar, pipefail carries unzip's status either way).
  The two no-natives cases were red before the fix.
- **CI: the GitHub `snapshot` pre-release holds exactly the current build, and the release assets
  include the pom.** `gh release upload --clobber` replaces same-named assets and never removes any
  other, so the `snapshot` release accumulated: all 16 of its assets were stale -- the 0.1.0-SNAPSHOT and
  1.0.1-SNAPSHOT jars of the old `llamacpp-ai-index-maven-plugin` coordinates and four SpotBugs plugin jars
  (`fb-contrib-7.6.4.jar`, `fb-contrib-7.7.4.jar`, `findsecbugs-plugin-1.13.0.jar`,
  `findsecbugs-plugin-1.14.0.jar`) an old collect step uploaded from `target/spotbugs/` in 2026-05/06. `github-snapshot` now deletes every asset
  the run did not upload, after the upload and each printed by name; it refuses an empty upload,
  treats an asset that is already gone as removed and fails only after trying every deletion (asset
  by asset on purpose: the job's first form deleted and recreated the release, and failed in every
  burst of main pushes in May 2026); versioned releases are never pruned, and the next snapshot run
  clears the backlog by itself. The collect steps copy
  `<artifactId>-<version>.pom` + `.pom.asc` as well (Central had them, the release did not), and
  the signature check counts every attachable type (`*.jar`, `*.aar`, `*.pom`) instead of jars
  alone. Shared job text with the two sibling repositories (`shared-files.sha256`), the convention
  in `workspace/policies/fat-jar-release-assets.md`.

---

## [1.2.0] - 2026-09-01

### Added
- **The plan phase now checks the configuration against the model itself, still without loading it.**
  `net.ladenthin:llama` ships `GgufInspector`, which parses only a GGUF's header key/value table — no
  native library, no tensor data — so it is usable from a phase whose whole promise is that it loads no
  model. srcmorph was not using it; its check was `new File(modelPath).isFile()`, which misses two
  things, both silently:
  - **The file is not a GGUF at all.** A Git LFS pointer, a truncated download, or simply the wrong
    file passes an existence check and dies much later inside the native loader — in a multi-model run,
    after the earlier model groups have already generated. Now a plan-time failure naming the file.
  - **`contextSize` exceeds what the model declares.** This is the one that costs the most and shows
    the least: *every* number the plan produces is derived from `contextSize` — `maxInputChars`, the
    oversize/chunking decision, the time estimate. Point the default 32768 at a 4096-context model and
    the plan is wrong by 8&times; before a token is generated. A warning rather than an error, because
    llama.cpp will deliberately run past a model's trained context with RoPE scaling.

  New `provider.GgufModelInspector` (the `jniConfinedToProvider` rule keeps binding types out of
  `engine`) and the framework-free `provider.GgufModelInfo` it returns, the latter on the PIT gate.

- **A truncated summary is now reported instead of being written as if it were complete.**
  `maxOutputTokens` defaults to 128; a model that hits that ceiling stops mid-sentence, and llama.cpp
  says so — the OpenAI finish reason is `length` rather than `stop`. The provider was calling
  `chatCompleteText(...)`, which returns only the text, so the signal was not merely ignored but
  unavailable. The generate path now parses the whole response, which costs **no extra inference**:
  `chatCompleteText` is literally `extractChoiceContent(chatComplete(...))`, the same native call.

  Worth recording for whoever touches this next: the check compares against the literal `"length"`,
  deliberately **not** against `StopReason`. Those are two different vocabularies — `getFinishReason()`
  is OpenAI's (`stop`/`length`/`tool_calls`), while `StopReason` maps llama.cpp's own `stop_type`
  (`eos`/`word`/`limit`). `StopReason.fromStopType("length")` returns `NONE`: a silent wrong answer,
  not a compile error.

- **Prompt-cache reuse is now visible during an indexing run**, not only during `calibrate`, closing
  the follow-up that release left open. Parsing the full response also makes `Usage` available, so the
  `DEBUG` line reports cached / total prompt tokens and tokens generated, per file — the run that
  actually pays `swaFull`'s KV-memory surcharge had no visibility into whether it was paying off.

- **`srcmorph-cli` and `srcmorph-maven-plugin` are PIT-gated**, at the same `mutationThreshold` 100
  as the core. They were the last two modules without one, and the gap was not theoretical: PIT
  measured the plugin at **53%** (33/62) and the CLI at **84%** (16/19) before any test was written.

  The plugin's hole was the interesting one. `AbstractAiIndexMojoTest` covers the *shared*
  `buildConfiguration()`, but each goal adds its own mapping step on top — and every one of
  `buildGenerateConfiguration` / `buildAggregatePackagesConfiguration` /
  `buildAggregateProjectConfiguration`, plus all eight `getLlamaContextSize`/`getLlamaThreads`
  accessors, had **no coverage at all**. Drop `setExcludes(...)` from `GenerateMojo` and the plugin
  quietly indexes files the user excluded, with nothing failing. `MojoConfigurationMappingTest` now
  pins each goal's own parameters with values distinct within their type, so a transposition fails
  rather than cancelling out — verified by swapping `minFileSizeBytes`/`maxFileSizeBytes`, which
  reds the test. The three mapping methods went from `private` to package-private for it.

  Two survivors that the new gate exposed were weaknesses in *existing* tests, not missing ones:
  `AbstractAiIndexMojoTest` asserted `generationProvider` was `"mock"`, which is
  `SrcMorphConfiguration`'s own default — so dropping the setter call was invisible; it now uses a
  non-default provider name. And `CalibrateMojo.execute()`'s deliberate blank separator line could
  not be seen by a `contains` check, so it is asserted by position.

  The CLI gate excludes exactly one method, `Main.main(String[])`, with the reason recorded in the
  pom: the `smoke-fatjar` job — a release gate for both publish jobs — already runs the real
  `java -jar` artifact and asserts `Main#run end.`, which only `run()` emits and only `main()`
  calls. Its one true survivor, the `System.out.println` that makes the `<calibration>` block
  paste-ready rather than a log line, is now pinned by a stdout-capturing test.

  CI runs the goal reactor-wide instead of `-pl srcmorph -am`, and the survivor extraction and
  report upload cover every module's `target/pit-reports`.

- **Four new model-definition knobs, wired through to `net.ladenthin:llama` 5.1.0's new
  `ModelParameters` setters.** They are configured exactly like the existing `gpuLayers` /
  `mainGpu` / `devices` knobs — as elements of an `<aiDefinition>` in the Maven plugin, or as
  keys under `srcMorph.aiDefinitions[]` in a CLI JSON/YAML config — and each is only forwarded
  to the binding when explicitly set, so an unconfigured build behaves exactly as before:
  - `cpuMoeLayers` (`--n-cpu-moe` / `-ncmoe`, default `-1`) — keep the MoE expert weights of the
    first *n* layers on the CPU. Usually the better trade than lowering `gpuLayers` on a MoE
    model: it moves only the expert weights (the class that dominates such a model's size), so a
    substantially larger model fits the same VRAM at a smaller speed cost. `0` is a valid,
    meaningful value, so the forwarding guard is `>= 0`, not `> 0`.
  - `cpuFfnLayers` (`--n-cpu-ffn` / `-ncffn`, default `-1`) — the dense-model counterpart; same
    `>= 0` guard for the same reason.
  - `kvUnifiedPerSlot` (`--kv-unified-per-slot`, default `-1`) — per-slot unified KV context cap.
    The binding rejects `0` and negatives, so only a positive value is forwarded.
  - `tensorReadLazy` (`--tensor-read-lazy`, default empty) — `off` / `auto` / `on`. Shortens model
    load time, which matters here because a run loads one model per model group and `calibrate`
    preflights every model in turn. Carried as a `String` through the `config` package (the
    `jniConfinedToProvider` ArchUnit rule keeps `net.ladenthin.llama` types out of it) and
    resolved to the binding's `TensorReadLazyMode` inside the provider, matched
    case-insensitively against the CLI strings the enum itself declares. An unrecognised value is
    **rejected**, not silently dropped — a typo there would otherwise hand the user a run that
    quietly did not do what was asked.

- **`seed` model-definition knob** (`InferenceParameters.withSeed`, default `-1`). Upstream draws a
  random seed per request, so with a non-zero temperature every generation samples differently: a
  re-index of an unchanged file, or any `force=true` run, produced a different `.ai.md` body than the
  one already committed. That sat awkwardly next to the project's own "deterministic indexing"
  principle. Setting a seed makes the body stable for a given machine and configuration, which turns a
  re-index into a reviewable diff. Forwarded only when `>= 0`, so an unconfigured run is byte-identical
  to before. Documented deliberately as *not* bit-reproducibility: llama.cpp results move with thread
  count, batch size and backend, so the seed pins the sampling, not the arithmetic.

- **The prompt cache is now measured instead of assumed** — `cache_n` reaches Java. The binding has
  always reported how many leading prompt tokens llama.cpp served straight from the KV cache; the
  provider read the `Timings` object and dropped that number. It is the *only* observable the whole
  prefix-reuse stack produces (`cachePrompt`, `cacheReuse`, `swaFull`), so with it discarded a run paid
  `swaFull`'s KV-memory surcharge on the assumption that it was working, with no way to tell whether it
  was. `AiGenerationTimings` now carries it as `cachedPromptTokens()` next to the evaluated
  `promptTokens()`, plus their sum as `totalPromptTokens()`; `AiCalibrationMeasurement` carries the
  near-window run's count, and `srcmorph:calibrate` logs it per model — an `INFO` line naming the reused
  token count, or a `WARN` when nothing was reused, which is the case where those settings cost memory
  and buy nothing. The real-model provider test asserts that a second request against the same system
  prompt finds it cached, so the reuse is proven end to end and not just plumbed.

- **Seven further model-definition knobs**, wired through to `net.ladenthin:llama` 5.1.0 exactly like the
  existing `gpuLayers` / `cpuMoeLayers` set — an `<aiDefinition>` element in the Maven plugin, or a key
  under `srcMorph.aiDefinitions[]` in a CLI JSON/YAML config — and each forwarded to the binding only when
  explicitly set, so an unconfigured build behaves exactly as before. They are added now rather than later
  because configuration surface is the part of this library that cannot be extended without a release.
  - `repeatLastN` (`--repeat-last-n`, default `-1`) — **the gap this closes is a pair that was half
    wired**: `repeatPenalty` was configurable, the window it acts on was not, so the strength was
    adjustable while the reach stayed on llama.cpp's default of 64 tokens. `0` disables the penalty and is
    a meaningful value, so the guard is `>= 0`; the binding rejects negatives. Per-request, next to
    `repeatPenalty` itself.
  - `cacheTypeK` / `cacheTypeV` (`--cache-type-k` / `--cache-type-v`, default empty) — KV-cache
    quantization, the most direct trade of quality for context length: at `q8_0` the cache costs about
    half of `f16`, so a larger `contextSize` (or `swaFull`) fits the same VRAM. Carried as a `String`
    through the `config` package — the `jniConfinedToProvider` ArchUnit rule keeps `net.ladenthin.llama`
    types out of it — and resolved to the binding's `CacheType` inside the provider, matched
    case-insensitively against the CLI strings the enum itself declares. An unrecognised value is
    **rejected**, and the message names which of the two knobs was wrong, since one resolver serves both.
  - `flashAttn` (`--flash-attn`, default `false`) — lower KV memory and faster attention where the
    backend supports it, and the precondition for quantizing the V cache.
  - `batchSize` / `ubatchSize` (`--batch-size` / `--ubatch-size`, default `-1`) — prefill sizing, and
    prefill is what dominates an indexing run: every file is one large prompt with a short answer. The
    binding's own default for both is `0`, which llama.cpp reads as "decide for me", so `0` is not a
    meaningful user value and the guard is `> 0`.
  - `threadsBatch` (`--threads-batch`, default `-1`) — prompt-processing threads, separate from the
    decode `threads`. The two optima differ on machines with efficiency cores; `-1` keeps llama.cpp's
    behaviour of reusing `threads`.

  `srcmorph:calibrate` is what makes these measurable rather than guesswork: it reports prefill and
  decode throughput, and — since this release — the prompt-cache reuse the extra KV room buys.

  Note the cost this exposed: `LlamaCppJniConfig`'s positional constructor reached 37 arguments. It was
  covered field-by-field by `LlamaCppJniConfigFactoryTest`, which gives every field a distinct value so a
  mis-ordered pair fails — but the shape was past its limit, and this same release replaces it with a
  builder (see *Changed* below). The constructor is private now; nothing outside the class can call it.

- **Fail-fast check on a routed model's `<modelPath>`.** A typo previously survived the whole plan
  phase — walk, classify, rendered plan — and only died inside the native loader, which with several
  model groups means *after* the earlier groups had already generated. The check joins the fail-fast
  block the engines already run.

  Two placement constraints, both load-bearing. It is **gated on
  `generationProvider == llamacpp-jni`**, because only that provider loads a GGUF and every shipped
  example deliberately points at a non-existent `unused-with-mock-provider.gguf` while running the
  mock; an ungated check would red the examples, their binding tests, the CLI end-to-end test and the
  fat-jar release smoke. And it runs **after the `planOnly` early-out**, because `planOnly` is
  documented to "stop before loading any model" and `Plan` is the CLI's default command — the
  workflow it serves is configuring routing on a machine where the GGUFs are not present and running
  for real on the one that has them. Stat'ing a file is not loading it, but failing the plan on a
  missing model would break that workflow all the same.

  The ordering was wrong in the first cut and no test saw it: every other `GenerateEngineTest`
  fixture uses the `mock` provider, so the check never ran through the engine at all. Both halves are
  now pinned — a plan-only run with the real provider and a missing model still plans; the same
  configuration without `planOnly` still fails fast.

- **Every model knob is now exercised against the real model, one knob per test case.**
  `LlamaCppJniKnobSweepTest` sets each of the 34 sweepable `LlamaCppJniConfig` knobs to a non-default
  value in turn and runs a real generation with the committed 135M model. The failure class it exists
  for is precisely the one that produced this release's two provider fixes: a knob is not exercised by
  being *set*, it is exercised by the binding *accepting* the value derived from it, and those two came
  apart twice — `dryPenaltyLastN` at its own `-1` default (rejected outright, so every generation threw
  before a token) and `flashAttn` (mapped onto a setter that cannot express what llama.cpp now
  demands). Neither is visible to a mapping unit test, and neither is visible to the rest of the suite,
  which runs on the `mock` provider and never loads the native library.

  Per-knob rather than all-at-once on purpose: an all-knobs config catches the same breakage but names
  no culprit, and one rejected value masks every knob behind it. 36 cases in ~35&nbsp;s. Three knobs
  are excluded with stated reasons (`modelPath`, `devices`, `flashAttn`), and a reflective completeness
  check fails the class when a knob is neither swept nor excluded — in **both** directions, plus a
  count assertion so an empty sweep list cannot satisfy it. A knob added later therefore reds this test
  until somebody decides how it is covered. Verified by falsification: a deliberately invalid value
  reds exactly the case carrying it and names the knob.

- **The Maven plugin is now run as a plugin in CI** (`plugin-it` job, `.github/plugin-it.sh`,
  fixture in `.github/plugin-it/`). Until now every check on `srcmorph-maven-plugin` called Java
  methods directly -- `PluginArchitectureTest`, `MojoPhaseSkipTest`, `MojoConfigurationMappingTest`,
  PIT at 62/62 -- so nothing covered the parts only Maven performs: plexus binding of the
  `<configuration>` XML onto the `@Parameter` fields (the CLI's Jackson binding is a different code
  path), goal-prefix resolution, the `srcmorph.*` property strings, lifecycle-phase binding, and the
  descriptor `maven-plugin-plugin` generates. **A renamed `@Parameter` property would have shipped
  green.**

  Six checks: the full lifecycle with all three goals configured entirely from the fixture's pom XML
  (the nested `<condition><extensions>` routing tree, both `<subtrees>` entries, `<excludes>`, the
  per-execution `<configuration>` override, and both readonly `${project.*}` injections); the
  generated descriptor read out of the packaged jar (goal prefix, the four goal names, and a
  **two-way diff of all 19 `srcmorph.*` property strings**, so a rename fails and a new property
  fails until it is listed deliberately); `mvn srcmorph:generate -Dsrcmorph.planOnly=true` via the
  goal prefix; `-Dsrcmorph.aiVersion=9.9.9` reaching the written document header; all four skip
  properties including one **off-diagonal** case (`srcmorph.file.skip` must not skip the packages
  goal -- the diagonal alone cannot catch a copy-pasted property); and the `calibrate` goal, the one
  goal the lifecycle run does not reach. The fixture uses the default `mock` provider, so no GGUF,
  no GPU and no network.

  The fixture's `<configuration>` deliberately mirrors the worked example in
  `srcmorph-maven-plugin/README.md`, so it also fails when the documented XML stops being the XML
  that works.

  Every assertion was falsified rather than assumed, and that pass rewrote three of them. A first
  version passed a `settings.xml` whose `<pluginGroups>` entry was believed necessary for
  `mvn srcmorph:generate` to resolve; it is not (Maven matches the prefix against the descriptor of
  the plugin the fixture declares), and adding it opened a second resolution path through repository
  metadata that **masked a changed goal prefix entirely** -- on a pristine runner too, since Central
  serves that metadata. Removing the file turned that check into a real second guard. Separately,
  `<subtrees>`, `<excludes>` and all three execution-level `<configuration>` blocks could each be
  deleted with the test staying green -- the first because the fixture's only value equalled the
  engine's own fallback, the second because the pattern matched no file that existed, the third
  because the overrides changed nothing observable. All three are now pinned by a fixture that makes
  them observable.

- **The classifier fat jars are verified before they are attached** (`.github/verify-classifier-fatjars.sh`).
  The publish jobs build seventeen fat jars — one per `net.ladenthin:llama` native classifier plus the
  default — and exactly one of them was ever checked: `smoke-fatjar` launches the default. The sixteen
  GPU jars still cannot be *launched* meaningfully (a GitHub-hosted runner has no such device, and the
  only command that loads the native library is a real generation), so the script asserts instead that
  each is the artifact its name claims: one jar per requested classifier and no unexpected one, a
  native library present, the native for the OS/arch the name promises, a default jar spanning more
  than one OS, and — the load-bearing check — a native set that **differs** from the default jar's. If
  `-Dllama.classifier=` ever stops being wired through, Maven resolves the default artifact and the
  loop ships sixteen copies of the CPU build under GPU names; nothing else in the pipeline would
  notice. A classifier shape nobody has mapped fails the script rather than passing unchecked.

- **A release can no longer attach an unsigned asset quietly.** The attach jobs collect
  `target/*.jar.asc` with `|| true`, so a signing step that produced nothing yielded an attach that
  looked complete and was not. The check deliberately does **not** gate the upload: both attach jobs
  run even when the publish job failed, because when Central is unreachable the GitHub assets are the
  only way to get the build output at all. So it reports before the upload, uploads unconditionally,
  and fails the job afterwards — the assets always land, an unsigned release is loudly red instead of
  quietly wrong. The same two steps are now byte-identical in all four sibling repositories.

### Changed
- **Two further public constructors gained a parameter, and a mojo parameter was removed.** Neither was
  listed as breaking when the changes landed; both are, so they are recorded here rather than left for a
  consumer to discover at compile time.

  `AiGenerationTimings` and `AiCalibrationMeasurement` each went from five parameters to six, both
  gaining `cachedPromptTokens` — the `cache_n` value this release surfaces. Anyone constructing either
  directly must add the argument; both are value types a downstream extension could plausibly build.

  `srcmorph.llama.libraryPath` is gone from the plugin (see *Removed*), and Maven does not ignore an
  unknown `<configuration>` element: a consumer POM still declaring it fails the goal outright rather
  than warning. That is the intended outcome — a parameter that never worked should not keep looking
  like it does — but it is a hard break on first invocation, not a silent one. CLI users hit the same
  wall at parse time, because both JSON and YAML are read with `FAIL_ON_UNKNOWN_PROPERTIES` enabled.

- **`LlamaCppJniConfig` is built through a builder; its positional constructor is private.** Breaking for
  anyone who constructed one directly — deliberately taken in this release rather than later, because the
  class is already breaking here and a second break for the same type is worse than one.

  It had grown to a **37-argument** constructor, which is past the point where a call site can be read or
  a swapped pair of same-typed neighbours is noticeable: the compiler accepts
  `…, batchSize, ubatchSize, …` in either order. `LlamaCppJniConfig.builder(modelPath)` replaces it, and
  `modelPath` — the one value with no meaningful default — is required at the entry point rather than
  checked in `build()`, so a half-formed builder cannot be constructed at all.

  The builder is also where the defaults now live, and that removes a real duplication:
  `LlamaCppJniConfigFactory.fromFallbackParameters` used to restate all 30+
  `AiGenerationConfig.DEFAULT_*` constants by hand, so a knob added to `AiGenerationConfig` could end up
  with a different default there by simple omission. It is now five lines. Each builder field starts at
  the matching `AiGenerationConfig.DEFAULT_*`, and a `null` on a String or list setter restores that
  knob's default instead of storing `null` — note this is "the default", not "empty":
  `reasoningEffort(null)` gives back `"low"`.

  Two tests carry this, and both were verified to fail against a deliberately broken build rather than
  assumed to: `builder_leavesEveryUnsetValueAtItsAiGenerationConfigDefault` (every unset value equals its
  config default — reds when one builder field is initialised to `0` instead) and
  `builder_everySetterWritesItsOwnField` (36 distinct values — reds when `ubatchSize(…)` is made to write
  `batchSize`). The old positional-constructor tests they supersede are removed.

- **`net.ladenthin:llama` 5.0.6 → 5.1.0** (llama.cpp b10456 → b10682). Unlike the earlier bumps in
  this series, 5.1.0 is **not** purely additive: it deprecates six `InferenceParameters` methods that
  were always silent no-ops, and this provider called one of them — `withUseChatTemplate(true)`.
  Because `srcmorph` compiles with `-Xlint:all -Werror`, that deprecation is a **compile error**, so
  the bump would have broken the build. The call was **removed rather than suppressed**: it never had
  an effect (upstream reads no such key from a request body), and llama.cpp defaults `use_jinja` to
  true, so chat templating and tool calling are unchanged. The rest of the surface this provider uses
  — `LlamaModel`, `InferenceParameters`, `ModelParameters`, `ChatResponse`/`Timings`/`Pair`,
  `ChatResponseParser`, `ReasoningFormat` — is untouched by 5.1.0. Verified by a full reactor
  `clean test`: 639 / 39 / 32 tests, 0 failures, 0 skipped, all four modules SUCCESS.

- CI actions bumped to latest: `actions/setup-java` v5 → v6.

- **Build tooling bumped and NullAway aligned with the sibling repos**: `nullaway` 0.13.8 → 0.14.0,
  `spotless-maven-plugin` 3.10.0 → 3.10.1, `palantir-java-format` 2.96.0 → 2.97.0, `pitest-maven`
  1.25.9 → 1.30.0. All three of the first are declared **per module** here (`srcmorph`,
  `srcmorph-cli`, `srcmorph-maven-plugin`), not in the reactor parent, and `pitest-maven` has two
  separate version declarations — every one was moved. nullaway is an alignment rather than a plain
  bump: streambuffer had already merged a Dependabot bump to 0.14.0, so the four repos had silently
  stopped being identical. Deliberately **not** taken: `jqwik` 1.9.3 → 1.10.1, forbidden by
  [`workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md).

- `AiGenerationConfig.getStopStrings()` no longer declares a `@Nullable` return. The field is
  initialised to an empty list and the setter normalises `null`, so the documented "or `null` if not
  set" case was unreachable; the getter now matches its sibling `getDrySequenceBreakers()`. The two
  null-guards this made dead in `LlamaCppJniConfigFactory` were dropped with it — the real guard lives
  in `LlamaCppJniConfig`'s constructor, and is now pinned directly by a new `LlamaCppJniConfigTest`.

- **PIT gate widened from 632 to 775 mutations in the core module, all killed at `mutationThreshold` 100.** Newly gated:
  `provider.LlamaCppJniConfig`, `config.AiConditionGroup` (both already at 100% with no new test —
  `TODO.md` had listed them as needing "careful fixtures", which was stale), `document.AiMdDocumentCodec`
  and `indexer.AiIndexPlan` (survivors killed here). `document.AiMdHeaderCodec` is documented as
  permanently out: its last two survivors are equivalent mutants in the colon-position guard, unkillable
  through the public API. Coverage was also added for the CLI's `.js`/`.yml` extension aliases and all
  six `CCommand` dispatch arms, the plugin's `buildConfiguration()`/`messageOf()`, and `GenerateEngine`'s
  missing-subtree and unknown-`factsKey` paths. The count reached 775 by release: later work in this
  cycle added `provider.GgufModelInfo` to the list and grew the gated classes to 52. Measured on
  `main` at release time: **775 / 16 / 62**, one hundred percent in each of the three modules.

- **Line-based routing is now covered end to end.** `<lines>` is documented in the plugin README and
  the condition layer was tested directly, but no test ever made the indexer's `anyRuleUsesLines`
  return true, so `countLines` never ran during planning. A silent failure there would send every
  file to the fallback rule with nothing failing. Verified by inverting the `usesLines` check: the
  new test goes red. Writing it also surfaced a semantic worth knowing — a condition node evaluates
  exactly one leaf and returns on the first present, with `extensions` before `lines`, so combining
  the two needs an `<and>` group; the first version of the fixture set both on one node and matched
  on extension alone.

- **`AiSourceChunker`'s observable boundaries are pinned** (28/34 mutants, up from 25): `maxChars == 1`,
  the guard that stops the last chunk being trimmed and re-split, and the guard that stops a chunk
  beginning on a newline collapsing to a single character — the last two are silent-content-change
  shapes. The six remaining mutants are equivalent (capacity hint, empty-list return, two arithmetic
  terms an enclosing `Math.max` absorbs, a loop head an earlier `break` already guarantees, and a
  subset branch that at equality reproduces its input); they are enumerated in `TODO.md` so nobody
  re-hunts them.

### Fixed
- **The `llamacpp-jni` provider produced nothing at all, and had done so since 1.1.0.** Every real
  generation threw `IllegalArgumentException: Invalid dry_penalty_last_n value: -1` before a single
  token, because `buildInferenceParameters` forwarded the DRY penalty window unconditionally while
  the window's default is the `-1` sentinel and the binding rejects any negative value — llama.cpp
  b10273 dropped the old "`-1` = context size" meaning, and `net.ladenthin:llama` has enforced that
  since 5.0.5. The neighbouring `repeatLastN` was already guarded with `>= 0`, and its comment even
  spells out the rule; DRY's window was written earlier and never caught up. Both are guarded now,
  so `-1` means "send nothing, llama.cpp keeps its own window", which is what the javadoc always
  claimed. That javadoc was wrong too, on five sites, and is corrected.

  What let it survive two releases is worth recording. The belief behind the unguarded call is in
  the code: *"multiplier 0.0 (default) = off, so the base/allowed-length/penalty-last-n knobs have
  no effect unless opted in."* True of the window's **effect** — and irrelevant, because the wither
  **validates** whatever it is handed, DRY enabled or not. Everything that runs in CI uses the
  `mock` provider (the test suite, `Plan`, the fat-jar smoke), so no gate ever touched the failing
  path, and every one of them stayed green throughout.

- **The three real-model tests now run** instead of being disabled by a flag nobody set.
  `-DrunNativeLlamaTests=true` dates from the initial commit, when the repository carried no model
  and a developer had to supply one — a correct opt-in at the time. The model was committed six
  minutes later, and the flag was never revisited: it appears in **no** workflow and **no** POM in
  the repository's entire history, so those tests had never executed anywhere while the ~90 MB GGUF
  they need was checked out on every job. Turning them on found the defect above in ten seconds.
  The gate is now a capability check (`NativeLlamaAvailability`): the tests run by default and skip
  only where the model or a loadable native library is genuinely absent, each skip naming its cause.

- **`warnOnTruncatedAnswer` and `logPromptCacheReuse`, both added in this release, had no executed
  coverage at all** — they sit on the same provider path. Five model-free tests now drive them from
  a hand-built `ChatResponse`, pinning the `"length"`-versus-`"stop"` decision (the vocabulary trap
  the code documents), the empty-`choices` guard, and the `DEBUG` line's three counts. Four further
  tests pin the penalty-window guards without a model, so the regression is caught on every platform
  rather than only where a GGUF is present; removing either guard reds them.

- **`publish.yml` concurrency group: every non-PR run now gets its own group.** The block previously
  claimed a push to `main` or a `v*` tag *"always runs to completion"* because `cancel-in-progress`
  is scoped to `pull_request`. GitHub cancels a **pending** run whenever a newer run joins the same
  group behind an in-progress one, and that rule is **independent of** `cancel-in-progress` — so a
  queued release run on `main` could be dropped silently by a later push. The group expression now
  appends the unique `github.run_id` for non-PR runs; PR runs still share a group per ref and
  supersede each other as intended.

- Bumped `jackson.version` 2.22.0 → 2.22.2 (`jackson-databind` / `jackson-dataformat-yaml`,
  pinned in the parent `pom.xml`) to close
  [GHSA-5jmj-h7xm-6q6v](https://github.com/advisories/GHSA-5jmj-h7xm-6q6v) (CVSS 5.3, Medium),
  flagged by OSV-Scanner against `srcmorph/pom.xml` and `srcmorph-cli/pom.xml` after the `main`
  merge of the relocation-stub removal.

- **`AiMdHeaderSupport.shouldWrite`'s change-detection chain was not covered by a single test.** The
  seven-way header comparison that decides whether a `.ai.md` is regenerated could be replaced
  wholesale with `return false;` and the class's tests stayed green — verified, not inferred. Two of
  them wrote the fixture document with a header but no body, so the method returned at its blank-body
  guard before the comparison ever ran. Since checksum-driven regeneration is a stated design
  principle, dropping any disjunct would have meant stale files silently never regenerating, with
  nothing failing. Both tests now write a body, and a parameterized case covers each compared field
  (`h`/`x`/`title`/`c`/`d`/`g`/`a`) plus the unchanged-header negative. Re-running the same mutation
  now fails 9 of 15 tests, and removing a single disjunct fails 2.

- **`srcmorph:calibrate`'s chars-per-token silently depended on a cache hit nothing checked.** It divided
  the near-window run's *source* characters by the prompt tokens the model had *evaluated* — a figure that
  excludes whatever the KV cache served. That is only the source's own token count while the base prompt
  is a cache hit; with no reuse the same divisor also covers the base prompt, so the ratio came out too
  low and the plan's time estimate with it. Nothing enforced the assumption and, until `cache_n` was
  surfaced, nothing could even observe whether it held. The ratio is now read from the mid&rarr;near
  *size differential* over `totalPromptTokens()` (evaluated **plus** cache-served): both runs share the
  same base prompt, so subtracting them cancels it and the result describes the source text alone,
  identically whether or not the prefix was reused. A regression test measures the same prompt twice —
  once fully reused, once not — and requires both to agree; it fails on the previous formula.

- **No `execute()` verified that it honours the skip flag.** `shouldSkip()` itself was well tested, but
  a mojo that never called it would have passed every one of those tests — and would have loaded a
  model and written files under `-Dsrcmorph.skip=true`. All four goals now assert it, `CalibrateMojo`
  included (it previously had no test constructing it at all).

### Removed
- **Relocation-stub module** (`llamacpp-ai-index-maven-plugin/`, `net.ladenthin:llamacpp-ai-index-maven-plugin`)
  removed from the active reactor. It was published once at `1.0.4` as part of the `1.1.1` release
  and the redirect verified working end-to-end (a clean-environment Maven resolution of the old
  coordinates correctly follows the relocation through to `srcmorph-maven-plugin:1.1.1` and its
  full dependency graph). The published `1.0.4` artifact is permanent on Maven Central regardless
  of this repo's module list and will never need another release, so there is no reason to keep
  carrying the module (and the `versions:set -Dexcludes=...` caveat it required) in ongoing
  development. `.github/workflows/publish.yml` updated accordingly (no more per-module handling for
  a fourth artifact).

- **`llamaLibraryPath` / `srcmorph.llama.libraryPath` — a parameter that never worked.**

  It was declared on every mojo and threaded through
  `SrcMorphConfiguration`, `EngineSupport`, both `LlamaCppJniConfigFactory` methods and
  `LlamaCppJniConfig` — and no code read it, so setting it was a silent no-op. Git history shows this
  was never a working feature a refactor broke: in `a1df3e0 "Initial version."` it is already the
  first field of the then six-field `LlamaCppJniConfig` record, next to `modelPath`, while that
  commit's provider built its `ModelParameters` from `modelPath` / `contextSize` / `threads` alone.

  It is removed rather than implemented, because the binding already offers the same knob and offers
  it *better*. `net.ladenthin.llama.lib.path` is an ordinary JVM system property and the loader's
  highest-precedence source; `MAVEN_OPTS` and `.mvn/jvm.config` set it before the JVM starts, so it
  is in place before any class loads. A plugin parameter can only act once the provider builds a
  model — by then `LlamaModel`'s static initializer may already have resolved the library, and every
  later value is silently ignored. Implementing it would have added a weaker path with a
  silent-failure mode alongside one that always works. Verified empirically that the property reaches
  the build JVM: `mvn -Dnet.ladenthin.llama.lib.path=…` is readable via `System.getProperty` from
  inside the build.

  **Migration:** replace `<llamaLibraryPath>DIR</llamaLibraryPath>` with
  `-Dnet.ladenthin.llama.lib.path=DIR` on the command line, in `MAVEN_OPTS`, or in `.mvn/jvm.config`.
  A POM that still sets the element will fail with an unknown-parameter error; it was doing nothing
  before, so no behaviour changes with it gone. The plugin README's GPU section already documented
  the property as the runtime override.

- **`config.AiGenerationKind` — dead code from a superseded design.** The enum (`FILE_SUMMARY`,
  `PACKAGE_SUMMARY`, `FILE_KEYWORDS`, `PACKAGE_KEYWORDS`) is the original *fixed* generation taxonomy
  from when the goals were `SummarizeFilesMojo` / `SummarizePackagesMojo`, and `8d1be51` and `0a6f462` deliberately replaced
  that with configurable prompt keys and rule routing — which is design principle 6, "prompt templates
  and routing rules are data, never hardcoded in Java". No production class referenced it in any
  commit. Its two apparent users were concurrency-infrastructure examples that had borrowed the name:
  the Lincheck one never touched the enum at all (it exercises an `AtomicInteger`) and is now
  `AtomicCounterLincheckTest`; the jcstress one now races a real enum and is `AiOversizeStrategyRace`.
  Both examples are kept — only their misleading names and the dead enum are gone. It was `public` in
  a published artifact, so formally a breaking change; nothing could construct or receive it, so
  practically not.

## [1.1.1] - 2026-07-15

### Added
- **Reactor split**: the former single-module `llamacpp-ai-index-maven-plugin` is now a 3-module
  Maven reactor under a new parent, `net.ladenthin:srcmorph-parent` — `srcmorph` (new core library,
  `net.ladenthin:srcmorph`, framework-free — no Maven Plugin API dependency), `srcmorph-cli` (new
  standalone CLI, `net.ladenthin:srcmorph-cli`, driven by a single JSON/YAML configuration file,
  ships as a `java -jar`-ready fat jar), and `srcmorph-maven-plugin` (the original plugin, now a
  thin wrapper depending on `srcmorph`). All three (plus the parent pom) release together at one
  shared version.
- **Plugin renamed** from `net.ladenthin:llamacpp-ai-index-maven-plugin` to
  `net.ladenthin:srcmorph-maven-plugin` in this same release (goal prefix `ai-index` → `srcmorph`;
  package `net.ladenthin.maven.llamacpp.aiindex.mojo` → `net.ladenthin.maven.srcmorph.mojo`; every
  `@Parameter` property `aiIndex.*` → `srcmorph.*`). A new, independently-versioned relocation-stub
  module/POM (`net.ladenthin:llamacpp-ai-index-maven-plugin:1.0.4`, no source, no dependencies, only
  `<distributionManagement><relocation>`) keeps the old coordinates resolvable on Maven Central,
  redirecting to `net.ladenthin:srcmorph-maven-plugin:1.1.1`.
- New engine layer in `srcmorph` (`GenerateEngine`, `AggregatePackagesEngine`,
  `AggregateProjectEngine`, `CalibrateEngine`) extracted from what used to be each mojo's
  `execute()` body, plus a new shared root configuration object,
  `net.ladenthin.srcmorph.config.SrcMorphConfiguration`, bindable identically from Maven plexus XML,
  Jackson JSON/YAML (the new CLI), or plain Java code.
- New `examples/` directory at the repo root: paired `config_*.json`/`.yaml` fixtures for every
  `srcmorph-cli` command (`Plan`, `GenerateFileIndex`, `All`, `Calibrate`), paired `run_*.sh`/`.bat`
  launcher scripts, and an example `logbackConfiguration.xml` — all runnable out of the box with the
  `mock` provider (no GGUF model required).
- Per-module `README.md` files (`srcmorph/README.md`, `srcmorph-cli/README.md`) and a rewritten,
  product-level root `README.md`/`CLAUDE.md` describing the reactor.

### Changed
- Logging in the extracted core/CLI layers moved from a constructor-injected Maven `Log` to
  `org.slf4j.Logger` (see the `1.0.x` entries below for the indexer-layer half of this change,
  already shipped before the reactor split).
- `.github/workflows/publish.yml` adapted to the 4-module reactor: per-module jar upload/release
  globs, a repo-wide crash-dump glob, the PIT step scoped to `srcmorph` (the only module with a
  mutation-testing gate), the `vmlens` job scoped to `srcmorph-maven-plugin` (where its
  test actually lives), and Coveralls/Codecov pointed at `srcmorph`'s jacoco report.

### Notes
- **This release renames the Maven plugin's coordinates, package, goal prefix, and `@Parameter`
  property names.** `net.ladenthin:llamacpp-ai-index-maven-plugin` → `net.ladenthin:srcmorph-maven-plugin`;
  package `net.ladenthin.maven.llamacpp.aiindex.mojo` → `net.ladenthin.maven.srcmorph.mojo`; goal
  prefix `ai-index` → `srcmorph`; every `aiIndex.*` property → `srcmorph.*`. Existing consumers of
  the old coordinates are not broken: a new, independently-versioned relocation-stub artifact
  (`net.ladenthin:llamacpp-ai-index-maven-plugin:1.0.4`, POM-only, no source/dependencies) is
  published with a `<distributionManagement><relocation>` pointing at
  `net.ladenthin:srcmorph-maven-plugin:1.1.1`, so Maven transparently redirects any build still
  declaring the old artifactId.

### Fixed
- **Sources-jar signing**: `maven-source-plugin`'s `attach-sources` execution was bound to the
  `verify` phase instead of `package` in all three real modules. `maven-gpg-plugin`'s signing
  execution is also bound to `verify`, and within the same phase execution order follows
  declaration/inheritance order — the inherited gpg execution ran before the sources jar was
  built, so it was silently omitted from every signed bundle. Rebound `attach-sources` to
  `package` (its own goal default). Also gave the relocation-stub module a `<parent>` so it
  inherits the release profile's signing/publishing plugins at all (it previously had none),
  while keeping its own version pinned independently.

## [1.1.0] - 2026-07-11

> Reconstructed on 2026-08-29 from `git log v1.0.2..v1.1.0` (49 commits). This section was missing
> entirely: `v1.1.0` was tagged and published, but neither a heading nor a compare-link was ever
> added, so the chain jumped `1.0.2 → 1.1.1` and a shipped version was undocumented. The omission is
> recorded in [`../workspace/workflows/release-process.md`](../workspace/workflows/release-process.md)
> as the reason the CHANGELOG footer now has a mechanical headings/links/tags check.

### Added
- **`srcmorph-cli` module** — a standalone JSON/YAML-driven CLI (`net.ladenthin:srcmorph-cli`), with
  the fat jar as its deliverable, bound unconditionally to the `package` phase.

### Changed
- **Restructured into a 3-module Maven reactor** (migration steps 3–9): a parent pom plus the
  framework-free core library `srcmorph`, the new `srcmorph-cli`, and the Maven plugin. The engine
  layer was extracted out of the mojos so the core carries no dependency on the Maven API.
- **The Maven plugin was renamed** to `net.ladenthin:srcmorph-maven-plugin` (package
  `net.ladenthin.maven.srcmorph.mojo`, goal prefix `srcmorph`, `@Parameter` properties `srcmorph.*`).
  The old coordinates stayed resolvable via a one-time relocation-stub POM published at `1.0.4`.
- **Maven `Log` replaced with SLF4J** throughout the indexer layer, which is what makes the core
  module Maven-free.
- CI adapted to the reactor; a Gradle/BouncyCastle signing-key preflight added (cross-repo sync);
  `net.ladenthin:llama` 5.0.4 → 5.0.6; `pitest-maven` and `junit-jupiter` bumped.

### Fixed
- `module-info` for `srcmorph` was missing `requires org.slf4j`.
- The code-style job used bare `spotless`/`spotbugs` prefix goals, which do not resolve in a reactor.
- REUSE compliance: SPDX headers / license sidecars added to the benchmark files.

## [1.0.2] - 2026-07-02

### Changed
- Split the big-window size-routing rule per source kind in the `ai-index-selftest` example POM: the former single `big-window` rule is now `big-window-java` (prompt `file-body-java`), and a matching `big-window-sql` rule (prompt `file-body-sql`) routes oversized `.sql` sources to the same large-context model — so an oversized `.sql` file keeps the SQL prompt instead of being misrouted/uncovered.
- Bumped `net.ladenthin:llama` 5.0.3 → 5.0.4.

## [1.0.1] - 2026-06-29

### Added
- Third-level **project index** (`aggregate-project` goal): a single `project.ai.md` table of contents harvesting each package's lead, with an optional one-call AI `#### Overview` paragraph.
- **Rule-based file routing** for the `generate` goal: each `<fieldGeneration>` routes a file to a `(model, prompt)` via a composable `<condition>` tree (`<and>`/`<or>`/`<not>` over `extensions`/`size`/`lines`/`modifiedAfter`/`modifiedBefore`/`pathGlob`), with `<priority>`, `<skip>`, and exactly one explicit `<fallback>`. Plan-then-execute loads each model once; `aiIndex.planOnly=true` prints the routing plan tree without loading a model.
- Plan-time **context-window fit check**: oversized files fail the build up front; new big-window fallback preset (IBM Granite 4.0-H-Tiny, Apache-2.0) covers source files up to ~1 MB.
- Independently switchable phases via `aiIndex.file.skip` / `aiIndex.package.skip` / `aiIndex.project.skip` (plus the global `aiIndex.skip`).
- File-size **band filter** (`minFileSizeBytes` / `maxFileSizeBytes`) for size-tiered indexing, and `excludes` globs to skip generated/trivial sources.
- Deterministic child-link list (`F` header field) for project → package → file navigation.
- Per-file **progress bar with ETA** and measured (actual vs estimated) per-file generation time.
- **GPU support**: opt-in `gpu-cuda` / `gpu-vulkan` profiles, parameterized native classifier, and `gpuLayers` / `mainGpu` / `devices` knobs.
- Opt-in sampling controls (default off): `min_p`, `top_n_sigma`, DRY repetition suppression, reasoning/think budget, and model-level `swa-full` + cache-reuse.
- Extension-selected file-body prompts (java / sql / fallback).

### Changed
- Default model preset is now `gpt-oss-20B-mxfp4`; all gpt-oss presets are GPU-ready.
- Pinned `net.ladenthin:llama` to the released `5.0.3` (dropped the SNAPSHOT dependency and snapshot repository).
- Bumped JUnit 6.1.0 → 6.1.1 and palantir-java-format 2.92.0 → 2.94.0.

### Removed
- The empty-body generation retry mechanism.

## [1.0.0] - 2026-06-08

First public release on Maven Central. Pre-OpenSSF history themes (March–May 2026): Java 8 compatibility, key-indexed `aiDefinitions` (PR #21), Sonatype Central Portal migration, JaCoCo+Coveralls+Codecov, GH Actions major-version bumps, CodeQL v3→v4, model catalogue (Qwen2.5-Coder, Ministral 8B/14B, Gemma 4 MoE).

### Added
- OpenSSF Best Practices badge and passing-level artifacts (CONTRIBUTING.md, SECURITY.md, CHANGELOG.md).

### Changed
- Switched runtime dependency to `net.ladenthin:llama` 5.0.2 (official Maven Central release).
- CI: added `startgate` abort-window environment before publish pipeline.
- CI: separated snapshot and release publish paths; added `check-snapshot` / `check-tag` gate jobs.
- CI: bumped `softprops/action-gh-release` v2 → v3 (Node 24 compatibility).
- CI: added JaCoCo coverage reporting with Coveralls and Codecov integration.
- README: grouped badges by category (Build / Coverage / Package / License / Community); added Maven Central dependency section.

### Fixed
- CI: quoted gate job names to avoid YAML colon-in-scalar parsing error.
- CI: use `GITHUB_TOKEN` for Coveralls `github-token` parameter instead of `COVERALLS_TOKEN`.

---

[Unreleased]: https://github.com/bernardladenthin/srcmorph/compare/v1.4.0...HEAD
[1.4.0]: https://github.com/bernardladenthin/srcmorph/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/bernardladenthin/srcmorph/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/bernardladenthin/srcmorph/compare/v1.1.1...v1.2.0
[1.1.1]: https://github.com/bernardladenthin/srcmorph/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/bernardladenthin/srcmorph/compare/v1.0.2...v1.1.0
[1.0.2]: https://github.com/bernardladenthin/srcmorph/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/bernardladenthin/srcmorph/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/bernardladenthin/srcmorph/releases/tag/v1.0.0
