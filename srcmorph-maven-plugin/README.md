# srcmorph-maven-plugin

A Maven plugin for generating hierarchical, AI-readable documentation of source code projects
using local llama.cpp-compatible models. It creates structured `.ai.md` files per source file and
aggregates them into package-level (and project-level) summaries for fast semantic navigation and
retrieval.

> This module is one of three in the [srcmorph](../README.md) Maven reactor — see the root README
> for the product overview, badges, and build status. **This plugin was renamed** from
> `net.ladenthin:llamacpp-ai-index-maven-plugin` to `net.ladenthin:srcmorph-maven-plugin` (goal
> prefix `srcmorph`, properties `srcmorph.*`) as part of the reactor migration's final step.
> Consumers still declaring the old coordinates are transparently redirected: the old artifactId was
> published once (`1.0.4`, a tiny relocation-stub POM with only a
> `<distributionManagement><relocation>` pointing here) and the redirect verified working — see the
> root README's migration note. Internally, the plugin depends on
> [`net.ladenthin:srcmorph`](../srcmorph/README.md) (a sibling reactor module) for all of
> its engine/indexing logic — the five goal mojos in this module read the configuration file through
> the core's `SrcMorphConfigurationFileLoader`, fill what it leaves unset from the Maven project, and
> delegate to one of `srcmorph`'s `engine.*` classes. That split is an internal implementation
> detail; nothing below changes for a user of this plugin.

