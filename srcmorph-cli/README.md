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

Both `.json`/`.js` (parsed via a
Jackson `ObjectMapper`) and `.yaml`/`.yml` (parsed via `YAMLMapper`) are supported — pick whichever you
prefer; both mappers are configured strictly (`FAIL_ON_UNKNOWN_PROPERTIES`), so a typo'd key fails the
run immediately rather than being silently ignored. On startup the CLI logs the parsed configuration
back to you (re-serialized as both JSON and YAML) so you can confirm exactly what will run before any
model loads or file is written.

See [`../examples/`](../examples/) for a complete, ready-to-run set of `config_*.json`/`.yaml` files
(all using the `mock` provider, so they run with no GGUF model on disk) plus paired
`run_*.sh`/`run_*.bat` launcher scripts and an example `simplelogger.properties`.

## Config-file reference

The root object is `net.ladenthin.srcmorph.cli.configuration.CConfiguration` — a plain, public-field
JavaBean (Jackson binds it directly, no getters/setters needed):

```java
public class CConfiguration {
    public CCommand command = CCommand.Plan;
    public SrcMorphConfiguration srcMorph = new SrcMorphConfiguration();
}
```

- **`command`** — one of the six `CCommand` values below. Defaults to `Plan` — the safe default: no
  command means no model load and nothing written.
- **`srcMorph`** — the **same** `net.ladenthin.srcmorph.config.SrcMorphConfiguration` object the Maven
  plugin's mojos build from their own `@Parameter` fields. **Every key under `srcMorph` in your JSON/YAML
  file reads identically to the matching Maven `<configuration>` XML element** — `baseDirectory`,
  `outputDirectory`, `subtrees`, `excludes`, `fileExtensions`, `generationProvider`,
  `promptDefinitions`, `profileDirectory`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`,
  `factDefinitions`, the `llama*` fallback parameters, `force`, `planOnly`, `projectName`. A relative
  `modelDirectory` or `profileDirectory` (like every relative path here) is relative to the working
  directory the CLI is started in. With a `profileDirectory`, the rules' `promptKey` and `factsKey`
  are files there (`<key>.md`, `<key>.tsv`) and need no definition; the examples read the measured
  profile [`../profiles/v2/`](../profiles/README.md) that way. If you already know the plugin's XML shape, you
  already know this shape — see
  [`../srcmorph-maven-plugin/README.md`](../srcmorph-maven-plugin/README.md)'s
  "Configuration" section for the full field-by-field reference (routing-rule conditions, oversize
  strategies, per-model sampling parameters, etc.), which applies verbatim here.

### The seven `CCommand` values

| Command | What it does |
|---|---|
| `Plan` | Runs `GenerateEngine` with `planOnly` **forced** to `true`, regardless of what the file says — builds and logs the routing plan (which model + prompt each file would get) and stops. No model is loaded, nothing is written. The default; always safe to run. |
| `GenerateFileIndex` | Phase 1, exactly as configured: indexes source files and fills in their AI-generated summaries (`GenerateEngine`). |
| `AggregatePackages` | Phase 2: aggregates per-package `.ai.md` index files (`AggregatePackagesEngine`), with the top-level `packageFieldGenerations` as its rules when set (like the plugin's `aggregate-packages` execution), otherwise `srcMorph.fieldGenerations`. |
| `AggregateProject` | Phase 3: aggregates the single project-level `.ai.md` index (`AggregateProjectEngine`), with the top-level `projectFieldGenerations` when set, otherwise `srcMorph.fieldGenerations`. |
| `All` | Runs `GenerateFileIndex`, then `AggregatePackages`, then `AggregateProject` in order, stopping at the first phase that fails; each aggregation phase with its own rules when set. [`examples/build-your-own-jar/srcmorph.yaml`](../examples/build-your-own-jar/srcmorph.yaml) is the plugin's self-test profile in this form. |
| `Calibrate` | Loads each distinct model your `fieldGenerations` would route to, measures its prefill/decode throughput, and prints a paste-ready `<calibration>` XML block per model to standard output (`CalibrateEngine`). |
| `ListModels` | Asks every OpenAI-compatible server your `aiDefinitions` name (`GET <baseUrl>/models`, with the definition's key) which models it serves, and prints to standard output a table (id, token limit, price where the server states one, as OpenRouter does) and a paste-ready `aiDefinitions` YAML block with one entry per model, copied from that server's first definition (`ListModelsEngine`). The top-level `modelFilter` keeps the ids that contain it, ignoring case. Sends no prompt, writes nothing. |

### One shared `srcMorph` object across commands — a nuance to know

Unlike the Maven plugin, where each goal execution gets its **own** `<fieldGenerations>` block in the
POM, the CLI's `All` and `Calibrate` commands reuse the **one** `srcMorph.fieldGenerations` list across
every engine they run. That list means different things to different engines:

- **`GenerateFileIndex`** *routes*: each file is matched to exactly one rule by `condition` + `priority`
  (the explicit `fallback` catches anything unmatched).
- **`AggregatePackages`** does **not** route by condition — it applies **every** entry in
  `fieldGenerations` to every package, in list order, and the **last** entry's output is kept.
- **`AggregateProject`**'s optional AI overview paragraph uses only `fieldGenerations[0]`.

If your `fieldGenerations` list has more than one entry (e.g. a conditioned Java-file rule plus a
fallback, as in [`../examples/config_All.json`](../examples/config_All.json)), running `All` will
still aggregate every package using the *entire* list's rules layered on top of each other (last one
wins) — which is usually fine (the rules typically write similar structured Markdown), but is worth
knowing if you see a package's body reflecting your fallback rule's prompt rather than the
file-specific one. A single fallback-only rule sidesteps the question entirely and behaves identically
across all three phases. See [`../srcmorph/README.md`](../srcmorph/README.md) for the engine-level
detail.

## Testing

- `MainTest` — unit tests for the extension-dispatched loader and the JSON/YAML mappers.
- `configuration.ConfigBindingTest` — round-trips a private ad-hoc
  `src/test/resources/test-fixtures/minimal-generate.{json,yaml}` fixture pair.
- `ExamplesConfigBindingTest` — sweeps every shipped `../examples/config_*.{json,yaml}` file (the
  public, documented examples) through the same strict mappers, so an example can never silently drift
  out of sync with `SrcMorphConfiguration`'s field set.
- `CliEndToEndTest` — drives `Main#run()` directly (no forked process) against the `mock` provider for
  the `All` and `Plan` commands, asserting the expected `.ai.md` tree lands on disk (or, for `Plan`,
  that nothing is written and the original configuration object is never mutated).
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
