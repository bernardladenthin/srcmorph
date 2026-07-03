<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
SPDX-License-Identifier: Apache-2.0
-->

# Granite 4.0 H‑Small vs gpt‑oss‑20B — one model for everything?

A focused head‑to‑head between **IBM Granite 4.0 H‑Small** (Unsloth **UD‑Q4_K_XL**, hybrid
Mamba‑2 + sparse‑MoE, 32B total / ~9B active) and **OpenAI gpt‑oss‑20B** (UD‑Q4_K_XL, MoE,
~3.6B active) for the plugin's code‑summarization task, on **CPU and GPU**.

Motivating question: *can a single well‑configured Granite hybrid replace the two‑model setup
(gpt‑oss for precision, H‑Tiny for speed), given Mamba's long‑context advantage?*

## TL;DR verdict

- **Quality: a genuine peer.** On the identical 24‑file tree, H‑Small matches gpt‑oss's
  faithfulness and structure, with **zero** code‑fence or reasoning‑leak defects, and is
  **noticeably leaner** (it does not pad the large config file the way gpt‑oss does).
- **Speed: the "Granite is faster" assumption is *false* for H‑Small on this workload/hardware.**
  H‑Small activates ~9B params/token vs gpt‑oss's ~3.6B, so its **decode is ~4× slower on CPU**
  (5 vs 22 tok/s). Decode dominates summary *generation*, so H‑Small is slower overall.
- **The Mamba win is real but narrow:** H‑Small's **prefill is faster and stays flat at long
  context on GPU** (215 tok/s vs gpt‑oss 154→73), and its **KV/state memory is flat** (only 4 of
  40 layers grow a KV cache). Those advantages help *large inputs / long context*, not the
  decode‑bound generation of short summaries.
- **On an 8 GB GPU neither model fits** (H‑Small ~17 GB, gpt‑oss ~12 GB), so both are
  partial‑offload; that accelerates prefill but **not decode** → little real‑world gain for this task.
- **They cross at ~5,000 input tokens (~15–20 KB).** gpt‑oss prefills far faster on small inputs
  (~600 tok/s) then collapses (O(L²)); H‑Small is flat (~205) and overtakes above ~5k. This is the
  natural **size‑tier boundary** (§3b) — route small files to gpt‑oss, large files to a hybrid.
- **So:** H‑Small is a **quality‑parity, RAM‑light** option, **not** the fast one. The fast Granite
  remains **H‑Tiny (1B active)**. "One model for everything" only holds if you accept gpt‑oss‑class
  (or slower) speed in exchange for a single, permissive, long‑context‑cheap model.

## Setup

- **Scope:** `config` + `provider` subtree, **24 source files → 35 `.ai.md`** (24 file + 10 package
  + 1 project), prompt **v1**, context **16384**. Both models run on the *same current tree*
  (the older `ai__gptoss-20b__v1` cell was the stale 12‑file tree and is **not** comparable).
- **Hardware:** AMD Ryzen 7 5800H (8 threads) · NVIDIA RTX 3070 **8 GB** · `net.ladenthin:llama`
  5.0.4 (llama.cpp JNI), CUDA 13.3.
- **Params (from the research pass):**
  - H‑Small — **greedy** (`temperature=0`, `topP=1`, `topK=0`, `repeatPenalty=1`), **no thinking
    mode** (Granite 4.0 Instruct has none). IBM+Unsloth's official recommendation.
  - gpt‑oss — `temperature=0.7`, `topP=1`, `topK=0`, `minP=0.05`, `repeatPenalty=1`,
    **`reasoning_effort=low`** (the right setting for summarization; corroborated by COMPARISON.md §11).
- **Outputs:** `outputs/ai__granite4-h-small__v1/` and `outputs/ai__gptoss-20b__v1cur/`.

## 1. Quality (CPU, identical 24 files)