## Features
- Generate AI summaries for source files
- Per-kind prompts — class-based languages, SQL, shell, Perl, XML, views, API contracts, deploy files and a generic fallback — selected by routing rules (extension, file name, path, shebang, content)
- Weave searchable type, API and domain names into every summary
- Aggregate summaries at package level
- Build a single project index (one line + link per package) for top-down navigation, optionally with a one-call AI `#### Overview`
- Run any phase independently — toggle file / package / project on or off
- Exclude trivial or generated files with glob patterns
- Uses local models via llama.cpp by default (no cloud dependency); a model can instead run on any
  OpenAI-compatible API (OpenRouter, `llama-server`, jllama's `NativeServer`), chosen per model
- Incremental updates (skips unchanged files)
- Logs a rough per-file duration estimate before each generation (size, token count, expected time)
- Optimized for AI-assisted code understanding
## How It Works
The plugin runs in three phases, building a navigable index from fine to coarse.
### 1. File Generation (generate)
- Scans configured source directories
- Creates `.ai.md` files per source file
- Each file contains metadata header and markdown summary
### 2. Package Aggregation (aggregate-packages)
- Traverses generated `.ai.md` files
- Builds hierarchical package summaries
- Produces `package.ai.md` files; the header carries a deterministic `F` link list to each child (package → file navigation)
- Takes exactly one rule, `packageRules` of the configuration file (the package prompt and its model); none or more fail the goal
### 3. Project Index (aggregate-project)
- Harvests the one-line lead from every `package.ai.md` — the per-package listing is deterministic, no AI call
- Produces a single `project.ai.md`: one body line per package (its lead) with the clickable links in the header `F` list
- A compact, always-loadable table of contents an agent reads first to navigate down
- **Optional** (opt-in): configure one rule in `projectRules` (more fail the goal) and it makes *one* extra AI call to
  write a short `#### Overview` paragraph from the package leads. The deterministic per-package listing is
  unchanged; with no rule the goal stays purely deterministic and calls no model.
## Example Output
```
### AiMdDocument.java
- H: 1.0
- C: A48CED8C
- D: 2026-03-15T23:31:52Z
- T: 2026-03-19T18:13:31Z
- G: 1.0.0
- A: 0.0.0
- X: file
- P: 3F2A91C0
- N: 7D04E5B1
- M: 5C1E08A2
---
> Immutable value type pairing a deterministic metadata header with the AI-generated markdown body of one .ai.md document.

#### Purpose
- Hold one parsed `.ai.md` document as an `AiMdHeader` plus its markdown body.

#### Type
- record-shaped value class (Java); marked `@ConvertToRecord`.

#### Public API
- `header() -> AiMdHeader` — the document's metadata header.
- `body() -> String` — the AI-generated markdown body.
```

`P`, `N`, `M` and `K` are the CRC32 of what the summary was written with, each only when set: the
system prompt (with the rule's map and reduce prompts, when it names them), the fact counters, the
model (the definition's key, its `remoteModel` and the file name of its `modelPath`) and, for a
package or the project, the child summaries it read. A summary is rewritten when it is missing or
empty, or its header format (`H`), node type (`X`), title or `aiVersion` (`A`) changed, always; and
when one of the fields below differs while its switch under `regenerateOn` is `true`, as each is by
default (a summary that does not record a field differs too):

| Switch | Field | Changes with |
|---|---|---|
| `source` | `C` | the file's source; a package's files; the packages the project lists and their checksums |
| `prompt` | `P` | the rule's prompts, its map and reduce prompts too: a changed `map-chunk` rewrites every file of a rule that names it, also one that fit the window and never used it |
| `facts` | `N` | the rule's fact counters |
| `model` | `M` | the rule's model definition, or the model it names |
| `children` | `K` | a summary a package or the project is written from (a file re-summarized over the same source) |

```yaml
srcMorph:
  force: false            # true: rewrite every summary, whatever regenerateOn says
  regenerateOn:
    prompt: false         # e.g. keep the summaries of the old prompt after a prompt edit
```

A switch set to `false` keeps the summary and records the new value at its next rewrite: a run
record names the hashes for an unchanged file, so it shows which summaries an older prompt wrote.
`D`, `T` and `G` never cause a rewrite; a sampling parameter is in no hash (raise `aiVersion`).
## Requirements
- Java 8+ (the plugin and the core are Java 8 bytecode; the descriptor states Java 1.8)
- Maven 3.6.3+
- Local GGUF model (llama.cpp compatible), or a remote OpenAI-compatible model per definition

## Dependency

The plugin depends on [`net.ladenthin:llama`](https://central.sonatype.com/artifact/net.ladenthin/llama), the Java/JNI binding for llama.cpp (via the `srcmorph` core library it wraps).
It is published on Maven Central and resolves automatically — no manual installation required.
Since `llama` 6.0.0 the binding is split: `net.ladenthin:llama` holds the Java classes only, and every
native build is a natives jar of the same artifact (classifier `<backend>-<os>-<arch>`).
`srcmorph` depends on `net.ladenthin:llama-platform`, a pom naming the CPU natives of every desktop
platform (macOS: Metal), so the plugin runs anywhere out of the box:

```xml
<dependency>
    <groupId>net.ladenthin</groupId>
    <artifactId>llama-platform</artifactId>
    <version>6.0.0</version>
    <type>pom</type>
</dependency>
```
## Configuration
Since 1.4.0 the plugin is configured by **one file**, the same JSON or YAML file the CLI reads
([`srcmorph-cli/README.md`](../srcmorph-cli/README.md)): `srcmorph.yaml` in the project directory, or
wherever `<configurationFile>` (`-Dsrcmorph.configurationFile`) points. The POM names the goals and
nothing else. What the file leaves unset, Maven fills from the project:

| Key | Unset in the file |
|---|---|
| `baseDirectory` | `${project.basedir}` |
| `subtrees` | the project's source directory and resource directories that exist below the base directory (a resource's includes and excludes are not applied; a resource directory that holds the source directory, such as the project directory itself, is left out) |
| `outputDirectory` | `src/site/ai` below the base directory |
| `projectName` | `${project.name}` |

`generatorVersion`, the header's `G`, is srcmorph's own version unless the file sets it, as in the CLI.

Relative paths in the file resolve against the file's directory: the directories (`partsDirectory`
too), `runRecordFile`,
and a `modelPath`, `templateFile` or `factsFile` through `modelDirectory` / `profileDirectory`,
which are the file's directory when unset. A `${name}` in a value is a Maven property (the command
line's `-D`, then the project's `<properties>`, then the project's own values as a POM names them,
then the system's), so `gpuLayers: ${ai.gpuLayers}` takes `-Dai.gpuLayers=12`; `$${name}` writes a
literal `${name}` (a shell variable in an inline prompt). The project's values
are `basedir`, `project.basedir`, `project.build.directory`, `project.build.sourceDirectory`,
`project.groupId`, `project.artifactId`, `project.version` and `project.name`. An unknown one stays as
it is, except in a directory, `runRecordFile` or a subtree, where it fails the goal. Every goal takes
its section of the file:

| Goal | Rules |
|---|---|
| `generate`, `calibrate` | `srcMorph.rules`, the file rules |
| `aggregate-packages` | `packageRules`, exactly one rule |
| `aggregate-project` | `projectRules`, none (a deterministic index) or one (with an overview) |
| `check` | all three |

The file has three building blocks:

1. **`aiDefinitions`** — define each model once (path + sampling parameters), each with a `key`.
2. **`profileDirectory`** — the profile: one directory with a file per prompt (`<key>.md`) and per
   group of exact fact counters (`<key>.tsv`), named by key from the rules, so no prompt text and no
   regex is written into the configuration. A prompt is the **system prompt** and takes **no
   placeholders**: the file's path below the base directory (a package's name) and the source (for
   packages, the child summaries; for the
   optional project overview, the per-package leads) are sent as a separate user message
   (`PromptCatalog`). A `%s` in a prompt reaches the model literally. The measured profile of this
   repository is [`profiles/v3/`](../profiles/README.md); copy it into your project or point at a
   checkout. A `promptDefinitions` entry with an inline `template` (or its own `templateFile`) still
   works and wins over a file of the same key, as does a `factDefinitions` entry for facts.
3. **`rules`** — the routing **rules** of the file phase. Each rule maps a `promptKey` to an
   `aiDefinitionKey` (model id, which carries the full parameter set) and selects files with a
   composable **`condition`** tree: `and`/`or`/`not` over the leaves `extensions`, `size`
   (`min`/`max` bytes of the source as srcmorph reads it: the file's bytes with `\n` line endings and
   no BOM, a UTF-16 file its text's UTF-8 length; the run's `minFileSizeBytes`/`maxFileSizeBytes`
   band takes the size on disk instead, before a file is read), `lines` (`min`/`max`), `modifiedAfter`/`modifiedBefore` (ISO-8601 instant vs
   the file's last-modified time), `pathGlob` (base-relative glob, in the case written, so it
   matches the same on every system), `fileNames` (globs over the name without its directory, in any
   case: `Dockerfile`, `Jenkinsfile`, `Dockerfile.*`), `contentMatches` (a Java regular expression
   found in the first `contentHeadChars` characters of the file, 16,384 by default, so a Kubernetes
   manifest and an OpenAPI document of the same extension take different rules:
   `(?m)^apiVersion:`) and `interpreters` (the interpreter the file's `#!` line names, a version of
   it too: `[bash, sh]`, `[python]` matches `#!/usr/bin/env python3`). A glob takes `*`, `**`, `?`
   and `{a,b}` alternatives (`**/*.{yml,yaml}`). When several rules match, the
   highest `priority` wins (ties by declaration order). A rule may instead be `skip: true` (ignore
   matching files — a high-priority skip beats routes and the fallback), and **exactly one**
   `fallback: true` (no condition) catches the rest. A file that matches no rule and no fallback
   **fails the build**. So one `generate` run can index different file kinds/sizes with **different
   models *and* prompts**; it loads each model once. Run with `-Dsrcmorph.planOnly=true` to print the
   routing plan (a copy-pasteable Markdown table: file → rule id → prompt → context-window fit →
   estimated source tokens → rough time estimate; the tokens summed per model and per directory, the
   time per model and overall) and stop before loading any model. The plan also checks each file
   against its routed model's **context window**: a file too large for the window would lose content
   if trimmed. What happens then is **configuration only** — the plugin never picks a model for
   you — and is chosen per rule with `onOversize`:
   - **`fail`** *(default)* — abort the build (a hard fail). The fix is a rule with a size `condition`
     that routes oversized files to a model with a large enough window or to one of the strategies
     below (see the `granite-4.0-h-tiny-mapreduce` definition + the `big-java` / `big-sql` rules of
     [`srcmorph-selftest.yaml`](srcmorph-selftest.yaml) — IBM Granite 4.0-H-Tiny, Apache-2.0,
     map-reducing files up to ~1 MB over a 64K window). A large window alone is not enough: H-Tiny, a
     hybrid Mamba model whose KV cache grows only linearly, fits a 384K window on an 8 GB GPU (a
     ~995 KB / ~268K-token file ran without OOM), but over an 870 KB XML catalog in one pass it copied
     records with invented values instead of summarizing them.
   - **`sample`** — feed only the head of the file (trimmed to the window) in a single call. Fast and
     bounded; good for repetitive data where the head represents the whole.
   - **`mapReduce`** — split the file into window-sized chunks at line boundaries (with overlap so
     records are never torn mid-syntax), summarize **every** chunk, then combine the partial summaries
     in batches until one is left. No part of the file goes unread; the calls grow linearly with its
     size. **Size the chunks moderately.** Prefill is `O(n²)` in prompt length, so many cheap passes are
     far faster than a few giant ones (one 384K-token pass alone can dwarf a whole run): the self-test
     map-reduces with H-Tiny over 64K and samples with H-1B over 16K; map-reduce over a 384K-class window
     is the slowest combination. A local model sizes the chunks by the source's density in tokens; a
     chunk denser than that is read in two halves. The plan loads no model, so it counts the chunks by
     the character estimate: for a dense file of a local model, its chunk count and time are an
     estimate the run can correct.
   - **`sampleChunks`** — the same over `maxChunks` representative chunks (always the first and the last,
     evenly spaced between), to bound the time of an arbitrarily large file (e.g. 7 MB): a 16K-window
     model with `maxChunks: 6` is ~7 calls (~1 h order on a reference CPU; less on GPU) — the right
     trade-off for repetitive data. `maxChunks` applies only here; with another strategy it fails the
     rule set.

     Both chunked strategies tell the model what its input is: `mapPromptKey` names a prompt appended
     to the rule's prompt in every map call (whose user message starts with `Chunk i of n:`), and
     `reducePromptKey` one appended in every reduce call (whose input is the part summaries, `Part n:`).
     The profile's `map-chunk` says to index only what the chunk shows, `reduce-parts` to merge the
     parts into one summary and keep every name a part states. Without them every call takes the
     rule's prompt alone, which describes a whole file: a model then indexed a chunk of INSERT rows as
     a file and invented its schema, and answered a reduce with the prompt's own example repeated
     under `Part 8:`, `Part 9:`, ... until its output budget ended. Both keys apply only to `mapReduce`
     and `sampleChunks`.
   - **`deterministic`** — no model at all: emit a deterministic body (size, line count, head/tail
     sample). Instant; for pure data where no AI analysis is needed.

   Quality from a real model is best within Granite's validated 128K (~500 KB) and degrades gradually
   beyond it — a whole-file (or chunked) summary still beats a trimmed one. Every summary of an
   over-window file begins with what it read: `**Coverage:** 9 of 9 chunks summarized`,
   `**Coverage:** 6 of 9 chunks summarized (sampled)`, or for `sample`
   `**Coverage:** the first 12000 of 90000 characters (sampled head)`.

   **Exact counts with `factsKey` (optional, every file — not just oversize).** A sampled/chunked AI
   summary can't reliably *count* things (no single call sees the whole file — it will guess "25 rows").
   A rule's `factsKey` names a group of counters, `<profileDirectory>/<key>.tsv` (one `label<TAB>regex`
   per line, `#` comments, an `[ignore]` label for a region blanked before counting, `[raw]` for a
   counter that counts before the blanking, `[distinct]` for one that counts different names; see
   [`profiles/README.md`](../profiles/README.md)), and each reports
   its regex match count over the **whole** source, prepended to the body of every file the rule
   matches as an exact facts line — so downstream agents get authoritative structural counts in every
   summary. It's fully generic — the meaning is in the regex, so the same mechanism counts SQL rows or
   Java `\bboolean\b` fields; multi-line matching is opt-in via the inline `(?m)` flag. Keep patterns
   robust (a fact that miscounts is worse than none); the profile's Java, SQL, shell, Perl and XML
   counters are pinned by tests. Example:
   `**Facts (exact, whole file):** INSERT statements: 5; VALUES rows (approx): 33; tables: 2`. A rule
   can also list its counters inline in `facts`.

