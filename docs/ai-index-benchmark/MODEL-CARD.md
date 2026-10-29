# Model card — srcmorph `.ai.md` summaries, October 2026

Which model to put behind srcmorph, measured on one fixed corpus: how much each model compacts
Java, SQL and other source files, how many of the names that matter it keeps, how many it makes up,
and how well it holds the `.ai.md` format. Hosted models (OpenRouter) and local GGUF models
(llama.cpp on a laptop GPU) ran the same three phases with the same prompts.

The June 2026 benchmark ([COMPARISON.md](COMPARISON.md)) compared local models on Java only; the
models marked ¹ below were in it.

<!-- assessment:begin -->
## Assessment

### Recommendations

- **Hosted, best overall: GPT-6 Luna.** The only model with full recall on both Java and SQL, all
  four seed counts, nothing invented to speak of, a clean format, and the fastest run (4.4 min for
  37 files) at 0.022 USD. This is the model to put behind an `openai-compatible` run.
- **Hosted, cheapest: gpt-oss-20b** (0.012 USD). Java recall is as good as Luna's and it compacts
  more (5.25 against 4.09), but it states none of the seed counts and its other-file summaries keep
  fewer names (87 %). The June precision pick holds up on the hosted route as well.
- **Local on an 8 GB GPU: Gemma 4 E4B with thinking off.** Full Java recall, no invented names, a
  clean format, 11 s per file. **Qwen3.5-4B with thinking off** is as fast (9.6 s), is the only small
  model that gets all four seed counts, but drops more names (Java 92 %, other files 68 %).
- **Local, when time does not matter: Granite 4.0 H-Small.** Full Java recall and a clean format,
  but 59 s per file.
- **Not worth their cost here.** Qwen3.8-27B is about as faithful as Luna, but costs 18 times as much
  (0.40 USD) and writes the longest summaries. Gemma 4 12B gets the seed counts right, but at 172 s
  per file: the Q4_K_M weights do not fit in 8 GB, so part of the model runs on the CPU.
- **Avoid for this task:**
  - **Granite 4.0 H-Tiny** starts 7 summaries with "Here is the markdown summary …", wraps 13 in a
    ```` ```markdown ```` fence (34 fence lines), writes 129 `#`–`###` headings, and calls the
    `AiGenerationProvider` interface a class.
  - **Seed-Coder-8B** is too terse: compaction 9.8 at a Java recall of 68 %.
  - **DeepSeek-Coder-V2-Lite** takes 317 s per file (16B weights on an 8 GB card) and writes `##`
    `#`–`###` headings.
  - **Qwen3-Coder-30B** describes column types (`char`, `integer`) that the seed-data file does not
    contain (17 invented per 100 on SQL) and drops names on Java (78 %).

### Findings

1. **Reasoning tokens count against `maxOutputTokens`.** Nemotron 3.5 Lightning and Qwen3.5-35B-A3B
   spend the output budget on reasoning even at `reasoning.effort` = `low`.
   - At 4096 tokens Nemotron left 15 of 24 file summaries empty.
   - At 16 384 tokens it still cut off 3 Java files, and Qwen3.5-35B cut off 5 (2 and 5 of them
     empty).
   - The rerun of Qwen3.5-35B cost 0.24 USD, ten times what GPT-6 Luna cost.

   A reasoning model needs a much larger budget here, or reasoning switched off, which was not
   measured.
2. **llama.cpp turns Gemma 4's thinking on by default.** With the stock chat template, Gemma 4 12B
   spent all 4096 tokens of every file on thinking and wrote no summary, at about 4.4 tokens/s.
   `chatTemplateEnableThinking: false` fixes it. With it, E4B also writes about half the tokens it
   wrote before (for example 620 instead of 1306 per file). So both Gemma 4 models and Qwen3.5-4B ran
   with thinking off. That is why Qwen3.5-4B no longer pays the "thinking tax" the June benchmark
   held against it.
3. **srcmorph keeps a cut-off summary as if it were done.** A cut-off generation produces only a WARN
   (`Generated text for … was cut off …`), and the truncated `.ai.md` counts as up to date. An
   incremental run regenerates it only after the source changes. An empty body is written as well
   (`AI provider returned empty body …`), but the next run retries it. For the OpenAI-compatible
   provider the cut-off warning is logged under the `LlamaCppJniProviderSupport` logger, which points
   at the wrong backend. Both are in [`TODO.md`](../../TODO.md).
4. **Format discipline depends on the model, not on the prompt.** All prompts are the v1 prompts, and
   eight of fourteen models follow them without a slip. The June code-fence regression of DeepSeek
   and Seed-Coder came with prompt v2 and does not show up with v1. Granite H-Tiny, which was clean in
   June, is not clean here.

