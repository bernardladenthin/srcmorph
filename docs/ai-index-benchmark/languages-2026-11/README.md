# Language and large-file runs, November 2026

The outputs behind the model card's finding 7 and the language prompts in
[`profiles/README.md`](../../../profiles/README.md). Every run directory holds `meta/<model>.json`
(the model definition as sent, phase times, cost) and `out/<model>/` (the `.ai.md` tree).

| Directory | Corpus | Profile | Runs |
|---|---|---|---|
| `languages-generic/run1..3` | [`corpus-languages/`](../corpus-languages/) | `profiles/v3` before the language prompts: every shell, Perl and XML file through `file-body-fallback` | gpt-oss-20b, GPT-6 Luna on OpenRouter; the "before" column of finding 7 |
| `languages/run1..6` | the same | `profiles/v3` with `file-body-shell`, `-perl`, `-xml`; runs 4 to 6 after the wording change in `file-body-perl` | gpt-oss-20b, GPT-6 Luna on OpenRouter; in run 6 gpt-oss's summary of `nightly_extract.ksh` has no body (the provider answered empty) |
| `benchmark/run1..3` | the model-card corpus | the same | gpt-oss-20b, GPT-6 Luna on OpenRouter, with the search measurement (`search.json`, `search.tsv`) |
| `large/run1..3` | the files of [`modelcard/large.py`](../modelcard/large.py) | the same, `--facts --oversize mapReduce --max-chunks 6` | gpt-oss-20b (`--chars-per-token 2`) and GPT-6 Luna on OpenRouter; in `run1` also the two Granite presets, local |
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
    --facts facts-languages.json
```