The POM:

```xml
<plugin>
    <groupId>net.ladenthin</groupId>
    <artifactId>srcmorph-maven-plugin</artifactId>
    <version>1.4.0</version>
    <!-- configured by ${project.basedir}/srcmorph.yaml; another file: <configuration>
         <configurationFile>...</configurationFile></configuration> -->
    <executions>
        <execution>
            <id>ai-generate</id>
            <phase>generate-resources</phase>
            <goals><goal>generate</goal></goals>
        </execution>
        <!-- Phase 2 reads the tree phase 1 wrote, phase 3 the package summaries. -->
        <execution>
            <id>ai-aggregate-packages</id>
            <phase>process-resources</phase>
            <goals><goal>aggregate-packages</goal></goals>
        </execution>
        <execution>
            <id>ai-aggregate-project</id>
            <phase>prepare-package</phase>
            <goals><goal>aggregate-project</goal></goals>
        </execution>
    </executions>
</plugin>
```

and `srcmorph.yaml` next to it:

```yaml
# provider defaults to "mock"; llamacpp-jni runs a real GGUF model
packageRules:            # aggregate-packages: exactly one rule
  - promptKey: package-body
    aiDefinitionKey: coder
projectRules:            # aggregate-project: leave it out for a purely deterministic index
  - promptKey: project-body
    aiDefinitionKey: coder

srcMorph:
  generationProvider: llamacpp-jni
  # 1) Models: define once, reference by key.
  aiDefinitions:
    - key: coder
      modelPath: /path/to/model.gguf
      contextSize: 32768
      maxOutputTokens: 1536
      temperature: 0.7
      threads: 8
  # 2) The profile: srcmorph's profiles/v3/ copied into your project (or a checkout of it). Every
  #    promptKey and factsKey here is a file there: file-body-oop.md, file-body-fallback.md,
  #    package-body.md, project-body.md, java-facts.tsv.
  profileDirectory: src/srcmorph/profile
  # 3) The file rules: a .java rule (matched by its condition) and the fallback for everything else.
  #    See "Routing rules" below for size/lines/modified/glob conditions, priority, and skip.
  rules:
    - id: java
      promptKey: file-body-oop
      aiDefinitionKey: coder
      factsKey: java-facts
      condition:
        extensions: [".java"]
    - id: fallback
      fallback: true
      promptKey: file-body-fallback
      aiDefinitionKey: coder
```

[`srcmorph-selftest.yaml`](srcmorph-selftest.yaml) is the full configuration this module indexes
itself with (`-P srcmorph-selftest`), three models and the rules of every language;
[`.github/plugin-it/`](../.github/plugin-it/) is the fixture the integration test runs, a
configuration of the same shape on the `mock` provider.

### Routing rules (conditions, priority, skip, plan)
Within one `generate` run you can route files to different models **and** prompts by size, language,
age or path. Example — Java files to three context windows of gpt-oss-20b (three `aiDefinitions` with
the same model and different `contextSize`), a map-reduce over a small fast Granite window for huge
files, skip generated sources, and a fallback for everything else:

```yaml
  rules:
    - id: skip-generated                             # skip wins by priority
      skip: true
      priority: 100
      condition: {pathGlob: "**/generated/**"}
    - id: java-small
      promptKey: file-body-oop
      aiDefinitionKey: gpt-oss-16k
      condition:
        and:
          conditions:
            - extensions: [".java"]
            - size: {max: 16384}
    - id: java-mid
      promptKey: file-body-oop
      aiDefinitionKey: gpt-oss-48k
      condition:
        and:
          conditions:
            - extensions: [".java"]
            - size: {min: 16384, max: 49152}
    - id: java-large
      promptKey: file-body-oop
      aiDefinitionKey: gpt-oss-20B-mxfp4
      condition:
        and:
          conditions:
            - extensions: [".java"]
            - size: {min: 49152, max: 1048576}     # a max: at one priority the first match wins
    - id: java-huge                                  # huge files: chunk over a SMALL fast window + combine
      promptKey: file-body-oop
      aiDefinitionKey: granite-4.0-h-1b-fastchunk
      onOversize: sampleChunks
      maxChunks: 6                                   # ~7 cheap calls; bounds time to ~1 h/file
      mapPromptKey: map-chunk                        # appended in a map call: the input is one chunk
      reducePromptKey: reduce-parts                  # appended in a reduce call: merge the parts
      facts:                                         # exact whole-file counts, prepended to the body
        - {label: boolean fields, pattern: '\bboolean\b'}
        - {label: methods, pattern: '(?m)^[ \t]+(public|private|protected)[^(\n]*+\('}
      condition:
        and:
          conditions:
            - extensions: [".java"]
            - size: {min: 1048576}
    - id: fallback
      fallback: true
      promptKey: file-body-fallback
      aiDefinitionKey: gpt-oss-20B-mxfp4
```

