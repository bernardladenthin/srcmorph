# srcmorph-cli

`net.ladenthin:srcmorph-cli` is a standalone command-line front end for
[srcmorph](../README.md): a single `java -jar` invocation driven by one JSON or YAML configuration
file, with no Maven project of your own required. It wraps the
[`srcmorph`](../srcmorph/README.md) core library's engines exactly the way the
[Maven plugin](../srcmorph-maven-plugin/README.md) does, following the
[BitcoinAddressFinder](https://github.com/bernardladenthin/BitcoinAddressFinder) `cli.Main` pattern.

## Running it

srcmorph publishes no runnable jar. The `srcmorph-cli` jar on Maven Central is the CLI without its
dependencies; the llama natives it needs differ per OS, CPU and GPU, and no set of pre-built jars
covers every combination. You build the runnable jar for your machine with Maven, from
[`../examples/build-your-own-jar/`](../examples/build-your-own-jar/):

```bash
cd examples/build-your-own-jar
./build.sh                       # build.bat on Windows
java -jar target/srcmorph.jar <path-to-config.json-or-.yaml>
```

Its `pom.xml` takes `srcmorph-cli` from Maven Central, swaps the CPU natives of every desktop
platform (`llama-platform`) for the one natives jar of your OS/arch (picked by a Maven profile), and
packs everything into `target/srcmorph.jar` together with `slf4j-simple` defaults (INFO, stdout,
timestamps). A GPU module of the same OS/arch is one more property, for example
`./build.sh -Dgpu=cuda13-linux-x86-64` or `build.bat -Dgpu=vulkan-windows-x86-64`; ggml loads it when
the vendor runtime is installed and runs on the CPU otherwise. The classifiers are listed in the
java-llama.cpp README, [Choosing the natives
jars](https://github.com/bernardladenthin/java-llama.cpp#choosing-the-natives-jars). A walkthrough for a
Windows laptop is in [`../docs/windows-intel-core-ultra.md`](../docs/windows-intel-core-ultra.md).

[JBang](https://www.jbang.dev/) runs the CLI without a build (`srcmorph-cli`'s jar names its main
class): `jbang net.ladenthin:srcmorph-cli:<version> <config>`, with a GPU module as
`--deps net.ladenthin:llama:<llama-version>:<classifier>`. Maven is the supported path.

Releases up to 1.3.0 attached pre-built fat jars to the
[GitHub Release](https://github.com/bernardladenthin/srcmorph/releases); from 1.4.0 there are none.

Both `.json`/`.js` and `.yaml`/`.yml` are supported — pick whichever you prefer. The file is the
same one the Maven plugin reads (since 1.4.0), through the same loader
(`SrcMorphConfigurationFileLoader`): an unknown key fails the run immediately rather than being
silently ignored, a `${name}` in a value is the Java system property `name` (`java -Dname=value -jar
...`; an unknown one stays as it is), and **relative paths resolve against the directory of the file**,
so a configuration names the same tree whatever directory you start the CLI in. On startup the CLI
logs the parsed configuration back to you (re-serialized as both JSON and YAML, its paths resolved) so
you can confirm exactly what will run before any model loads or file is written.

```
java -jar srcmorph.jar <configuration.json|.js|.yaml|.yml>
java -jar srcmorph.jar --help        # the usage and every command; -h for short
```

A wrong number of arguments fails with the usage.

See [`../examples/`](../examples/) for a complete, ready-to-run set of `config_*.json`/`.yaml` files
(all using the `mock` provider, so they run with no GGUF model on disk) plus paired
`run_*.sh`/`run_*.bat` launcher scripts and an example `simplelogger.properties`.

## Config-file reference

The file binds into `net.ladenthin.srcmorph.config.SrcMorphConfigurationFile` (the core), which the
CLI turns into its `net.ladenthin.srcmorph.cli.configuration.CConfiguration`:

```java
public class CConfiguration {
    public CCommand command = CCommand.Plan;
    public String modelFilter = "";
    public SrcMorphConfiguration srcMorph = new SrcMorphConfiguration();
    public List<AiFieldGenerationConfig> packageFieldGenerations;   // exactly one rule
    public List<AiFieldGenerationConfig> projectFieldGenerations;   // none or one
}
```

- **`command`** — one of the eight `CCommand` values below. Defaults to `Plan` — the safe default: no
  command means no model load and nothing written. An unknown one fails, naming the eight.
- **`srcMorph`** — the run: `baseDirectory` (unset: the directory of the file), `outputDirectory`
  (unset: `src/site/ai` below the base directory), `subtrees`, `excludes`, `fileExtensions`,
  `minFileSizeBytes` / `maxFileSizeBytes`, `generationProvider`, `promptDefinitions`,
  `profileDirectory`, `aiDefinitions`, `modelDirectory`, `fieldGenerations` (the file rules),
  `factDefinitions`, `force`, `planOnly`, `aiVersion`, `projectName`. A relative directory resolves
  against the file's directory, a relative `modelPath` against `modelDirectory`, a relative
  `templateFile` or `factsFile` against `profileDirectory`. With a `profileDirectory`, the rules'
  `promptKey` and `factsKey` are files there (`<key>.md`, `<key>.tsv`) and need no definition; the
  examples read the measured profile [`../profiles/v3/`](../profiles/README.md) that way. The plugin
  reads the same keys from the same file, so
  [`../srcmorph-maven-plugin/README.md`](../srcmorph-maven-plugin/README.md)'s "Configuration" section
  (routing-rule conditions, oversize strategies, per-model sampling parameters, etc.) applies verbatim
  here.
- **`packageFieldGenerations`** / **`projectFieldGenerations`** — the rule of the package phase and of
  the project overview (see "Each phase its own rules" below).
- **`modelFilter`** — `ListModels` only: keeps the model ids that contain it, ignoring case.

### The eight `CCommand` values

| Command | What it does |
|---|---|
| `Plan` | Runs `GenerateEngine` with `planOnly` **forced** to `true`, regardless of what the file says — builds and logs the routing plan (which model + prompt each file would get) and stops. No model is loaded, nothing is written. The default; always safe to run. |
| `Check` | Checks the configuration as the three phases would run it, without loading a model or writing anything: the plan of the file rules (every file routed, none over a window with `onOversize: fail`, no two into one summary), the package rule when there is one, the project rule, the profile files of every rule, and every model a rule names (a local model's file and GGUF header, a remote model's `baseUrl` and `apiKeyEnv`). Fails naming the first problem. The plugin's `srcmorph:check` does the same. |
| `GenerateFileIndex` | Phase 1, exactly as configured: indexes source files and fills in their AI-generated summaries (`GenerateEngine`). |
| `AggregatePackages` | Phase 2: aggregates per-package `.ai.md` index files (`AggregatePackagesEngine`), with the one rule of the top-level `packageFieldGenerations` (the plugin's `aggregate-packages` reads the same key); it fails without one. |
| `AggregateProject` | Phase 3: aggregates the single project-level `.ai.md` index (`AggregateProjectEngine`), with the one rule of the top-level `projectFieldGenerations` for the overview paragraph; unset, the index is written without one. |
| `All` | Runs `GenerateFileIndex`, then `AggregatePackages`, then `AggregateProject` in order, stopping at the first phase that fails; each aggregation phase with its own rule. [`examples/build-your-own-jar/srcmorph.yaml`](../examples/build-your-own-jar/srcmorph.yaml) is the plugin's self-test configuration in this form. |
| `Calibrate` | Loads each distinct model your `fieldGenerations` would route to, measures its prefill/decode throughput, and prints a paste-ready `<calibration>` XML block per model to standard output (`CalibrateEngine`). |
| `ListModels` | Asks every OpenAI-compatible server your `aiDefinitions` name (`GET <baseUrl>/models`, with the definition's key) which models it serves, and prints to standard output a table (id, token limit, price where the server states one, as OpenRouter does) and a paste-ready `aiDefinitions` YAML block with one entry per model, copied from that server's first definition (`ListModelsEngine`). The top-level `modelFilter` keeps the ids that contain it, ignoring case. Sends no prompt, writes nothing. |

### Each phase its own rules

`srcMorph.fieldGenerations` are the file rules: `GenerateFileIndex` *routes* each file to exactly one
of them by `condition` + `priority` (the explicit `fallback` catches anything unmatched), and
`Calibrate` measures the models they route to. They never reach an aggregation phase, which the
plugin's aggregate goals read from the same keys:

- **`AggregatePackages`** takes exactly one rule, the top-level `packageFieldGenerations` (the
  package prompt and its model), and fails without it.
- **`AggregateProject`** takes none or one, the top-level `projectFieldGenerations`: with one it
  writes the `#### Overview` paragraph, without one the index is fully deterministic.

[`../examples/config_All.json`](../examples/config_All.json) sets all three.

## Testing

- `MainTest` — the command line (usage, `--help`, a wrong argument count), the loader (paths against
  the file's directory, `${name}` from the system properties, an unknown command) and the mappers.
- `configuration.ConfigBindingTest` — round-trips a private ad-hoc
  `src/test/resources/test-fixtures/minimal-generate.{json,yaml}` fixture pair.
- `ExamplesConfigBindingTest` — sweeps every shipped `../examples/config_*.{json,yaml}` file (the
  public, documented examples) through the same strict loader, so an example can never silently drift
  out of sync with `SrcMorphConfiguration`'s field set.
- `CliEndToEndTest` — drives `Main#run()` directly (no forked process) against the `mock` provider for
  every command, asserting the expected `.ai.md` tree lands on disk (or, for `Plan` and `Check`, that
  nothing is written and the original configuration object is never mutated), and that each phase
  takes its own rule.
- `BuildYourOwnJarSelfTestParityTest` — `../examples/build-your-own-jar/srcmorph.yaml` and the plugin's
  `srcmorph-selftest.yaml` have the same models, profile, scope and rules.
- `CliArchitectureTest` — ArchUnit rules: this module is the reactor's leaf-most consumer, no public
  mutable fields outside `configuration`, no `System.exit`, no dependency on the Maven Plugin API.

## Building

```bash
mvn -pl srcmorph-cli -am package
```

produces `srcmorph-cli/target/srcmorph-cli-<version>.jar`, the plain jar that goes to Maven Central.
To run a local build, install the reactor (`mvn install`) and build
`examples/build-your-own-jar` with `-Dsrcmorph.version=<the reactor version>`; CI's `smoke-jar` job
does exactly that.