### Compared with June 2026

June measured local models on CPU, Java only. This round adds SQL and other files, a GPU, and hosted
models.
- **gpt-oss-20b** stays the most faithful of the June set (Java recall 99.9 %).
- **Qwen3-Coder-30B-A3B**, June's throughput alternative, falls behind on recall and on invented
  names.
- **Granite H-Tiny**, June's high-throughput choice, fails on format.
- **Gemma 4** was not in June. With thinking off, E4B is the best local model on this hardware.
<!-- assessment:end -->

## Results

<!-- generated:begin -->
### Run overview

| Model | Backend | Files | Time (min) | s/file | Cost (USD) | Cut off |
|---|---|---|---|---|---|---|
| GPT-6 Luna | OpenRouter | 37 | 4.4 | 6.9 | 0.0224 | 0 |
| Gemma 4 31B it | OpenRouter | 37 | 25.0 | 37.0 | 0.0486 | 1 (`java/provider/AiCompletionParser.java`) |
| Nemotron 3.5 Lightning 30B-A3B | OpenRouter | 37 | 19.7 | 34.1 | 0.0459 | 3 (`java/config/SrcMorphConfiguration.java`, `java/provider/LlamaCppJniAiGenerationProvider.java`, `java/provider/LlamaCppJniConfig.java`) |
| Qwen3-Coder-30B-A3B-Instruct ¹ | OpenRouter | 37 | 17.7 | 34.1 | 0.0175 | 0 |
| Qwen3.5-35B-A3B | OpenRouter | 37 | 28.5 | 55.6 | 0.2371 | 5 (`java/config/AiFactExtractor.java`, `java/config/AiGenerationConfig.java`, `java/config/AiOversizeStrategy.java`, `java/config/SrcMorphConfiguration.java`, `java/provider/AiCompletionParser.java`) |
| Qwen3.8-27B | OpenRouter | 37 | 8.7 | 14.8 | 0.4030 | 0 |
| gpt-oss-20b ¹ | OpenRouter | 37 | 7.1 | 11.8 | 0.0122 | 0 |
| Qwen3-4B-Instruct-2507 ¹ | HF / nscale | not run | | | | |
| Qwen2.5-Coder-7B-Instruct ¹ | HF / nscale | not run | | | | |
| Gemma 4 12B it (Q4_K_M, thinking off) | local GGUF (RTX 3070) | 37 | 95.2 | 172.3 | 0 (local) | 0 |
| Gemma 4 E4B it (Q4_K_M, thinking off) | local GGUF (RTX 3070) | 37 | 6.3 | 11.1 | 0 (local) | 0 |
| Qwen3.5-4B (Q4_K_M, thinking off) ¹ | local GGUF (RTX 3070) | 37 | 6.1 | 9.6 | 0 (local) | 0 |
| Granite 4.0 H-Tiny (Q4_K_M) ¹ | local GGUF (RTX 3070) | 37 | 3.3 | 5.8 | 0 (local) | 1 (`sql/library/05_seed_data.sql`) |
| Granite 4.0 H-Small (UD-Q4_K_XL) ¹ | local GGUF (RTX 3070) | 37 | 34.9 | 58.9 | 0 (local) | 0 |
| Seed-Coder-8B-Instruct (Q4_K_M) ¹ | local GGUF (RTX 3070) | 37 | 19.6 | 34.4 | 0 (local) | 0 |
| DeepSeek-Coder-V2-Lite-Instruct (Q4_K_M) ¹ | local GGUF (RTX 3070) | 37 | 164.9 | 316.6 | 0 (local) | 1 (`java/config/AiGenerationConfig.java`) |

### Compaction and faithfulness per file kind