Notes: size bounds are **min-exclusive / max-inclusive** so `band2.min == band1.max` is non-overlapping;
`lines` works the same way; of two matching rules of one priority the one declared first wins, so
bands of one priority need a `max` each (or the narrower band a higher priority), or a later band is
never reached; `modifiedAfter: "2026-01-01T00:00:00Z"` only (re)indexes
recently changed files. `onOversize` (`fail` *(default)* / `sample` / `mapReduce` / `sampleChunks` / `deterministic` —
see the context-window note above) chooses what a rule does when a file is still larger than its routed
model's window; `maxChunks` is how many chunks `sampleChunks` summarizes, `mapPromptKey` and
`reducePromptKey` the prompts a map and a reduce call append to the rule's. The package and project
rules default to `mapReduce`: their input is the summaries below them, and leaving a part out would
drop a whole subtree. `onIncomplete` chooses
what a rule does when the model's answer is no summary -- empty (for example a reasoning-only answer),
cut off at the output budget, or a response without an answer: `fail` *(default)* counts the file as
failed, `retryOnce` asks once more first, `keep` writes a cut-off answer that has text. Such an answer
is never written otherwise: the file keeps its older summary, or none, every other file is still
indexed, the goal fails at its end naming the count, and the next run tries the file again. Preview
the mapping without running a model:

```
mvn srcmorph:generate -Dsrcmorph.planOnly=true
```

## Usage
```
mvn generate-resources            # the file phase, bound as in the example
mvn prepare-package               # all three phases
mvn srcmorph:check                # the configuration as a run would see it: plan, profile files and
                                  # every model (file, GGUF header, baseUrl, apiKeyEnv); loads no model
mvn srcmorph:generate -Dsrcmorph.planOnly=true    # the routing plan only
```
This module indexes two of the core library's packages with its own configuration (needs the GGUF
files; `-Dai.modelDir` names their directory):
```
mvn -pl srcmorph-maven-plugin -P srcmorph-selftest prepare-package -Dai.modelDir=/path/to/models
```
## Plugin Configuration
The POM sets only these; everything else is the configuration file (above).
- `configurationFile` (`srcmorph.configurationFile`) — the configuration file, JSON or YAML (default:
  `${project.basedir}/srcmorph.yaml`). A missing file fails the goal, naming where it looked
- `force` (`srcmorph.force`) — regenerate every summary although it is up to date (default: `false`);
  `true` overrides the file's `force`
- `planOnly` (`srcmorph.planOnly`, goals `generate`, `aggregate-packages`, `aggregate-project`) —
  `generate` prints the routing plan and stops before loading any model, the two aggregation goals do
  not run (default: `false`); `true` overrides the file's `planOnly`
- `skip` (`srcmorph.skip`) — global switch: skip **every** goal (default: `false`)
- Per-phase switches — turn any of the three phases on/off independently (each default `false`).
  Named after the three index levels (`file` / `package` / `project`, the `x` node types):
  - `srcmorph.file.skip` — skip the **file** phase (the `generate` goal)
  - `srcmorph.package.skip` — skip the **package** phase (the `aggregate-packages` goal)
  - `srcmorph.project.skip` — skip the **project** phase (the `aggregate-project` goal)

  So `-Dsrcmorph.package.skip=true` runs file + project only, `-Dsrcmorph.skip=true` runs
  nothing, and the defaults run all three. `calibrate` and `check` are diagnostic goals that only the
  global switch skips.

The keys of the file under `srcMorph` (`baseDirectory`, `subtrees`, `outputDirectory`, `fileExtensions`,
`fileNames`, `fileInterpreters`, `contentHeadChars`, `excludes`, `minFileSizeBytes` /
`maxFileSizeBytes`, `generationProvider`, `modelDirectory`,
`profileDirectory`, `promptDefinitions`, `factDefinitions`, `aiDefinitions`, `rules`,
`force`, `regenerateOn`, `planOnly`, `pruneOrphans`, `aiVersion`, `projectName`, `runRecordFile`,
`partsDirectory`) are described in
[`srcmorph-cli/README.md`](../srcmorph-cli/README.md) and [`srcmorph/README.md`](../srcmorph/README.md).
A configured subtree that does not exist, or lies outside the base directory, fails the goal; file
extensions match with their dot and in any case. `fileNames` adds files by name, for those without an
extension (`Dockerfile`, `Jenkinsfile`, `Makefile`): globs over the name, in any case.
`fileInterpreters` adds the scripts without an extension whose `#!` line names one of the
interpreters (`[bash, sh, perl, python]`: `bin/deploy`, a Git hook); that line is read only for a
file the subtrees, the excludes and the size band let through, and one that cannot be read is
skipped with a warning. `contentHeadChars` (default 16,384, at least 256) is how much of a file the
`contentMatches` and `interpreters` conditions read while planning: raise it when the marker of a kind
comes after a long license header; the head is never read past the file's end. A source file
is read as UTF-8, or as UTF-16 when it starts with that byte order mark; a byte order mark is not
part of the text the model and the fact counters see.

The `generate` plan lists the **orphaned summaries**: the file summaries (`X: file`) below the output
directory that no file of the plan is summarized into, of a source that was deleted, renamed or newly
excluded, or that a skip rule takes now. The package and project phases would go on reading them.
`pruneOrphans: true` (default `false`) deletes them before the run generates, each with a log line;
a `planOnly` run and `srcmorph:check` delete nothing. Leave it off while you narrow the subtrees for a
single run: the summaries of the subtrees left out are orphans of that run too.

### Per-model `aiDefinitions` parameters

Every model knob lives inside its entry of `aiDefinitions` (referenced by `aiDefinitionKey`). Only `key` and `modelPath` are required (`modelPath` not for an `openai-compatible`
model); everything else has a default. The defaults below are the shipped values
(`ModelDefinition.DEFAULT_*`).

