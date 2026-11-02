<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Re-measurement, 2026-11-02

The runs of TODO item D3 after the map-reduce work and the second review round: gpt-oss-20b and
GPT-6 Luna three times each on the three corpora of [`remeasure-2026-11-01/`](../remeasure-2026-11-01/README.md),
gpt-oss-20b three more times on the large corpus with a larger output budget, and the large corpus
three times locally with the two Granite presets of the plugin's self-test, each with the XML prompt
and with the generic one. Every run directory holds `meta/<model>.json` and `out/<model>/`, the card
corpus's runs also `search.json` / `search.tsv`.

**What ran.** srcmorph at `530a439` (since `9c0289b`: a cut part summary is kept, a dense chunk is
split in halves, the map and reduce calls have prompts of their own, `map-chunk` and `reduce-parts`,
and name their input, the reduce reads the facts line, a local model's windows are counted in
tokens), `profiles/v3`, the tools of `modelcard/` at that commit. `local-before-review/` holds the
same three local runs with srcmorph at `c2d6834`, before the review fixes of `78d417d`. The meta
records the thin `srcmorph-cli` jar only, which is the same in both; the core jars were
`b629075263a8…` (`530a439`) and `bb6d7b3405d9…` (`c2d6834`, SHA-256 prefixes).

| Directory | Corpus | Options |
|---|---|---|
| `languages/run1..3` | [`corpus-languages/`](../corpus-languages/) | `--profile v3` |
| `benchmark/run1..3` | the model-card corpus | `--profile v3`, then `search.py` (reader Mistral Small 3.2) |
| `large/run1..3` | `large.py` (seed 20261031, with the names planted in the middle) | `--facts --oversize mapReduce`, gpt-oss-20b `--chars-per-token 2` |
| `large-16k/run1..3` | the same | gpt-oss-20b as above, `maxOutputTokens` 16384 instead of 4096 |
| `local/run1..3/granite-xml-prompt`, `granite-generic-prompt` | the same | the two Granite presets, `--facts --oversize mapReduce`, `profiles/v3` with and without `file-body-xml` |
| `local-before-review/run1..3/...` | the same | the same at `c2d6834` |
| `huge-xml/run1..3/granite-xml-prompt`, `granite-generic-prompt` | the catalog alone of `large.py --scale 1.3` (1.15 MB) | Granite H-1B as the self-test's `huge-xml` runs it: `--facts --oversize sampleChunks --max-chunks 6` |

Scored with today's `score.py` (`--meta` of each run; the large corpus with the
`<dir>-facts.json` that `large.py` writes next to it). Quality is the card's: the mean of the
kinds' recalls and the format share; the language corpus has only other files, so there it is the
mean of other-file recall and format.

## Hosted, OpenRouter

| | Run 1 / 2 / 3 | Mean | Reader hit@1 | s/file | USD per run (account delta) |
|---|---|---|---|---|---|
| gpt-oss-20b, language corpus | 98.9 / 99.3 / 99.0 % | 99.1 % | – | 3.1 to 4.5 | 0.001 to 0.002 |
| GPT-6 Luna, language corpus | 100 / 99.3 / 99.3 % | 99.5 % | – | 6.7 to 7.2 | 0.006 to 0.008 |
| gpt-oss-20b, card corpus | 100 / 99.7 / 100 % | 99.9 % | 93 / 95 / 95 % | 5.3 to 5.6 | 0.002 to 0.003 |
| GPT-6 Luna, card corpus | 100 / 100 / 100 % | 100 % | 86 / 91 / 86 % | 6.8 to 7.5 | 0.016 to 0.025 |
| gpt-oss-20b, large corpus | 59.7 / 69.1 / 96.9 % | 75.2 % | – | 30 to 55 | 0.004 to 0.019 |
| gpt-oss-20b, large corpus, 16384 output tokens | 39.7 / 66.0 / 47.2 % | 51.0 % | – | – | – |
| GPT-6 Luna, large corpus | 100 / 96.9 / 100 % | 99.0 % | – | 27 to 31 | 0.05 to 0.20 |

- **The small corpora stay at the ceiling** for both models, with no failed summary and a clean
  format.
- **The large corpus: Luna names every planted middle name in every run** (the dump's table and
  index, the catalog's `inv:recall`, the enum's `LEDGER_LOCK`). gpt-oss-20b now names the catalog's
  in every run (on 2026-11-01 in none), the enum's in none of the 4096-token runs and in one of the
  16384-token runs (`LEDGER_LOCK_02899`, run 2).