| Model | Java comp. | Java recall | SQL comp. | SQL recall | Seed counts | Other comp. | Other recall | Invented /100 |
|---|---|---|---|---|---|---|---|---|
| GPT-6 Luna | 4.09 | 100% | 4.29 | 100% | 4/4 | 0.82 | 96% | 0.4 |
| Gemma 4 31B it | 4.79 | 78% | 5.85 | 95% | 0/4 | 0.88 | 92% | 0.5 |
| Nemotron 3.5 Lightning 30B-A3B | 3.86 | 80% | 4.52 | 91% | 0/4 | 0.84 | 92% | 0.8 |
| Qwen3-Coder-30B-A3B-Instruct ¹ | 5.05 | 78% | 5.45 | 85% | 0/4 | 0.91 | 73% | 5.8 |
| Qwen3.5-35B-A3B | 4.66 | 49% | 5.06 | 96% | 4/4 | 0.96 | 79% | 0.6 |
| Qwen3.8-27B | 3.49 | 98% | 4.18 | 98% | 4/4 | 0.69 | 96% | 1.6 |
| gpt-oss-20b ¹ | 5.25 | 100% | 5.45 | 98% | 0/4 | 0.94 | 87% | 0.8 |
| Gemma 4 12B it (Q4_K_M, thinking off) | 4.22 | 88% | 5.72 | 95% | 4/4 | 0.90 | 85% | 0.2 |
| Gemma 4 E4B it (Q4_K_M, thinking off) | 4.08 | 100% | 5.36 | 94% | 0/4 | 0.91 | 78% | 0.0 |
| Qwen3.5-4B (Q4_K_M, thinking off) ¹ | 4.32 | 92% | 4.42 | 95% | 4/4 | 0.91 | 68% | 1.1 |
| Granite 4.0 H-Tiny (Q4_K_M) ¹ | 6.26 | 85% | 2.91 | 79% | 2/4 | 1.19 | 63% | 1.2 |
| Granite 4.0 H-Small (UD-Q4_K_XL) ¹ | 4.93 | 100% | 5.08 | 92% | 0/4 | 0.91 | 83% | 0.5 |
| Seed-Coder-8B-Instruct (Q4_K_M) ¹ | 9.83 | 68% | 6.63 | 81% | 0/4 | 1.40 | 61% | 0.6 |
| DeepSeek-Coder-V2-Lite-Instruct (Q4_K_M) ¹ | 3.60 | 96% | 4.29 | 91% | 0/4 | 0.98 | 64% | 0.5 |

### Format discipline (all 37 `.ai.md` per model)

| Model | No lead | `###` headings | Code fences | Reasoning leaks | Empty |
|---|---|---|---|---|---|
| GPT-6 Luna | 0 | 0 | 0 | 0 | 0 |
| Gemma 4 31B it | 1 | 0 | 0 | 0 | 0 |
| Nemotron 3.5 Lightning 30B-A3B | 0 | 0 | 0 | 0 | 2 |
| Qwen3-Coder-30B-A3B-Instruct ¹ | 0 | 0 | 0 | 0 | 0 |
| Qwen3.5-35B-A3B | 0 | 0 | 0 | 0 | 5 |
| Qwen3.8-27B | 0 | 0 | 0 | 0 | 0 |
| gpt-oss-20b ¹ | 0 | 0 | 0 | 0 | 0 |
| Gemma 4 12B it (Q4_K_M, thinking off) | 0 | 0 | 0 | 0 | 0 |
| Gemma 4 E4B it (Q4_K_M, thinking off) | 0 | 0 | 0 | 0 | 0 |
| Qwen3.5-4B (Q4_K_M, thinking off) ¹ | 0 | 0 | 0 | 0 | 0 |
| Granite 4.0 H-Tiny (Q4_K_M) ¹ | 20 | 129 | 34 | 0 | 0 |
| Granite 4.0 H-Small (UD-Q4_K_XL) ¹ | 0 | 0 | 0 | 0 | 0 |
| Seed-Coder-8B-Instruct (Q4_K_M) ¹ | 0 | 1 | 0 | 0 | 0 |
| DeepSeek-Coder-V2-Lite-Instruct (Q4_K_M) ¹ | 0 | 18 | 0 | 0 | 0 |

¹ also in the June 2026 benchmark ([COMPARISON.md](COMPARISON.md)).
<!-- generated:end -->

## Method

### Corpus (24 source files, 37 `.ai.md` per model)