| Key | Default | Description |
|---|---|---|
| `key` | *(required)* | Identifier referenced by a rule's `aiDefinitionKey` |
| `modelPath` | *(required)* | Path to the GGUF model file: absolute, or relative to `modelDirectory` (without it, to the configuration file's directory). Not used by an `openai-compatible` model |
| `provider` | *(empty)* | The backend of this model: `llamacpp-jni`, `openai-compatible` or `mock`; empty uses the run's `generationProvider`. Lets one run send some files to a local GGUF and others to a remote model |
| `remoteModel` | *(empty)* | `openai-compatible` only: the model id requested (the `model` field, e.g. `openai/gpt-6-luna` on OpenRouter); empty sends none, which a single-model `llama-server` or `NativeServer` accepts |
| `baseUrl` | `https://openrouter.ai/api/v1` | `openai-compatible` only: the API base; `<baseUrl>/chat/completions` is called. A local server is `http://127.0.0.1:<port>/v1`. Must be an `http(s)` URL without an `@` (no `user:key@`), query or fragment, checked before the run starts: the key goes in `apiKeyEnv` |
| `apiKeyEnv` | *(empty)* | `openai-compatible` only: the **name** of the environment variable holding the API key, sent as `Authorization: Bearer` (for OpenRouter `OPENROUTER_API_KEY`). The key itself never goes into a configuration file. Empty (the default) sends no key. A named variable that is unset or empty fails the run before any request, as does a key that would go over plain `http` to another machine: a key goes only where a model names it, so a custom `baseUrl` never receives your OpenRouter key by accident. A key is only sent over `https`, or over plain `http` to a loopback host (`localhost`, `127.x.x.x`, `[::1]`); any other plain-`http` `baseUrl` fails before anything is sent, so set `apiKeyEnv` empty for a keyless server on another machine |
| `stream` | `false` | `openai-compatible` only: request the answer as Server-Sent Events (`stream: true`, `stream_options.include_usage: true`) and assemble it into the same chat response, so the summary does not change. A stream without the closing `data: [DONE]` or with an `error` chunk fails the file. Required for the GitHub Copilot API Gateway, whose non-streaming path adds and runs VS Code's own tools (see [docs/copilot-api-gateway.md](../docs/copilot-api-gateway.md)); it also moves the read timeout from the whole answer to the gap between two events |
| `requestsPerMinute` | `0` | `openai-compatible` only: at most this many requests to the model's server start within any minute; a request beyond it waits until fewer than that many started in the last minute (until the newest of the starts that have to leave the window is a minute old), instead of being answered with `429`. Every model of one server with the same `apiKeyEnv` counts in one window, across the phases of a run and the runs of one process (mvnd, an IDE); each request waits for its own model's limit (a model at 10 sends while fewer than 10 requests of any of them started in the last minute), so a later run with a higher limit sends at it. Set it below the server's own limit, e.g. 25 for the Copilot API Gateway's `rateLimitPerMinute` 30. `0` or less: no limit |
| `maxRetries` | `3` | `openai-compatible` only: how often a request is sent again after a `408`, a `429` (not one that says the quota or credit is used up), a `5xx` other than `501` / `505` or a connect timeout, after the server's `Retry-After` (seconds or an HTTP date, at most 5 minutes) or an exponential backoff from 1 s (at most 60 s, plus up to a quarter as jitter). Any other failure is final at once; a prompt the server refuses as longer than the model's context (`413`, or `400` saying so) is shrunk and asked again, as for a local model. `0` sends every request once. A request that got no answer within the read timeout, or whose answer stopped while it was read, is not sent again: the server may already be generating and billing it (a `POST` goes in streaming mode, so the JDK does not resend it either). A request that still fails counts its file as failed; the other files are indexed and the goal fails at its end |
| `contextSize` | `32768` | Context window in tokens |
| `maxOutputTokens` | `128` | Max generated tokens per call |
| `threads` | `4` (fewer on a machine with fewer cores) | CPU threads for inference. Never set more than the machine has cores: ggml's threads wait on each other, and an oversubscribed run is orders of magnitude slower |
| `temperature` | `0.15` | Sampling temperature |
| `topP` | `0.9` | Nucleus (top-p) sampling threshold |
| `topK` | `40` | Top-k sampling limit (`0` = disabled) |
| `minP` | `0.0` | Min-p sampling threshold (`0.0` = disabled) |
| `topNSigma` | `-1.0` | Top-n-sigma sampling threshold (`-1.0` = disabled) |
| `repeatPenalty` | `1.0` | Repetition penalty (`1.0` = disabled) |
| `repeatLastN` | `-1` | Window the `repeatPenalty` above acts on (`--repeat-last-n`); `-1` = leave llama.cpp's own window (64 tokens), `0` = disable the penalty entirely. Configuring a penalty without its range leaves the strength adjustable but the reach fixed, which is why the two belong together |
| `charsPerToken` | `4` | Chars-per-token estimate; drives the automatic `maxInputChars` trim budget (`maxInputChars` itself is derived, not a field) and the plan. Use a value at or below your model's real ratio so the budget stays conservative. A local (`llamacpp-jni`) model counts each prompt with its own tokenizer before the call, so there the estimate only picks which files are counted: of one over it the prompt with a head of at most four estimated windows is counted (the whole file when it is shorter), and by that density it goes in one call when it fits after all, and is otherwise chunked; a prompt over `contextSize - maxOutputTokens` (less 4 tokens a count does not see) is not sent: its window shrinks by the measured density, up to four times. Where the source may not be cut (a whole file, a chunk), a window that would cut it hands the file to the rule's `onOversize` (a chunk is read in two halves); only a `sample` head still over the limit after the four smaller windows fails its file |
| `warnOnTrim` | `true` | Log a warning when the source is trimmed to fit the window |
| `cachePrompt` | `true` | Reuse the shared prompt-prefix KV across files (`cache_prompt`) |
| `swaFull` | `true` | Keep the full-size sliding-window-attention KV cache (`--swa-full`) |
| `cacheReuse` | `256` | KV prefix-reuse minimum chunk size in tokens (`--cache-reuse`; `0` = off) |
| `flashAttn` | `false` | Enable Flash Attention (`--flash-attn`). Lowers KV-cache memory and speeds up attention where the backend supports it, and it is the precondition for `cacheTypeV` below |
| `cacheTypeK` | *(empty)* | KV-cache data type for K (`--cache-type-k`): `f32`, `f16`, `bf16`, `q8_0`, `q4_0`, `q4_1`, `iq4_nl`, `q5_0`, `q5_1`; empty leaves the default. Quantizing the KV cache is the most direct trade of quality for context length — at `q8_0` the cache costs about half of `f16`, so a larger `contextSize` (or `swaFull`) fits the same VRAM. Whether it pays off is measurable: `srcmorph:calibrate` reports the prompt-cache reuse the extra room buys |
| `cacheTypeV` | *(empty)* | KV-cache data type for V (`--cache-type-v`); same values as `cacheTypeK`. **Quantizing V generally requires `flashAttn`** — without it llama.cpp will usually refuse to load, so set the two together |
| `seed` | `-1` | RNG seed for generation; `-1` leaves upstream's random-seed-per-request default. Set it to make a re-run produce the same `.ai.md` body instead of a fresh sample, so re-indexing yields a reviewable diff. **Not bit-reproducibility** — llama.cpp is not identical across thread counts, batch sizes or backends, so this pins the sampling, not the arithmetic; same machine + same config is the guarantee |
| `gpuLayers` | `-1` | GPU layers to offload (`--gpu-layers`); `-1` = auto-fit to free VRAM, `0` = force CPU, `>0` = partial. GPU native only |
| `cpuMoeLayers` | `-1` | Keep the MoE expert weights of the first *n* layers on the CPU (`--n-cpu-moe` / `-ncmoe`); `-1` = leave default, `0` = keep none on the CPU. Usually the better trade than lowering `gpuLayers` on a MoE model: it moves only the expert weights, so a much larger model fits the same VRAM at a smaller speed cost. GPU native only |
| `cpuFfnLayers` | `-1` | Dense-model counterpart of `cpuMoeLayers`: keep the FFN weights of the first *n* layers on the CPU (`--n-cpu-ffn` / `-ncffn`); `-1` = leave default, `0` = keep none on the CPU. GPU native only |
| `kvUnifiedPerSlot` | `-1` | Per-slot unified KV context cap (`--kv-unified-per-slot`); `-1` = leave default. Must be positive when set |
| `batchSize` | `-1` | Logical maximum batch size (`--batch-size`); `-1` = leave default. A prefill-throughput knob, and prefill is what dominates an indexing run: every file is one large prompt with a short answer. Must be positive when set |
| `ubatchSize` | `-1` | Physical maximum batch size (`--ubatch-size`); `-1` = leave default. Bounds how much of a logical batch reaches the backend at once, so it trades prefill speed against peak memory. Must be positive when set |
| `threadsBatch` | `-1` | Threads for batch/prompt processing (`--threads-batch`); `-1` = reuse `threads`. Prefill parallelises differently from decode, so the optimum often is not the decode thread count — on a machine with efficiency cores, more threads help prompt processing while hurting generation. Must be positive when set |
| `lazyMode` | *(empty)* | On-demand reading of lazy-loadable tensors (`--tensor-read-lazy`): `off`, `auto` or `on`; empty leaves the default. Trades resident memory for disk reads and shortens model load time; **requires mmap** |
| `mainGpu` | `-1` | Primary GPU index (`--main-gpu`); `-1` = leave default. Matters on multi-GPU hosts (e.g. a Vulkan build enumerates every GPU) |
| `devices` | *(empty)* | Explicit device selection (`--device`), comma-separated backend device names (e.g. `Vulkan1`); takes precedence over `mainGpu` |
| `chatTemplateEnableThinking` | *(unset)* | Chat-template thinking mode (`enable_thinking` kwarg): `true` or `false` is forwarded verbatim, unset omits the kwarg so the model's own template default applies. Set `false` for Gemma 4 to keep thought tokens out of stored output |
| `reasoningEffort` | *(empty)* | Reasoning effort (`low`/`medium`/`high`), the `reasoning_effort` kwarg for gpt-oss harmony and templates such as Kolibri-1's, and for an `openai-compatible` model a field of the request; empty sends none, so the model's own default applies. gpt-oss was measured at `low` (its template's default is `medium`), so a gpt-oss definition names `low`, as the self-test's does. Thinking off is sent as llama.cpp's server sends it, `enable_thinking` `false` and no `reasoning_effort`: `none` sends that instead of the effort, and while `chatTemplateEnableThinking` is `false` the effort is not sent, since a template that reads it first (Kolibri-1) would reason anyway |
| `reasoningBudgetTokens` | `-1` | Cap on harmony reasoning tokens (`-1` = unrestricted) |
| `dryMultiplier` | `0.0` | DRY repetition-penalty multiplier (`0.0` = disabled); the other `dry*` knobs only apply when this is `> 0` |
| `dryBase` | `1.75` | DRY exponential base |
| `dryAllowedLength` | `2` | Longest n-gram that may repeat without DRY penalty |
| `dryPenaltyLastN` | `-1` | DRY look-back window in tokens (`-1` = whole context, `0` = off) |
| `drySequenceBreakers` | *(empty)* | DRY sequence-breaker strings; empty = the binding defaults |
| `stopStrings` | *(empty)* | Extra stop strings that end generation |

**An `openai-compatible` model** sends the source file to the server at `baseUrl`; with the default
that is OpenRouter, i.e. a third party. It sends the same system prompt and user message as the
local provider and only the portable sampling knobs: `maxOutputTokens`, `temperature`, `topP`,
`seed` (when set), `stopStrings`, and these only when the definition gives them a value of its own:
`topK` (other than `40`), `minP` (when positive), `repeatPenalty`
(other than `1.0`; as `repetition_penalty` and `repeat_penalty`) and `reasoningEffort` (when set; as
`reasoning.effort` and `reasoning_effort`). The body is shaped for OpenRouter and llama.cpp's
`llama-server`, each of which reads its own name and ignores the other. The knobs of a local llama.cpp context (threads, GPU, KV cache, DRY, prompt
caching) are ignored. A `408`, a `429` (not one saying the quota is used up), a `5xx` other than `501` /
`505` or a connect timeout is retried (`maxRetries`); a read timeout after the request was sent, and any
other non-2xx answer, fail the file with the status and the server's error text. With `stream` set, the answer arrives as Server-Sent Events and is assembled
before it is parsed. For the models of a GitHub Copilot subscription through a local gateway, see
[docs/copilot-api-gateway.md](../docs/copilot-api-gateway.md).

```yaml
  aiDefinitions:
    - key: luna
      provider: openai-compatible
      remoteModel: openai/gpt-6-luna
      apiKeyEnv: OPENROUTER_API_KEY
      maxOutputTokens: 1024
```

## Prompt System
Prompts are referenced by key from the rules and read from the profile (`profileDirectory`). The
self-test configuration uses thirteen prompt files (nine file prompts, the package and project
prompts and the two map-reduce parts) and seventeen fact-counter files of
[`profiles/v3/`](../profiles/README.md); the benchmark and the examples read the same files, so each
prompt and each counter exists once in the repository:
- `file-body-oop` — summarizes a single source file of a class-based language: Java, Kotlin, Groovy, Scala, and TypeScript (types, public API, dependencies)
- `file-body-sql` — summarizes a single SQL file as schema (tables/views/procedures, columns,
  the tables it reads vs writes, and relationships)
- `file-body-shell`, `file-body-perl`, `file-body-xml` — the same for shell scripts, Perl and XML
- `file-body-fallback` — generic multi-language summary for any other source file
- `file-body-view`, `file-body-contract`, `file-body-ops` — JSP and Facelets views; API contracts
  (OpenAPI, Protobuf, ...); CI, Docker, Kubernetes and other deploy files. The self-test routes
  every kind `examples/copilot-api-gateway/srcmorph-luna.yaml` routes but the Java EE classes (the
  `java` rule takes their prompt), each measured on real projects, most with their fact counters
  (`typescript` and `pyproject` have none); Ansible goes to `file-body-fallback` without counters
  there, where the self-test's model names more of a play's modules
- `package-body` — synthesizes a package summary from the already-generated file summaries
- `project-body` — (optional) synthesizes the short project `#### Overview` paragraph from the package leads
- `map-chunk`, `reduce-parts` — not prompts of their own: a chunked rule's `mapPromptKey` /
  `reducePromptKey` appends them to its prompt in the map and the reduce calls (one chunk of the input;
  the part summaries to merge)

For the `generate` goal the rule, and with it the prompt, is chosen per file by the routing rules
above: the matching rule with the highest `priority`, otherwise the one `fallback`.

Each summary begins with a one-sentence blockquote lead, followed by structured `####` sections.
Prompts are optimized to avoid code blocks, formatter artifacts, and empty outputs, and to produce structured markdown.
## Output Structure
```
src/site/ai/
└── main/
    └── java/
        └── com/
            └── example/
                ├── MyClass.java.ai.md
                ├── AnotherClass.java.ai.md
                └── package.ai.md
```

The `calibrate` goal writes two more files into the same `outputDirectory`:

```
src/site/ai/
├── srcmorph-calibration.json
└── srcmorph-calibration.yaml
```

Both carry one entry per measured model — `modelKey`, `loadSeconds`,
`prefillTokensPerSecond`, `decodeTokensPerSecond`, `charsPerToken`,
`midPrefillTokensPerSecond`, `cachedPromptTokens` — so a calibration run can be diffed
across machines or committed as a baseline, instead of existing only as console output.
The three figures that also appear in the paste-ready YAML `calibration:` block (for the model's
entry under `aiDefinitions`) are formatted identically, so the two can never disagree about what
was measured.

## Design Principles
- Deterministic metadata (hash-based change detection)
- Separation of concerns (header = metadata, body = summary)
- AI-friendly structure (predictable and hierarchical)
- Local-first (no external APIs required; a remote model is an explicit, per-model opt-in)
## Known Limitations
- Model output may require normalization (handled in code)
- Large models increase runtime
- Output quality depends on chosen model

## Recommended Models
Based on an 8-model × 2-prompt benchmark run against this codebase — full results, per-model
pros/cons, a source-faithfulness deep-dive, and reproduction steps in
[docs/ai-index-benchmark](../docs/ai-index-benchmark/COMPARISON.md):

> **On the `-Dai.*` flags below** (`ai.model`, `ai.gpuLayers`, `ai.mainGpu`, `ai.devices`, …): these are
> **this repo's own Maven build properties** (defined in `srcmorph-maven-plugin/pom.xml` and named as
> `${ai.*}` in `srcmorph-selftest.yaml`), overridable per run with `-D` when you build *this* repo. They
> are **not** plugin `@Parameter`s — a downstream build sets the same knobs in its own configuration file
> (`gpuLayers`, `mainGpu`, `devices`, `aiDefinitionKey`), with `${...}` for its own properties. The
> published mojo parameters are the separate `srcmorph.*` set.

- **`gpt-oss-20B-mxfp4` — the production default** (switch with `-Dai.model=<key>`). The native MXFP4
  quant at a 96K window; it inherits the benchmark's accuracy lead (gpt-oss-20b was most *accurate* per
  file, won 5/6 in the per-file matrix — measured on the `c96k`/UD-Q4_K_XL quant, but E5 shows quant
  choice is within noise so the native MXFP4 is the better-quality swap), run at `reasoningEffort=low`
  and a 96K window so it covers files up to ~250 KB untrimmed. Slowest of the set (~2× the 30B) — the
  accepted cost for accuracy. See the preset/timing details below.
