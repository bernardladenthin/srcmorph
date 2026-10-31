### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:31Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Operational command-line utilities for scheduled directory backup retention (Bash) and monthly revenue reporting from CSV exports (Python).

#### Purpose
- Standalone utilities that run from the shell in scheduled or manual operations.
- Provide two independent functional areas: backup rotation with retention pruning, and revenue aggregation/reporting.

#### Responsibilities
- Backup lifecycle (rotate_backups.sh): timestamped tar.gz creation, keep‑N retention, atomic archive finalization.
- Sales reporting (sales_report.py): CSV ingestion, column validation, currency filtering, month/region revenue aggregation, threshold‑based output.

#### Key units
- `rotate_backups.sh` — Bash CLI; validates args, archives SOURCE_DIR, prunes stale archives.
- `parse_orders(path, currency)` — generator yielding `Order` records from one CSV, skipping mismatched currency rows.
- `revenue_by_month_and_region(orders)` — aggregates `Decimal` revenue keyed by `(month, region)`, sorted for chronological output.
- `Order` — frozen dataclass carrying `order_date`, `region`, `amount`.
- `main(argv=None)` — Python entry point returning exit code.

#### Data flow
- Backup: `SOURCE_DIR` → tar with `-C` on parent → staging file `.partialial` → `mv` atomic finalization → `mapfile` from `ls -1t` (newest first) → delete archives beyond KEEP.
- Reporting: one or more CSV files → `parse_orders` (column check, currency filter, date/amount parsing) → `revenue_by_month_and_region` → `main` prints `month<TAB>region<TAB>revenue` for buckets ≥ min revenue.

#### Dependencies
- Backup: external binaries `tar`, `date`, `ls`, `du`, `cut`, `mv`, `rm`, `mkdir`, `basename`, `dirnamename; Bash builtins `mapfile`, `trap`, `[[ ]]`.
- Reporting: stdlib `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime.date`, `decimal.Decimal` / `decimal.InvalidOperation`.
- No internal collaboration between the two scripts.

#### Cross-cutting
- Both are CLI-only, self-contained, and return meaningful exit codes (`0` success, `1` no data/empty results, `2` argument validation failure).
- Strict up‑front validation: KEEP regex, SOURCE_DIR existence; required CSV columns, currency match.
- Error reporting goes to stderr with context: `trap EXIT` removes staging file; `ValueError` includes file path and line number.
- No shared types, configuration, or external package dependencies beyond the standard library and coreutils.
