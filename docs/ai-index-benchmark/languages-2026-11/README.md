# Language and large-file runs, November 2026

The outputs behind the model card's finding 7 and the language prompts in
[`profiles/README.md`](../../../profiles/README.md). Every run directory holds `meta/<model>.json`
(the model definition as sent, phase times, cost) and `out/<model>/` (the `.ai.md` tree).

| Directory | Corpus | Profile | Runs |
|---|---|---|---|
| `languages-generic/run1..3` | [`corpus-languages/`](../corpus-languages/) | `profiles/v3` before the language prompts: every shell, Perl and XML file through `file-body-fallback` | gpt-oss-20b, GPT-6 Luna on OpenRouter; the "before" column of finding 7 |
| `languages/run1..6` | the same | `profiles/v3` with `file-body-shell`, `-perl`, `-xml`; runs 4 to 6 after the wording change in `file-body-perl` | gpt-oss-20b, GPT-6 Luna on OpenRouter; in run 6 gpt-oss's summary of `nightly_extract.ksh` has no body (the provider answered empty) |
| `benchmark/run1..3` | the model-card corpus | the same | gpt-oss-20b, GPT-6 Luna on OpenRouter, with the search measurement (`search.json`, `search.tsv`) |
| `large/run1..3` | the files of [`modelcard/large.py`](../modelcard/large.py) | the same, `--facts --oversize mapReduce --max-chunks 6` (then a sample of 6 chunks; today `--oversize sampleChunks --max-chunks 6`, and `mapReduce` reads every chunk, see [`remeasure-2026-11-01/`](../remeasure-2026-11-01/README.md)) | gpt-oss-20b (`--chars-per-token 2`) and GPT-6 Luna on OpenRouter; in `run1` also the two Granite presets, local |
| `large/granite-fallback/run1` | the same | `profiles/v3` without the language prompts (XML through `file-body-fallback`) | the two Granite presets, local |

The large corpus is not in the repository: `python large.py --out <dir>` writes it (seed 20261031,
1.6 MB SQL dump, 870 KB XML catalog, 283 KB Java enum) together with its expected facts. The runs
here were made with the large.py of that time, which wrote the facts into the corpus as
`<dir>/facts-large.json` (score a copy of the three source trees without it) and had no names in
the middle of the files; the current one writes `<dir>-facts.json` next to the directory and plants
a table, an XML element and an error-code area in each file's middle (`middle` in the facts). From
`modelcard/`, with a copy of the corpus as the work directory:

```bash
python score.py --work <copy of corpus-languages> --out ../languages-2026-11/languages/run1/out \
    --meta ../languages-2026-11/languages/run1/meta --facts facts-languages.json --scores <dir>
```

`--meta` names the run's own cut-offs (without it, those of the card's runs are read), `--scores` a
directory of its own (without it, the card's `scores/` is overwritten).

The quality figures of finding 7 and of `profiles/README.md` (per run and the mean, min and max of
each model) come from `repeat.py`, with a copy of `corpus-languages/other` as the work directory:

```bash
L=../languages-2026-11/languages
python repeat.py --work <copy> --facts facts-languages.json --scores <dir> \
    --runs $L/run1,$L/run2,$L/run3,$L/run4,$L/run5,$L/run6
```

(and `languages-generic/run1..3` for the generic prompt). The corpus has only other files, so the
score has two parts, (recall + format share) / 2, where the card's has four (Java, SQL and other
recall, format share). On 2026-11-05 it gave gpt-oss-20b 100, 98.5, 99.3, 98.9, 99.7 and 89.5 %
(mean 97.7) and 95.6, 96.1 and 95.3 % with the generic prompt (mean 95.7), GPT-6 Luna 99.9 and
99.6 % (each 99.3 to 100).