- **Qwen3-Coder-30B-A3B-Instruct** — throughput alternative (and best of the non-reasoning models for
  large Java files): most complete/faithful of the fast models, code-specialized, Apache-2.0,
  ~3.3B-active MoE, 262K context. Pick it when throughput beats the last points of fidelity.
- **Granite-4.0-H-Tiny** — fastest on CPU (~4×, flat-KV hybrid, Apache-2.0); best for very large
  or many files when throughput beats the last points of fidelity.
- **Seed-Coder-8B-Instruct** — clean, permissive (MIT) small dense coder.
- Avoid `Qwen3.5-4B` (thinking tax, no quality gain).

### gpt-oss-20b presets, large files, and timing

`gpt-oss` is a *reasoning* model whose analysis tokens share the output budget, so use
`reasoningEffort=low` for code summaries (best quality here) and size the budget to the file. The
self-test profile carries gpt-oss-20b once, `gpt-oss-20B-mxfp4` at a 96K window, plus the two Granite
presets for larger files (`granite-4.0-h-tiny-mapreduce`, `granite-4.0-h-1b-fastchunk`). What a window
covers, measured on CPU (full rationale in [COMPARISON.md §11](../docs/ai-index-benchmark/COMPARISON.md)):

| `contextSize` | covers up to | ~ time (CPU) |
|---|---|---|
| 16K | ~40 KB | ~1–2 min |
| 48K | ~125 KB | ~25 min @ 100 KB |
| **96K (the profile's)** | ~260 KB | ~80 min @ 250 KB |

- **96K is the profile's window:** a measured A/B shows a wider context window costs only RAM, not
  per-file time, so it covers every file up to ~250 KB with no trimming while small files stay just as
  fast. Lower `contextSize` only to save RAM. Hard ceiling is the 128K window (~480–500 KB of code);
  larger files route to the Granite presets.
- **On a CPU, timing is quadratic, not linear:** prefill ≈ `24.4·n + 0.000674·n²` ms (n = prompt
  tokens; gpt-oss-20b on a Ryzen 7 5800H), because attention is O(n) per token — which is why
  throughput drops as files grow.
- **The logged estimate is based on GPT-6 Luna**, the benchmark's remote model: `2.1 s + 0.105 ms`
  per prompt token `+ 6.45 ms` per output token, linear up to the largest measured prompts (98K
  tokens). The plan, the progress bar and the per-file line use it for every model without a
  `calibration`; the plan and the per-file line say so (`based on GPT-6 Luna`). A local model is far
  slower, so run `Calibrate` once and paste its `calibration:` block, after which they say `from the
  model's calibration`.

## GPU acceleration (opt-in)
The default natives are CPU (from `net.ladenthin:llama-platform`, see "Dependency" above). On an NVIDIA
RTX 3070 a CUDA build measured **~4.5× the CPU decode speed**; Vulkan also works (AMD + NVIDIA) but pays
a one-time shader-compilation cost on the first run. OpenCL is intentionally not offered (llama.cpp's
OpenCL backend does not support NVIDIA GPUs).

How the native is found (`net.ladenthin:llama` 6.0.0): `net.ladenthin.llama.loader.LlamaLoader` tries
`net.ladenthin.llama.lib.path`, then `java.library.path`, then the natives jars on the classpath. The
library itself (`jllama.dll` with ggml and the CPU modules) comes from the CPU natives jar; a GPU
natives jar holds **one ggml backend module** (`ggml-cuda.dll`, `ggml-vulkan.dll`, …), which the loader
puts next to the library. ggml opens a module only when its vendor runtime is installed; otherwise the
work stays on the CPU modules. So there are two ways to enable a GPU when running the *published*
plugin in your own build.

**Recommended — add the GPU natives jar to the plugin's own classpath.** Declare the matching
`net.ladenthin:llama` natives jar as a dependency of the plugin in your POM. It is added next to the
CPU natives, not swapped in — no library path to manage, and a machine without the GPU runtime still
runs on the CPU:

```xml
<plugin>
  <groupId>net.ladenthin</groupId>
  <artifactId>srcmorph-maven-plugin</artifactId>
  <version>...</version>
  <dependencies>
    <dependency>
      <groupId>net.ladenthin</groupId>
      <artifactId>llama</artifactId>
      <version>6.0.0</version>
      <classifier>cuda13-windows-x86-64</classifier> <!-- NVIDIA; or vulkan-windows-x86-64 -->
    </dependency>
  </dependencies>
</plugin>
```

```
mvn srcmorph:generate      # srcmorph.yaml sets generationProvider: llamacpp-jni; + the GPU runtime on PATH (see below)
```

The provider is set in the configuration file: `generationProvider: llamacpp-jni` under `srcMorph`,
or a model's own `provider`. The plugin has no property for it.

**Alternative — a natives directory (no POM change).** Unpack the CPU natives jar's directory and
copy the GPU module jar's files into it, then point `net.ladenthin.llama.lib.path` at that directory;
it is tried before the natives jars (the binding's README, "Natives outside Maven"):

```
mvn srcmorph:generate -Dnet.ladenthin.llama.lib.path=C:\path\to\gpu-native
```

> [!NOTE]
> There is deliberately **no plugin parameter** for this. The property is read by the binding's
> loader from `LlamaModel`'s static initializer, i.e. once per class-load — so setting it before the
> JVM starts (`MAVEN_OPTS`, `.mvn/jvm.config`, or `-D` on the command line as above) always works,
> while a plugin parameter could only act after the library may already have been resolved. A
> `llamaLibraryPath` parameter existed up to 1.1.1 and was removed in 1.2.0; no code ever read it,
> so it never had an effect. See the CHANGELOG.

In both cases:

- **CUDA** needs a matching CUDA 13 toolkit + driver, and the toolkit's `bin\x64` (with `cudart64_13.dll`,
  `cublas64_13.dll`, `cublasLt64_13.dll`) on `PATH` — the natives jar bundles only the backend module
  `ggml-cuda.dll`, not the CUDA runtime.
- **Vulkan** needs only the GPU driver (it brings `vulkan-1.dll`). A CLI jar built with exactly
  the CPU and Vulkan natives of a Windows machine is in [docs/windows-intel-core-ultra.md](../docs/windows-intel-core-ultra.md).
- **GPU layer offload** — set `gpuLayers` in your `aiDefinitions` entry (see "Per-model
  `aiDefinitions` parameters" above): `-1` (default) does **not** pin a layer count, so llama.cpp
  **auto-fits** as many layers as fit the card's free VRAM — the robust "runs on any card" setting (it
  never over-commits, so no OOM on a 6 GB card, and uses more layers on a bigger one). Pin a positive
  number only to force a specific **partial** split (a fixed count disables auto-fit), or `0` to force
  CPU. Measured on an 8 GB RTX 3070, auto-fit gpt-oss-20b ≈ 29 decode t/s (vs ≈ 8 on CPU); a card with
  ≥ 16 GB fits all layers and is far faster. *(In this repo's own gpt-oss presets the same knob is wired
  to the `-Dai.gpuLayers` build property — see the note under "Recommended Models" above.)*
- **Picking a GPU on a multi-GPU host** — set `mainGpu` / `devices` in your `aiDefinitions` entry: a
  **CUDA** build only enumerates NVIDIA devices, so a single-NVIDIA host needs nothing. A **Vulkan**
  build enumerates *every* GPU (an integrated GPU is often device `0`), so the default may pick the
  slower one — set `mainGpu: 1` to select the discrete GPU, or `devices: Vulkan1`
  for explicit device names (these map to the binding's `--main-gpu` / `--device`). *(In this repo's own
  gpt-oss presets these are wired to the `-Dai.mainGpu` / `-Dai.devices` build properties.)*

**Profiles (this repo's own reactor build only — test/benchmark).** `-P gpu-cuda` / `-P gpu-vulkan`
add the Windows x86-64 CUDA / Vulkan natives jar to `srcmorph`'s own test/compile classpath, and
`-Dllama.classifier=<classifier>` adds any other one (e.g. `cuda13-linux-x86-64`) — handy for the
native test or benchmarking on GPU here. For real indexing in another build use one of the two methods
above.

## Development

Run full build:
```
mvn clean install
```
Skip AI generation:
```
mvn clean install -Dsrcmorph.skip=true
```

### Contributors: do not upgrade jqwik past 1.9.3

> ⚠️ **DO NOT UPGRADE jqwik past 1.9.3.** jqwik 1.10.0 added an anti-AI prompt-injection string to test stdout; the 1.10.1 user guide states the library "is not meant to be used by any 'AI' coding agents at all." 1.9.3 is the last pre-disclosure release and is the pinned version (declared in `srcmorph/pom.xml`, the only module with a jqwik test dependency). See `../CLAUDE.md` section "jqwik prompt-injection in test output" for the full context. Dependabot is configured to ignore **all** `net.jqwik` updates (every version, including patches) — see the `ignore` rule in [`../.github/dependabot.yml`](../.github/dependabot.yml).

## A Note on History

Somewhere in early 2026, the same idea apparently occurred more than once. This repository
started with an implementation on March 15, 2026. Andrej Karpathy published his ["LLM wiki" gist](https://gist.github.com/karpathy/442a6bf555914893e9891c11519de94f)
on April 4, 2026. Google Cloud eventually formalized the very same pattern into the [Open Knowledge Format](https://cloud.google.com/blog/products/data-analytics/how-the-open-knowledge-format-can-improve-data-sharing/),
announced on June 12, 2026.

## License
Apache License 2.0

---

<details>
<summary>🍺 Beer-driven architectural review (2026-05-26)</summary>

> _Sometimes you spend two beers debating "architektonisch" vs "architekturiell" and the only conclusion is: ship the code anyway. Cheers!_

</details>
