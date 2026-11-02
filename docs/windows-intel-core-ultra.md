<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: MIT
-->

# srcmorph on Windows: build your own jar, then run it

Two steps, all in [`examples/build-your-own-jar/`](../examples/build-your-own-jar/):

| File | What it does |
|---|---|
| `pom.xml` | srcmorph from Maven Central plus **the llama natives this machine needs** |
| `build.bat` | one `mvn package` → `target\srcmorph.jar` |
| `srcmorph.yaml` | what to index, with which prompt and which model |
| `run.bat` | `java -jar target\srcmorph.jar srcmorph.yaml` |

Needed on the machine: a JDK 8 or newer and Maven on `PATH`, and the GGUF models the configuration
names. Nothing else: no C++
toolchain and no GPU SDK. The Vulkan module uses the GPU driver that is already installed.

Why build the jar yourself: srcmorph publishes no runnable jar, because no set of pre-built jars
covers every OS, CPU and GPU. Yours carries only the natives of this machine (32 MiB for the two
below) and the GPU module you choose.

> [!NOTE]
> This is for **srcmorph 1.4.0** (on `net.ladenthin:llama` 6.0.0); both resolve from Maven Central.
> For 1.3.0, read this page at the `v1.3.0` tag: its configuration keys differ.

## Step 1 — pick the GPU module, build the jar

The pom takes one **library** jar for the OS/arch, picked by a Maven profile (`cpu-windows-x86-64`
here), and optionally one **GPU module** jar of the same OS/arch. For a Windows x86-64 machine with an
Intel Core Ultra (Arc iGPU):

| Classifier | Size | Why |
|---|--:|---|
| `cpu-windows-x86-64` | 16.6 MiB | `jllama.dll`, llama/ggml and all CPU variants; ggml picks the best one at start (`alderlake` on a Core Ultra: AVX2 + AVX-VNNI, no AVX-512) |
| `vulkan-windows-x86-64` | 12.0 MiB | the iGPU (or any GPU) through the driver's `vulkan-1.dll`; without a working driver ggml skips it and runs on the CPU |

Other machines pick another GPU module: for example `cuda13-windows-x86-64` for an NVIDIA GPU (it
needs the CUDA 13 runtime on `PATH`). On Linux and macOS the same pom picks the library jar of that
OS. The full table is in the java-llama.cpp README, [Choosing the natives
jars](https://github.com/bernardladenthin/java-llama.cpp#choosing-the-natives-jars).
`openvino-*` is opt-in and can be slower than the CPU. Leave it out unless you want to experiment
with the NPU.

```bat
build.bat -Dgpu=vulkan-windows-x86-64
```

The result is `target\srcmorph.jar`. Without `-Dgpu=...` it holds the CPU natives only (20 MiB).

## Step 2 — the configuration, then run

Edit `srcmorph.yaml`: the configuration of the plugin's self-test, for the CLI. Its comments explain
every key. The ones to set:

- **`baseDirectory`, `outputDirectory`, `subtrees`, `fileExtensions`**: which files to index, and
  where the `.ai.md` files go. Write forward slashes (`C:/work/project`): in a double-quoted YAML
  string, `C:\x` is an escape.
- **`modelDirectory`** and the GGUF files of the three models under `aiDefinitions` (`modelPath`):
  gpt-oss-20b for every file up to ~275 KB, every package and the project overview, IBM Granite 4.0
  H-Tiny and H-1B for larger files. gpt-oss-20b takes about 12 GB for the model and up to 11 GB more
  when its 96K window is full; with less memory, lower its `contextSize`, or let the rules'
  `aiDefinitionKey` name `granite-4.0-h-tiny-mapreduce`, the fastest on a CPU in
  [the benchmark](ai-index-benchmark/COMPARISON.md).
- **`profileDirectory`**: the prompts and fact counters, one file per key that a rule names
  (`promptKey`, `factsKey`). The shipped `../../profiles/v3` is this repository's measured profile and
  is found as long as the example stays in the clone; copy the directory and point there to change a
  prompt.
- **`fieldGenerations`**: which prompt and model each file gets, by extension and size; the package
  and project summaries have rules of their own, `packageFieldGenerations` and
  `projectFieldGenerations`.

What a run does: for every matching file, srcmorph sends the prompt and the source to the model and
writes the answer next to the source's path under `outputDirectory`, as `<file>.ai.md`.

Run it in two passes:

1. With `command: Plan` (the shipped value), `run.bat` loads no model and writes nothing. It prints
   which file gets which model, and which files are larger than the model's window.
2. Then set `command: GenerateFileIndex`, or `All` to add package and project summaries, and run
   `run.bat` again.

A second run regenerates only what changed and what failed. A file fails when the model's answer
stays incomplete, empty or cut off at `maxOutputTokens`: nothing is written for it, the run names it
and ends with an error, and the next run tries it again.

## Check what loaded

The start of the run names the CPU variant, the backends and the devices:

```
load_backend: loaded Vulkan backend from <dir>\ggml-vulkan.dll
load_backend: loaded CPU backend from <dir>\ggml-cpu-alderlake.dll
load_backends_next_to_this_library: 3 backend(s) registered [RPC, Vulkan, CPU], 2 device(s): Vulkan0 (Vulkan), CPU (CPU)
[jllama] native backend 'cpu' loaded from <dir> with GPU module(s) vulkan; 18 file(s) in place for ggml to load
```

- **`with no GPU module`**: the jar was built without `-Dgpu=vulkan-windows-x86-64`.
- **Two Vulkan devices** (an iGPU and a discrete GPU): choose one with `devices: Vulkan1` in the
  `aiDefinition`.
- **`gpuLayers: 0`**, or `-Dnet.ladenthin.llama.backend=cpu` before `-jar` in `run.bat`: runs on the
  CPU only. That is the comparison to measure.

## Two settings worth measuring

- **`threads`**: the shipped file sets 8 per model; srcmorph's default is 4. A Core Ultra mixes P-,
  E- and slow LP-E-cores, and ggml waits for the slowest thread, so more threads can be slower: try 4
  against 8. Never set more than the machine has cores: an oversubscribed run is orders of magnitude
  slower, not a little.
- **`gpuLayers: 99` against `0`**: the iGPU shares memory and a 15 W budget with the CPU, so measure
  before deciding. `command: Calibrate` prints each model's prefill and decode speed.

## Tested

These runs were with 1.3.0 and its configuration. For 1.4.0, CI builds `examples/build-your-own-jar`
on every pull request (CPU natives only) and runs `Plan` with it.

On Windows, `cmd.exe`, srcmorph 1.3.0-SNAPSHOT, with the former `examples/windows-fatjar` (the same
CPU + Vulkan natives, fixed in the pom instead of chosen by profile and `-Dgpu`):
- `build.bat` ran on a JDK 8 (1.8.0_504) and produced a 32 MiB jar holding only the Windows x86-64
  natives.
- `Plan` ran on the same JDK 8.
- `run.bat` indexed 6 Java files with Qwen3-0.6B on the Vulkan module, in 17 s, on a JDK 21. The test
  machine has no JDK 8 installed.
- The test machine is a Ryzen with an AMD iGPU and an RTX 3070, which is why the log above was
  written down for a Core Ultra rather than copied from it.

With `examples/build-your-own-jar` and srcmorph 1.3.0 from Maven Central (2026-10-26, JDK 21, empty
local repository): `mvn package` picked `cpu-windows-x86-64` and gave a 20 MiB jar,
`-Dgpu=vulkan-windows-x86-64` a 32 MiB jar, and `Plan` ran.
