# Language and large-file runs, November 2026

The outputs behind the model card's finding 7 and the language prompts in
[`profiles/README.md`](../../../profiles/README.md). Every run directory holds `meta/<model>.json`
(the model definition as sent, phase times, cost) and `out/<model>/` (the `.ai.md` tree).

| Directory | Corpus | Profile | Runs |
|---|---|---|---|
| `languages/run1..3` | [`corpus-languages/`](../corpus-languages/) | `profiles/v3` with `file-body-shell`, `-perl`, `-xml` | gpt-oss-20b, GPT-6 Luna on OpenRouter |
| `benchmark/run1..3` | the model-card corpus | the same | gpt-oss-20b, GPT-6 Luna on OpenRouter, with the search measurement (`search.json`, `search.tsv`) |
| `large/run1..3` | the files of [`modelcard/large.py`](../modelcard/large.py) | the same, `--facts --oversize mapReduce --max-chunks 6` | gpt-oss-20b (`--chars-per-token 2`) and GPT-6 Luna on OpenRouter; in `run1` also the two Granite presets, local |
| `large/granite-fallback/run1` | the same | `profiles/v3` without the language prompts (XML through `file-body-fallback`) | the two Granite presets, local |

The large corpus is not in the repository: `python large.py --out <dir>` writes it (seed 20261031,
1.6 MB SQL dump, 850 KB XML catalog, 276 KB Java enum) together with its expected facts. To score:

```bash
python score.py --work <prepared corpus dir> --out languages-2026-11/languages/run1/out \
    --facts facts-languages.json
```
