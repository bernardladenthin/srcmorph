<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: MIT
-->

# srcmorph on Windows: build your own fat jar, then run it

Two steps, four files, all in [`examples/windows-fatjar/`](../examples/windows-fatjar/):

| File | What it does |
|---|---|
| `pom.xml` | names srcmorph and **the llama natives this machine needs** (the classifiers) |
| `build.bat` | one `mvn package` on a JDK 8 → `target\srcmorph.jar` |
| `srcmorph.yaml` | what to index, with which prompt and which model |
| `run.bat` | `java -jar target\srcmorph.jar srcmorph.yaml` |

Needed on the machine: a JDK 8 or newer, Maven, and a GGUF model. Nothing else: no C++ toolchain and
no GPU SDK. The Vulkan module uses the GPU driver that is already installed.

Why build the jar yourself: the fat jar on the GitHub release carries the CPU natives of every
desktop platform (~111 MiB with Vulkan). Your own carries only the ones you name (32 MiB for the two
below), and you choose the GPU module.

> [!NOTE]
> This is for **srcmorph 1.3.0** (on `net.ladenthin:llama` 6.0.0); both resolve from Maven Central.

## Step 1 — pick the classifiers, build the jar

The pom declares one **library** jar for the OS/arch and, optionally, **GPU module** jars of the same
OS/arch. For a Windows x86-64 machine with an Intel Core Ultra (Arc iGPU), as shipped:

| Classifier | Size | Why |
|---|--:|---|
| `cpu-windows-x86-64` | 16.6 MiB | `jllama.dll`, llama/ggml and all CPU variants; ggml picks the best one at start (`alderlake` on a Core Ultra: AVX2 + AVX-VNNI, no AVX-512) |
| `vulkan-windows-x86-64` | 12.0 MiB | the iGPU (or any GPU) through the driver's `vulkan-1.dll`; without a working driver ggml skips it and runs on the CPU |

Other machines swap the classifiers: for example `cuda13-windows-x86-64` for an NVIDIA GPU (it needs
the CUDA 13 runtime on `PATH`), or `cpu-linux-x86-64` on Linux. The full table is in the java-llama.cpp
README, [Choosing the natives
jars](https://github.com/bernardladenthin/java-llama.cpp#choosing-the-natives-jars).
`openvino-*` is opt-in and can be slower than the CPU. Leave it out unless you want to experiment
with the NPU.

Set `JAVA_HOME` in `build.bat` to your JDK, then:

```bat
build.bat
```

The result is `target\srcmorph.jar`.

## Step 2 — the configuration, then run

Edit `srcmorph.yaml`. Its comments explain every key. The ones to set:

- **`baseDirectory`, `outputDirectory`, `subtrees`, `fileExtensions`**: which files to index, and
  where the `.ai.md` files go.
- **`modelDirectory`, `modelPath`**: the GGUF. A good start on a laptop is
  `granite-4.0-h-tiny-Q4_K_M.gguf`, the fastest on a CPU in [the benchmark](ai-index-benchmark/COMPARISON.md).
- **`promptDefinitions`**: the instruction to the model (the system prompt, **no placeholders**).
  srcmorph sends each file's name and source as the user message itself.
- **`fieldGenerations`**: which prompt and model each file gets. One `fallback` rule covers every file.

What a run does: for every matching file, srcmorph sends the prompt and the source to the model and
writes the answer next to the source's path under `outputDirectory`, as `<file>.ai.md`.

Run it in two passes:

1. With `command: Plan` (the shipped value), `run.bat` loads no model and writes nothing. It prints
   which file gets which model, and which files are larger than the model's window.
2. Then set `command: GenerateFileIndex`, or `All` to add package and project summaries, and run
   `run.bat` again.

A second run regenerates only three kinds of files: a changed file, a file whose settings changed,
and a file whose summary is empty. An empty summary usually means `maxOutputTokens` was too small for
the model.

## Check what loaded

The start of the run names the CPU variant, the backends and the devices:

```
load_backend: loaded Vulkan backend from <dir>\ggml-vulkan.dll
load_backend: loaded CPU backend from <dir>\ggml-cpu-alderlake.dll
load_backends_next_to_this_library: 3 backend(s) registered [RPC, Vulkan, CPU], 2 device(s): Vulkan0 (Vulkan), CPU (CPU)
[jllama] native backend 'cpu' loaded from <dir> with GPU module(s) vulkan; 18 file(s) in place for ggml to load
```

- **`with no GPU module`**: the Vulkan jar is not in the build.
- **Two Vulkan devices** (an iGPU and a discrete GPU): choose one with `devices: Vulkan1` in the
  `aiDefinition`.
- **`gpuLayers: 0`**, or `-Dnet.ladenthin.llama.backend=cpu` before `-jar` in `run.bat`: runs on the
  CPU only. That is the comparison to measure.

## Two settings worth measuring

- **`threads`**: 4 is the default and enough for these models. A Core Ultra mixes P-, E- and slow
  LP-E-cores, and ggml waits for the slowest thread, so more threads can be slower. Never set more
  than the machine has cores: an oversubscribed run is orders of magnitude slower, not a little.
- **`gpuLayers: 99` against `0`**: the iGPU shares memory and a 15 W budget with the CPU, so measure
  before deciding. `command: Calibrate` prints each model's prefill and decode speed.

## Tested

On Windows, `cmd.exe`, srcmorph 1.3.0-SNAPSHOT:
- `build.bat` ran on a JDK 8 (1.8.0_504) and produced a 32 MiB jar holding only the Windows x86-64
  natives.
- `Plan` ran on the same JDK 8.
- `run.bat` indexed 6 Java files with Qwen3-0.6B on the Vulkan module, in 17 s, on a JDK 21. The test
  machine has no JDK 8 installed.
- The test machine is a Ryzen with an AMD iGPU and an RTX 3070, which is why the log above was
  written down for a Core Ultra rather than copied from it.
