<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: MIT
-->

# Windows on an Intel Core Ultra: which artifacts, and how to run srcmorph

For a Windows machine with **one or more JDKs in `C:\Program Files\Java`**, and Maven if you want the
plugin route. Nothing else is needed: no C++ toolchain, no GPU SDK, and no checkout of this
repository. The hardware notes are for an **Intel Core Ultra 7 165U** (Meteor Lake-U). The steps
apply to any Windows x86-64 machine with an Intel iGPU; only the CPU paragraph changes between CPU
generations.

srcmorph runs the GGUF model in-process through `net.ladenthin:llama` (java-llama.cpp), so its
natives are what decide CPU against GPU. The binding's own Windows guide covers the same natives from
the java-llama.cpp side: [java-llama.cpp, docs/windows-intel-core-ultra.md](https://github.com/bernardladenthin/java-llama.cpp/blob/main/docs/windows-intel-core-ultra.md).

> [!NOTE]
> **Version.** This guide is for **srcmorph 1.3.0**, the first release on `net.ladenthin:llama`
> 6.0.0, where the natives are separate jars and a GPU backend is an additive module. Until 1.3.0 is
> released, the same artifacts are built from `main` as **1.3.0-SNAPSHOT**:
> - the fat jars are on the rolling [`snapshot` pre-release](https://github.com/bernardladenthin/srcmorph/releases/tag/snapshot);
> - the plugin is on Central's snapshot repository (see [Route B](#route-b--the-maven-plugin-in-your-project)).
>
> srcmorph 1.2.0 runs on llama 5.1.0. Its steps are the same, but its log lines differ from the ones
> quoted below.

Two routes, and both were run on Windows for this guide (1.3.0-SNAPSHOT, a local JDK 21, `cmd.exe`):

| Route | Needs | Use it when |
|---|---|---|
| **A — the CLI fat jar** | a JDK | you want to index a directory, with no Maven project involved |
| **B — the Maven plugin** | a JDK and Maven | the index belongs to a Maven build (`mvn srcmorph:generate`) |

## Which artifacts

**Route A** downloads one file from the [GitHub release](https://github.com/bernardladenthin/srcmorph/releases),
never from Maven Central. Both jars run on any OS; the Vulkan one also uses the iGPU on Windows:

| Take | Size | Why |
|---|--:|---|
| `srcmorph-cli-<version>-jar-with-dependencies-vulkan-windows-x86-64.jar` | ~111 MiB | the CLI, the CPU natives of every desktop platform, and the Vulkan module for the Arc iGPU through the installed driver's `vulkan-1.dll`. Nothing else to install |
| `srcmorph-cli-<version>-jar-with-dependencies.jar` | ~99 MiB | the same without the Vulkan module: CPU only |

Take the Vulkan jar. On a machine without a working Vulkan driver, ggml skips the module and runs on
the CPU, so it is never slower than the plain jar. Every `.jar` has a `.asc` signature next to it in
the release.

**Route B** gets the plugin from Maven Central and declares **one extra natives jar** as a dependency
of the plugin: `net.ladenthin:llama:6.0.0:vulkan-windows-x86-64` (12 MiB). The CPU natives already
come with the plugin through `net.ladenthin:llama-platform`.

**Skip** these:
- the `cuda13-*` and `rocm-*` variants: there is no NVIDIA or AMD GPU here;
- `sycl-windows-x86-64`: it needs the oneAPI 2026 runtime DLLs on every machine;
- `opencl-windows-x86-64`: ggml's OpenCL backend is tuned for Qualcomm Adreno;
- `openvino-windows-x86-64`: it is opt-in and can be slower than the CPU. See [the NPU](#the-npu).

What the hardware implies:

- **CPU:** AVX2 and AVX-VNNI, no AVX-512, no AMX (Core Ultra series 1 ships AVX-512 disabled). ggml
  therefore loads **`ggml-cpu-alderlake.dll`**.
- **iGPU:** Arc Graphics (Xe-LPG, 4 Xe-cores) shares LPDDR5 with the CPU. Vulkan helps prompt
  processing, which is most of srcmorph's work since every file is a long prompt. Token generation
  is memory-bound on both, at a 15 W package budget. Measure both with `Calibrate` (below) before you
  believe either.
- **Cores:** 2 P-cores, 8 E-cores and 2 LP-E-cores. Do not use all 14 threads; see [Thread
  count](#thread-count).

## The JDK

| | JDK floor |
|---|---|
| the CLI, the plugin, the core and the natives | **Java 8+** (all of it is class-file major 52) |

Any installed JDK from 8 up works, so take the newest. To pick one for a single window without
touching the machine-wide setting:

```bat
set "JAVA_HOME=C:\Program Files\Java\openjdk21.0.11"
set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version
```

For Route B, `mvn -v` reports the JDK Maven runs on; the plugin runs inside Maven's JVM.

## A model

Put a GGUF anywhere, for example `C:\models\`. The model choice is measured in
[the benchmark](ai-index-benchmark/COMPARISON.md) and summarised in the plugin README's
[Recommended Models](../srcmorph-maven-plugin/README.md#recommended-models). For a 15 W laptop:

- **`granite-4.0-h-tiny-Q4_K_M.gguf`**: the fastest on a CPU (~4×), Apache-2.0. Start here.
- **`Seed-Coder-8B-Instruct-Q4_K_M.gguf`**: a small dense coder, MIT.
- **`Qwen3-Coder-30B-A3B-Instruct-UD-Q4_K_XL.gguf`**: more faithful, but a ~17 GB file. Only with
  32 GB RAM or more.

## The configuration: one file for both routes

The CLI reads JSON or YAML, and the plugin reads the same keys as XML elements; the CLI README says
so key by key. Save this as `C:\srcmorph\srcmorph.yaml`. Relative paths are relative to the directory
the CLI is started in:

```yaml
command: Plan
srcMorph:
  baseDirectory: "C:/path/to/project"
  outputDirectory: "C:/path/to/project/target/srcmorph-ai"
  subtrees:
    - "src/main/java"
  fileExtensions:
    - ".java"
  generationProvider: llamacpp-jni
  modelDirectory: "C:/models"
  promptDefinitions:
    - key: file-body
      template: "Summarize this Java file as structured Markdown: purpose, public API, collaborators."
  aiDefinitions:
    - key: local
      modelPath: granite-4.0-h-tiny-Q4_K_M.gguf
      contextSize: 16384
      maxOutputTokens: 1024
      temperature: 0.15
      threads: 8
      threadsBatch: 10
      gpuLayers: 99
  fieldGenerations:
    - promptKey: file-body
      aiDefinitionKey: local
      fallback: true
      onOversize: sample
```

- **`template`** is the system prompt and takes **no placeholders**. srcmorph sends the file name and
  the source as a separate user message.
- **`gpuLayers: 99`** offloads everything to the iGPU when the Vulkan module loaded. `0` keeps the
  model on the CPU, which is the comparison to measure.
- **`modelDirectory`** makes every relative `modelPath` relative to it. An absolute `modelPath` is
  used as it is.
- **`onOversize`** matters on day one. By default a file larger than the model's context window
  **fails the run on purpose**: srcmorph does not trim silently. `sample` summarises a representative
  slice instead; `mapReduce` and `deterministic` are the other strategies. The plan in the next step
  lists every file that would hit this.

In a double-quoted YAML string, use forward slashes or doubled backslashes for Windows paths, as
above; a single `\` starts an escape sequence there (`"C:\models"` fails to parse).

## Route A — the CLI

### Step 1 — the plan: no model loaded, nothing written

`command: Plan` is the default and is always safe. It loads no model and writes nothing; it prints
which model and prompt every file would get, and which files are larger than the window:

```bat
cd /d C:\srcmorph
java -jar srcmorph-cli-1.3.0-jar-with-dependencies-vulkan-windows-x86-64.jar srcmorph.yaml
```

With the default `onOversize`, an oversized file is reported like this, and the run stops there:

```
### (!) Over window - source exceeds the model's context window, would be trimmed (1)
- src/main/java/.../Main.java -> model `local` (source 15071 chars > window 7203 chars) onOversize=fail -> FAILS BUILD
```

That line was measured with a deliberately small 2048-token window. Raise `contextSize`, or keep
`onOversize: sample`, and run the plan again until no line says `FAILS BUILD`.

### Step 2 — the index

Set `command: GenerateFileIndex` for the per-file summaries, or `command: All` to also aggregate the
packages and the project. Then run the same command again:

```bat
java -jar srcmorph-cli-1.3.0-jar-with-dependencies-vulkan-windows-x86-64.jar srcmorph.yaml
```

The `.ai.md` files land under `outputDirectory`, mirroring the source tree.

**Re-runs are incremental.** A file is regenerated only in three cases: its source changed, its
settings changed, or its summary is **empty**. `force: true` regenerates everything. On the test
machine three consecutive runs over 6 files gave 6 written, then 4 written and 2 unchanged, then 1
written and 5 unchanged. The rewrites were the empty summaries: a small reasoning model (Qwen3-0.6B)
had spent its 256 output tokens thinking. If the same files are rewritten on every run, open them;
an empty body means `maxOutputTokens` is too small for that model.

### Step 3 — measure, then pick the settings

`command: Calibrate` loads each model your rules route to, measures its prefill and decode throughput,
and prints a paste-ready `<calibration>` block per model. Run it once with `gpuLayers: 99` and once
with `gpuLayers: 0`, with a few `threads` values each, and keep the fastest combination. On this class
of machine neither direction is obvious; see [Thread count](#thread-count).

## Route B — the Maven plugin in your project

The same configuration as XML in the project's `pom.xml`, with the Vulkan natives jar as a plugin
dependency:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>net.ladenthin</groupId>
            <artifactId>srcmorph-maven-plugin</artifactId>
            <version>1.3.0</version>
            <dependencies>
                <!-- added next to the CPU natives the plugin already brings; no GPU, no harm -->
                <dependency>
                    <groupId>net.ladenthin</groupId>
                    <artifactId>llama</artifactId>
                    <version>6.0.0</version>
                    <classifier>vulkan-windows-x86-64</classifier>
                </dependency>
            </dependencies>
            <configuration>
                <outputDirectory>${project.build.directory}/srcmorph-ai</outputDirectory>
                <generationProvider>llamacpp-jni</generationProvider>
                <modelDirectory>C:\models</modelDirectory>
                <aiDefinitions>
                    <aiDefinition>
                        <key>local</key>
                        <modelPath>granite-4.0-h-tiny-Q4_K_M.gguf</modelPath>
                        <contextSize>16384</contextSize>
                        <maxOutputTokens>1024</maxOutputTokens>
                        <temperature>0.15</temperature>
                        <threads>8</threads>
                        <threadsBatch>10</threadsBatch>
                        <gpuLayers>99</gpuLayers>
                    </aiDefinition>
                </aiDefinitions>
                <promptDefinitions>
                    <promptDefinition>
                        <key>file-body</key>
                        <template>Summarize this Java file as structured Markdown: purpose, public API, collaborators.</template>
                    </promptDefinition>
                </promptDefinitions>
                <fieldGenerations>
                    <fieldGeneration>
                        <fallback>true</fallback>
                        <onOversize>sample</onOversize>
                        <promptKey>file-body</promptKey>
                        <aiDefinitionKey>local</aiDefinitionKey>
                    </fieldGeneration>
                </fieldGenerations>
            </configuration>
        </plugin>
    </plugins>
</build>
```

In XML a Windows path needs no escaping. Then run:

```bat
cd /d C:\path\to\project
mvn srcmorph:generate -Dsrcmorph.planOnly=true
mvn srcmorph:generate
```

The first command prints the plan, like the CLI's `Plan`. The second writes the index. The plugin
also has `aggregate-packages`, `aggregate-project` and `calibrate` goals; see the
[plugin README](../srcmorph-maven-plugin/README.md) for binding them to phases.

**Before 1.3.0 is released**, version `1.3.0-SNAPSHOT` resolves only with Central's snapshot
repository added to the project:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>central-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <releases><enabled>false</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </pluginRepository>
</pluginRepositories>
```

## Thread count

The biggest single knob on this CPU. On Windows ggml runs its own thread pool and synchronises on a
barrier per operation. **The barrier waits for the slowest thread**, and the two LP-E-cores are much
slower than the P- and E-cores, so all 14 threads is very likely not the optimum.

Measure `threads` 6, 8 and 10 with `Calibrate` and keep the best. `threadsBatch` sets the
prompt-processing thread count separately and tolerates a higher value, because prompt processing has
enough work per barrier to hide the synchronisation cost. srcmorph sends long prompts, so
`threadsBatch` matters more here than in a chat.

## Verifying what loaded

The log names the CPU variant, the backends and the devices ggml ended up with. From the Vulkan run on
the test machine (an AMD Ryzen, hence `haswell`; a Core Ultra shows `alderlake`):

```
load_backend: loaded Vulkan backend from <dir>\ggml-vulkan.dll
load_backend: loaded CPU backend from <dir>\ggml-cpu-haswell.dll
load_backends_next_to_this_library: 3 backend(s) registered from <dir> [RPC, Vulkan, CPU], 3 device(s): Vulkan0 (Vulkan), Vulkan1 (Vulkan), CPU (CPU)
[jllama] native backend 'cpu' loaded from <dir> with GPU module(s) vulkan; 18 file(s) in place for ggml to load
```

- **Vulkan module missing from the last line** (`with no GPU module`): you are running the plain jar,
  or the plugin dependency is missing.
- **More than one Vulkan device** (`Vulkan0`, `Vulkan1`, …, as on a laptop with an iGPU and a
  discrete GPU): pick one by name with `devices: Vulkan1` in the `aiDefinition`. Device indices depend
  on which modules loaded, so always name the device, never rely on an index.

A Core Ultra 7 165U has a single GPU, so `Vulkan0` is the Arc iGPU.

`-Dnet.ladenthin.llama.backend=cpu` before `-jar` (or on the `mvn` command line) puts no GPU module in
place. That gives the clean CPU baseline for a comparison without switching jars.

## The NPU

`openvino-windows-x86-64` is the only path to the Intel **NPU**, and it is **opt-in** in the binding:
its jar does nothing until `-Dnet.ladenthin.llama.backend` names it. The reason is measured: OpenVINO's
CPU plugin registers as a device that llama.cpp then prefers, and on a host without an Intel
accelerator that dropped token generation to 0.2 tokens per second
([java-llama.cpp, gpu-module-jars.md](https://github.com/bernardladenthin/java-llama.cpp/blob/main/docs/benchmarks/gpu-module-jars.md)).
It also needs the Intel OpenVINO runtime DLLs on `PATH`. srcmorph ships no OpenVINO fat jar. With the
plugin, add the `openvino-windows-x86-64` natives jar as a second plugin dependency, run with
`-Dnet.ladenthin.llama.backend=vulkan,openvino`, and read the device list before trusting a number.
NPU use is **not measured** here.