| Metric | **H‑Small UD‑Q4_K_XL** | **gpt‑oss‑20B (c16k)** |
|---|---|---|
| files summarized | 24 | 24 |
| avgLn (per file `.ai.md`) | 61.2 | 62.5 |
| KB (all 35 `.ai.md`) | 100 | 99 |
| stray code fences | **0** | **0** |
| reasoning/harmony leak | **0** | **0** |
| enum lines (trivial file) | 42 | 43 |
| **pojo lines (large config)** | **108** | **168** |

Both emit all 9 sections and the identical (deterministic) `<facts>` block. The one real
difference is verbosity on the large `AiGenerationConfig.java`: gpt‑oss elaborates to 168 lines,
H‑Small to 108 — **H‑Small is the more concise at equal faithfulness**. Neither hallucinated the
structural facts (those are prepended deterministically). A tighter (v2‑style) prompt would rein
in both.

## 2. Speed — CPU (16k context)

| CPU | prefill tok/s | decode tok/s | source |
|---|---|---|---|
| gpt‑oss‑20B (~3.6B active) | **42** | **22.2** | real 24‑file run (35 gens) |
| Granite H‑Small (~9B active) | 23 | **5.0** | calibrate (near‑window 13k) |

gpt‑oss is faster on **both** axes on CPU. Decode — the tokens‑out that dominate summary
generation — is **~4.4× faster** on gpt‑oss because it activates far fewer params per token, and
llama.cpp's hybrid/SSM decode kernels are not yet optimized (see ggml‑org/llama.cpp#16454).

## 3. Speed — GPU partial offload (8 GB, neither model fits)

| GPU (partial) | layers on GPU | prefill tok/s | decode tok/s |
|---|---|---|---|
| Granite H‑Small | 14 / 40 | **215** (flat) | 5 |
| gpt‑oss‑20B | 16 / 24 | 154 → **73** (degrades with ctx) | ~15–19 |

- **Prefill:** GPU offload helps H‑Small a lot (23 → 215) and, crucially, its prefill **stays flat**
  as context grows, whereas gpt‑oss's attention prefill **degrades** (154 → 73 from ~5k → ~10k
  tokens). This is the Mamba long‑context advantage made visible — but it needs the GPU to appear.
- **Decode:** unchanged for H‑Small (still 5 tok/s — the 26 CPU‑resident layers gate every token)
  and no better (arguably worse) for gpt‑oss than pure CPU. **Partial offload does not help the
  decode‑bound generation step on 8 GB.**

## 3b. Context scaling — where they cross (GPU prefill sweep)

Prefill throughput vs input length (GPU partial offload; raw data in
`tools/context-scaling-sweep.tsv`):

| input tokens | gpt‑oss prefill tok/s | H‑Small prefill tok/s | winner |
|---|---|---|---|
| ~1,000 | ~600 | ~150 | gpt‑oss (4×) |
| ~2,200 | 592 | ~185 | gpt‑oss |
| ~2,600 | 287 | ~198 | gpt‑oss |
| ~4,700 | 226 | 205 | ≈ tie |
| ~5,400 | 154 | 205 | H‑Small |
| ~9,700 | 73 | ~212 | H‑Small (3×) |
| ~13,000 | ~60 | 215 | H‑Small (3.5×) |

- gpt‑oss starts far **ahead** at small inputs (~600 tok/s) then **collapses** past ~2.5k tokens
  (O(L²) attention prefill). H‑Small is slow‑but‑**flat** (~205, Mamba O(L) prefill).
- **Prefill crossover ≈ 5,000 input tokens** (~15–20 KB Java, ~250–400 lines).
- **Total‑time crossover for summarization ≈ the same ~5k tokens:** decode time per summary is
  roughly equal despite gpt‑oss's ~4× faster decode *rate*, because `reasoning_effort=low` still
  emits ~4× more tokens (analysis channel) — so both spend ~110 s decoding a summary, and prefill
  is the tie‑breaker.

