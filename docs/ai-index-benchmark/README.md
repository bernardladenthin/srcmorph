# AI Index Benchmark — model & prompt comparison

This directory captures a controlled comparison of **local GGUF models** and **two prompt
versions** for the plugin's code-summarization task (`.ai.md` generation), run on CPU.

- **[COMPARISON.md](COMPARISON.md)** — scored results, per-model pros/cons, source deep-dive, recommendation.
- **[outputs/](outputs/)** — the raw generated `.ai.md` trees, one directory per cell.

## Bottom line

- **Best overall (and for large Java files): `Qwen3-Coder-30B-A3B-Instruct`** — most complete &
  faithful output, code-specialized, Apache-2.0, fast for its quality (~3.3B-active MoE, 262K ctx).
  The current production default is validated.
- **Fastest / best for very large or many files: `Granite-4.0-H-Tiny`** — ~4× faster on CPU
  (flat-KV hybrid, ~1B active), Apache-2.0, accept slightly lower fidelity.
- **Cleanest permissive small coder: `Seed-Coder-8B-Instruct`** (MIT).
- **Maximum precision, CPU time no object (one-off index of a large/important project):
  `gpt-oss-20b`** — the per-file *accuracy* leader (won 5/6 in the per-file matrix), most faithful,
  no hallucinated `final`/examples. It is the slowest (~2× the 30B, harmony reasoning overhead), so
  reserve it for precision runs, not bulk throughput.
- **Avoid:** `Qwen3.5-4B` (thinking tax, no quality gain) and `DeepSeek-Coder-V2-Lite` (copied the
  prompt's example as a summary).
- **Cross-model caveat:** all models mis-state structural facts (e.g. `final`, annotations, exact
  member sets); only prose sections are reliably model-added → a hybrid AST+AI design is the
  durable fix. Prompt **v2** helps verbosity but isn't shippable yet (code-fence regression on
  code models). Full detail: [COMPARISON.md](COMPARISON.md).

## What was tested

- **8 models × 2 prompts = 16 cells**, plus 1 full-project baseline (17 output trees).
- **Subtree scope:** `config` + `provider` packages (12 source files → 23 `.ai.md` each:
  12 file summaries + 10 package roll-ups + 1 project index). `package-info.java` /
  `module-info.java` excluded.
- **Context:** all experiment cells run at **16384** (CPU-feasible; native windows up to 262144
  are not). The baseline used 65536.
- **Hardware:** AMD Ryzen 7 5800H, 8 threads, CPU-only, via `net.ladenthin:llama` (llama.cpp JNI).

### Models (all Q4_K_M @ 16K ctx)

| Cell key | Model | Active params | Sampling |
|---|---|---|---|
| qwen35-4b | Qwen3.5-4B | 4B dense | t0.7 p0.8 k20 rep1.05 |
| qwen25coder-7b | Qwen2.5-Coder-7B | 7B dense | t0.7 p0.8 k20 rep1.05 |
| gptoss-20b | gpt-oss-20b | ~3.6B (MoE) | t1.0 p1.0 k0 |
| qwen3coder-30b | Qwen3-Coder-30B-A3B | ~3.3B (MoE) | t0.7 p0.8 k20 rep1.05 |
| deepseek-coder-v2-lite | DeepSeek-Coder-V2-Lite | 2.4B (MoE) | t0.3 p0.9 k40 rep1.05 |
| seed-coder-8b | Seed-Coder-8B-Instruct | 8B dense | t0.3 p0.9 k40 rep1.05 |
| granite4-h-tiny | Granite-4.0-H-Tiny | ~1B (MoE-hybrid) | t0.0 (greedy) |
| qwen3-4b-2507 | Qwen3-4B-Instruct-2507 | 4B dense | t0.7 p0.8 k20 rep1.05 |

### Prompts

- **v1** — the production prompts (`file-body-java`, `package-body`).
- **v2** — experimental (`file-body-java-v2`, `package-body-v2`): adds a trivial-file
  omission rule, JavaBean accessor grouping, an explicit "no invented keywords" ban,
  single-child package pass-through, and a tighter brevity instruction.

### Baseline

`outputs/ai__qwen3coder-30b__v1__fullproject-c64k/` is the earlier **full-project** run
(47 files, 65536 ctx, v1). Different scope/context — **not** directly comparable to the 16K
config+provider cells; kept for reference.

## Reproduce

The self-test profile is parameterized. Each cell was produced by:

```bash
mvn prepare-package -P ai-index-selftest -DskipTests \
  -Dexp.model=<EXP-key> \
  -Dexp.filePrompt=<file-body-java|file-body-java-v2> \
  -Dexp.pkgPrompt=<package-body|package-body-v2> \
  -Dai.index.output.directory=<output dir>
```

`EXP-*` model definitions, the v2 prompt templates, the `config`+`provider` subtree scope, and
the `package-info`/`module-info` excludes all live in the `ai-index-selftest` profile in
`pom.xml` (marked `claude2026_06_26_00`). The model files are catalogued in `X:\Modelle\index.md`.

> These are temporary experiment scaffolds on the `claude2026_06_26_00` branch, not production
> configuration.