- **gpt-oss-20b's final answer for the SQL dump ran to the output limit** in runs 1 and 2
  (`finishReason` `length`), so the file was not written and scores as missing; run 3 wrote it with
  every expected name. With 16384 output tokens the dump's answer ran to the limit in all three
  runs and the catalog's in one: a longer budget is room for a longer runaway, not a finished
  answer. A single probe of the same reduce call at 16384 wrote a complete and correct summary, so
  the runaway is random. The preset keeps 4096; the next things to try are in TODO.md.
- **Cost.** The account delta of a run also books its package and project phases, and late. Luna's
  large runs cost 0.13 USD at list price with a cold cache and 0.016 USD with the prompt cached.

## Local, RTX 3070 Laptop, three runs each

Granite at temperature 0.2 gave nearly the same answers in every run; the per-file results below
were the same in all three. All four ran `--oversize mapReduce`, H-1B over its 16K windows too
(153 to 168 calls): the self-test's `huge-xml` samples six chunks instead, for files over 1 MB.
The 870 KB catalog falls into `big-xml` (275 KB to 1 MB), which is H-Tiny's map-reduce over 64K as
measured here. The quality moves between runs with the package and project phases: in
`local/run2/granite-generic-prompt` (H-1B) and `local/run3/granite-xml-prompt` (H-Tiny) the
package summary ran to the output limit, `AggregatePackages` exited 1 and the project phase did not
run.

| | Quality: run 1 / 2 / 3 | Enum | Catalog | Dump |
|---|---|---|---|---|
| Granite 4.0 H-Tiny, map-reduce over 64K, XML prompt | 71.0 / 64.2 / 64.5 % | 7 of 8 names | **7 of 8** | 3 of 11 |
| Granite 4.0 H-Tiny, map-reduce over 64K, generic prompt | 52.6 / 59.4 / 59.4 % | 7 of 8 | 4 of 8 | 3 of 11 |
| Granite 4.0 H-1B, map-reduce over 16K, XML prompt | 4.5 / 2.3 / 4.5 % | too short | 0 of 8 | 0 of 11 |
| Granite 4.0 H-1B, map-reduce over 16K, generic prompt | 17.9 / 15.6 / 17.9 % | too short | **5 of 8** | 0 of 11 |
| *before the review, `c2d6834`:* H-Tiny, XML / generic prompt | 40.7 / 36.6 % (means) | 5 to 7 of 8 | missing in 5 of 6 runs | 8 of 11, once missing |
| *before the review:* H-1B, XML / generic prompt | 7.9 / 19.3 % (means) | too short (once 1 of 8) | too short or 0 / 4 of 8 | 2 of 11 |

- **`big-xml` (H-Tiny) takes `file-body-xml`**, measured as it runs: the 870 KB catalog falls into
  it, and H-Tiny named 7 of its 8 names with the XML prompt against 4 with the generic one, in every
  run.
- **H-1B map-reducing every chunk is not how `huge-xml` runs it**, so the H-1B rows above did not
  decide that tier; the next section did.
- **Every source file is written now.** Before the review H-Tiny lost the catalog in five of six
  runs to an answer cut at the output budget; since a cut part summary is kept and a dense chunk is
  split, it wrote all three files in every run (the package summary failed twice, see above).
- **What stays weak.** Neither Granite preset names a planted middle name; both still invent
  names beside the dump's real ones (H-Tiny a table `ledger_category` and columns such as
  `category_id`, H-1B a table `transactions`), and H-Tiny's dump answer names fewer of the real ones
  than before the review (3 against 8 of 11). H-1B's enum answer is too short in
  every run.

## `huge-xml`: H-1B sampling a catalog over 1 MB, three runs each

The tier's own setup: the catalog of `large.py --scale 1.3` (1.15 MB, 39 chunks of 16K) alone,
H-1B with `sampleChunks` and six chunks, as `huge-xml` routes a file over 1 MB.

| | Quality: run 1 / 2 / 3 | Catalog | Calls | s (file phase) |
|---|---|---|---|---|
| Granite 4.0 H-1B, XML prompt | 16.2 / 16.2 / 16.2 % | written: one sentence, 1 of 8 names | 7 | 28 to 29 |
| Granite 4.0 H-1B, generic prompt | 0 / 0 / 0 % | not written: the final answer ran to 1536 tokens | 7 | 46 to 48 |

- **With the generic prompt the file fails in every run**: the reduce over the six part summaries
  runs to the preset's output limit (`finishReason` `length`), so nothing is written and the next
  run tries again. With the XML prompt H-1B writes one correct sentence ("a catalog of products,
  each with a unique SKU, title, price (in EUR), and stock quantity") under the exact facts line.
- So `huge-xml` takes `file-body-xml` too: a short, right summary is worth more than a file that
  fails in every run. Neither names the middle element.