**Cure / sweet spot = the plugin's size‑tiered routing.** Cap gpt‑oss's input and route larger
files to the hybrid: `maxFileSizeBytes ≈ 15000` on the gpt‑oss `<fieldGeneration>`, larger files to
a Granite hybrid (H‑Small for quality, H‑Tiny for speed), and/or `<onOversize>` `mapReduce` to chunk.
The "cure" for gpt‑oss's long‑input collapse is simply **not feeding it long inputs**. (Crossover
value is quant/offload‑dependent on the 8 GB card; the *shape* — gpt‑oss high‑then‑collapsing vs
hybrid flat — is architectural. On CPU both rates are ~10× lower and gpt‑oss stays competitive
further out, but H‑Small's slow absolute speed makes it unattractive there regardless.)

## 4. Memory / long context (research‑established)

Only **4 of H‑Small's 40 layers** are attention (grow a KV cache with context); the other 36 are
Mamba‑2 with a **fixed‑size recurrent state**. llama.cpp implements this split
(`llama_memory_hybrid`), so H‑Small holds long context in far less RAM than a transformer whose KV
grows linearly (gpt‑oss mitigates this only via GQA + sliding‑window). This is the durable Granite
advantage for *very large single files* / long windows — orthogonal to the decode‑speed gap above.
(Sources: Mamba‑2 arXiv 2405.21060; IBM Granite 4.0 announcement; llama.cpp #13550/#16454/#19264.)

Caveat for this plugin: llama.cpp does **not** yet reuse a shared prompt‑prefix KV for recurrent
models (#19264), so the per‑file `cache_prompt` optimization (COMPARISON.md §10) does **not** help
H‑Small — each file recomputes the template prefix.

## 5. Answering the goal

**"Can Granite H‑Small be the single model for everything (with a good config)?"**

- **Yes, on quality and RAM** — it equals gpt‑oss's faithfulness, is leaner in output, permissive
  (Apache‑2.0), 128K‑capable, and cheap on memory at long context.
- **No, on speed** — on CPU it is ~2× slower prefill and ~4× slower decode than gpt‑oss; on an 8 GB
  GPU that doesn't change (decode stays CPU‑gated). It is **not** the fast option the Mamba
  reputation suggests; that role belongs to **H‑Tiny (1B active)**.
- **Where H‑Small would win outright:** a **≥24 GB GPU** (full offload → its flat‑state prefill and
  memory dominate at long context), or workloads dominated by **huge inputs** rather than long
  generation.

Net: H‑Small is a strong **precision + long‑context + permissive** single model if you can accept
gpt‑oss‑class (or slower) throughput; it is not a speed upgrade on 8 GB / CPU.

## Reproduce

Quality cells (both on the current tree, prompt v1, 16k):

```bash
mvn install -DskipTests            # install the plugin locally first
mvn -o prepare-package -P ai-index-selftest -DskipTests \
  -Dexp.model=EXP-Granite-4.0-H-Small \
  -Dexp.filePrompt=file-body-java -Dexp.pkgPrompt=package-body \
  -Dai.index.output.directory=docs/ai-index-benchmark/outputs/ai__granite4-h-small__v1
mvn -o prepare-package -P ai-index-selftest -DskipTests \
  -Dexp.model=gpt-oss-20B-c16k -Dai.gpuLayers=0 \
  -Dexp.filePrompt=file-body-java -Dexp.pkgPrompt=package-body \
  -Dai.index.output.directory=docs/ai-index-benchmark/outputs/ai__gptoss-20b__v1cur
```

Speed (exact prefill/decode tok/s) via a standalone calibrate POM parameterized by
`bench.model` / `bench.ctx` / `bench.ngl` / `bench.temp` / `bench.effort`, run with
`mvn ai-index:calibrate -f <pom>`; GPU adds `-Dbench.ngl=<N>
-Dllama.classifier=cuda13-windows-x86-64` with the CUDA toolkit on `PATH`. The `EXP-Granite-4.0-H-Small`
`<aiDefinition>` lives in the `ai-index-selftest` profile in `pom.xml`.

> Temporary experiment scaffold (not production config), analogous to the `claude2026_06_26_00`
> benchmark cells.