| Kind | Files | Bytes | What it is |
|---|---|---|---|
| Java | 12 | 159 594 | `config/` and `provider/` of srcmorph itself, taken with `git archive` from `1e056c2` (`main` after PR #253) |
| SQL | 7 | 66 780 | [`corpus/sql/`](corpus/sql/): a PostgreSQL library schema in five files (tables + constraints, views + a materialized view, functions/procedure/trigger, a v2 migration, 54 KB of generated seed data), one MySQL file with a stored procedure, one T-SQL file with `MERGE` and `TRY/CATCH` |
| Other | 5 | 8 231 | [`corpus/other/`](corpus/other/): a C ring buffer (`.h` + `.c`), an OpenCL histogram kernel, a Python report script, a Bash backup-rotation script |

All fixtures are written for this benchmark (Apache-2.0); the seed data is produced by
[`tools/generate-seed-sql.py`](tools/generate-seed-sql.py) (deterministic, fixed seed) and holds
15 authors, 110 books, 50 members and 150 loans, so a summary can be checked for the exact counts.

### Phases and prompts

Every model ran the three CLI phases `GenerateFileIndex`, `AggregatePackages` and
`AggregateProject` with `force` on. File prompts are routed by extension: `.java` →
`file-body-java`, `.sql` → `file-body-sql`, everything else → `file-body-fallback`; the package
and project phases use `package-body` and `project-body`. These are the v1 prompts of the
plugin's self-test profile, copied into [`modelcard/prompts.json`](modelcard/prompts.json).

### Settings

| | OpenRouter | Local (llama.cpp JNI) |
|---|---|---|
| Context | 131 072 | 32 768 |
| `maxOutputTokens` | 4096 (16 384 for Nemotron 3.5 Lightning and Qwen3.5-35B-A3B, see below) | 4096 |
| Temperature | 0.2 | 0.2 (seed 42) |
| Reasoning | `reasoning.effort` = `low` where the model has it | thinking off where the template has a switch (Gemma 4, Qwen3.5) |
| Weights | whatever precision the OpenRouter route serves (not stated per request) | Q4_K_M (Granite H-Small UD-Q4_K_XL) |
| Hardware | — | RTX 3070 Laptop (8 GB), CUDA 13 natives of `net.ladenthin:llama` 6.0.0, layers auto-fitted, 8 CPU threads |

Everything is in [`modelcard/models.json`](modelcard/models.json); a model's `note` says why it
deviates from the common settings.

### Metrics ([`modelcard/score.py`](modelcard/score.py))

- **Compaction** — source bytes / `.ai.md` body bytes, per file kind, over the files that got a
  body. Higher is shorter. Below 1 means the summary is longer than the file.
- **Recall** — per file the share of expected names the body mentions as a whole word, averaged
  over the files of a kind. For Java the expected names are the public type and its
  public/protected methods, read from the source; for SQL and the other files they are listed in
  [`modelcard/facts.json`](modelcard/facts.json) (tables, columns, constraints, routines,
  functions, constants). An empty body scores 0.
- **Invented** — backticked identifiers in the body that occur nowhere in the source, per 100
  backticked identifiers (case-insensitive for SQL). A proxy for made-up names, not a fact check.
- **Seed counts** — how many of the four row counts of `05_seed_data.sql` the body states exactly.
- **Format** — bodies without the `>` lead line, `#`–`###` headings (the prompts allow `####` and
  below), code fences, leaked reasoning markers, empty or missing bodies.
- **Cut off** — files whose generation hit `maxOutputTokens` (the provider's warning in the log).
- **Cost** — the difference of OpenRouter's `total_usage` before and after a model's run.

[`modelcard/run.py`](modelcard/run.py) runs a model, [`modelcard/score.py`](modelcard/score.py)
writes [`scores/files.tsv`](modelcard/scores/files.tsv) (one row per model and file) and
[`scores/summary.json`](modelcard/scores/summary.json), and
[`modelcard/report.py`](modelcard/report.py) renders the tables above from them. The `.ai.md`
trees are in [`outputs-2026-10/`](outputs-2026-10/), one directory per model.

### Caveats

- **One run per model** at temperature 0.2: small differences are noise.
- **Hosted and local weights differ.** An OpenRouter route serves the weights its provider
  chose (often BF16 or FP8); the local models are 4-bit. A hosted model and a local one are not comparable on quality alone.
- **Recall and invented names are proxies.** A summary that names every method can still describe
  one wrongly, and one that paraphrases instead of naming scores lower than it deserves. The
  outputs are there to read.
- **The SQL and other fixtures are small** (1–3 KB, the seed data aside). Compaction on files that
  small mostly measures how much a model writes regardless of input; the Java files (up to 51 KB)
  say more about compaction.
- **Not measured:** MAI-Code-1.1-Flash (Microsoft) is only on Azure AI Foundry and GitHub Copilot,
  neither on OpenRouter nor on Hugging Face's router. The two Hugging Face models of
  `models.json` (Qwen3-4B-Instruct-2507, Qwen2.5-Coder-7B-Instruct) wait for inference credits.

### Earlier SQL measurements

SQL had been run before, in the oversize work (map-reduce over chunks, commits `2145a4f`,
`94deb32`, `8b952f5`, `edf98f2`, `a10b13c`, `799c8fe`, `16e0b7a`), with Granite 4.0 H-Micro and
H-1B only and measuring speed and robustness, not quality: a 1 MB SQL file took 16:39 min with
H-Micro and 8:27 min with H-1B on CPU (about 40 s on a GPU), and a 10 MB dump went through 278
chunks with a hierarchical reduce at about 2.8 characters per token. The exact whole-file counts
(`INSERT` rows, tables, views) come from the fact extractor there, not from the model; this
benchmark asks the model itself for the seed-data counts.
