### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:16Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Automates dated gzip backups of a directory with per-source retention, and produces currency-filtered monthly revenue reports per region from order CSV exports.

#### Purpose
- Snapshot a source directory into timestamped `.tar.gz` archives and prune old ones, keeping the newest N per source name (`rotate_backups.sh`).
- Aggregate order CSV exports into monthly revenue per region for one currency, with a minimum-revenue cut-off (`sales_report.py`).

#### Responsibilities
- Backup rotation: argument validation, UTC-timestamped tar creation via `.partial` then atomic `mv`, retention pruning by `ls -1t` ordering.
- Sales reporting: CSV ingestion with header validation, currency filtering, `Decimal` aggregation by (year-month, region), CLI output.
- Both units are standalone CLI entry points; no shared runtime code between them.

#### Key units
- `rotate_backups.sh` - Bash script; creates `<name>_<YYYYMMDDTHHMMSSZ>.tar.gz` and deletes archives beyond `keep`.
- `sales_report.py` - Python CLI module; exposes `main`, `parse_orders`, `revenue_by_month_and_region`.
- `Order` - frozen dataclass (`order_date`, `region`, `amount`) representing one CSV row.
- `parse_orders` - generator yielding `Order` for rows matching the requested currency; raises `ValueError` on bad header or row data.
- `revenue_by_month_and_region` - groups and sums amounts into `dict[tuple[str, str], Decimal]`, sorted.
- `main` (both units) - entry point; `main(argv)` returns exit code in Python, `rotate_backups.sh` script body in Bash.

#### Data flow
- Backup: source dir -> `tar -C parent -czf` into `.partial` -> `mv` to final archive -> `ls -1t` lists existing archives -> stale entries past `keep` removed.
- Sales: CSV files -> `parse_orders` (header check, currency filter, row conversion) -> flattened `Order` stream -> `revenue_by_month_and_region` -> rows filtered by `min_revenue` -> stdout report.

#### Dependencies
- Bash side: `tar` (gzip via `-z`), `date -u`, `du`, `ls`, `basename`, `dirname`, `mapfile` (bash 4+), `rm`, `mv`, `mkdir`.
- Python side: standard library only - `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Data: input order CSV exports requiring `order_date`, `region`, `amount`, `currency` columns; backup directory with `<name>_*.tar.gz` archives.

#### Cross-cutting
- Error handling: argument validation exits with code 2 in the shell script; Python raises `ValueError` (chained with `from e`, including `path:line_no`) and lets `OSError` propagate; `argparse` handles invalid CLI input.
- Exit codes: `0` success; `2` bad args (Bash); `1` empty result (Python).
- Safety: Bash uses `set -euo pipefail` and an EXIT trap removing `.partial`; atomic rename for archive creation.
- Known limits: `ls -1t` breaks on filenames containing newlines; zero-match listing aborts under pipefail before deletion.
- No currency conversion; amounts are displayed with two decimals in their original currency.
- Retention is scoped by source name, so other sources' archives in the same directory are untouched.
