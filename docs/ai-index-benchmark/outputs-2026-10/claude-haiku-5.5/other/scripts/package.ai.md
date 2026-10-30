### other/scripts
- H: 1.0
- C: FAF6D678
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:12Z
- G: 
- A: 0.0.0
- X: package
- F: [rotate_backups.sh](rotate_backups.sh.ai.md)
- F: [sales_report.py](sales_report.py.ai.md)
---
> Bash and Python utilities that create timestamped backups with retention pruning and produce monthly per-region revenue reports from order CSV exports.

#### Purpose
- Archive a source directory into dated, gzipped tarballs and prune to the newest N.
- Aggregate order CSV exports into monthly revenue per region for a single currency, with a minimum revenue filter.

#### Responsibilities
- Backup management: `rotate_backups.sh` validates inputs, creates `<name>_<UTC timestamp>.tar.gz` via a `.partial` temp file and atomic `mv`, then removes archives beyond `KEEP` (default 7).
- Sales reporting: `sales_report.py` parses CSV rows into `Order` records, filters by currency, groups revenue by `(YYYY-MM, region)`, and prints sorted tab-separated lines.
- CLI entry points: both are command-line scripts with argument validation and exit codes (`0` success, `2` usage error in Bash; `0`/`1` in Python, `argparse` errors on bad arguments).

#### Key units
- `rotate_backups.sh`: top-level Bash script; creates archive, prunes old archives, EXIT trap for temp cleanup.
- `parse_orders`: generator yielding `Order` for the requested currency; validates required columns.
- `revenue_by_month_and_region`: sums `Decimal` amounts into a sorted dict keyed by month and region.
- `main`: Python entry point; combines files, applies `--min-revenue` filter, returns exit code.
- `Order`: frozen `dataclass` with `order_date`, `region`, `amount`.

#### Data flow
- Backup: source directory and backup directory args → `tar -czf` to `.partial` → `mv` to final archive → `ls -1t` lists archives → older entries removed.
- Sales: CSV files → `csv.DictReader` → `parse_orders` (currency filter, type conversion) → `Order` list → `revenue_by_month_and_region` → threshold filter → stdout report.

#### Dependencies
- Internal: none between the two files; they are independent utilities.
- External (Bash): `tar`, `date`, `ls`, `du`, `cut`, `rm`, `mv`, `mkdir`, `mapfile`, `trap`.
- External (Python): `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.

#### Cross-cutting
- Input validation with early exit: invalid `KEEP` or missing source directory exits 2 in Bash; missing CSV columns or invalid values raise `ValueError` in Python.
- Error context: Python `ValueError` includes `path:line` and chains the original error; Bash writes messages to stderr.
- Strict mode and cleanup: Bash uses `set -euo pipefail` with an EXIT `trap` removing partial archives.
- Unhandled failures: Python `OSError` on unreadable files is not caught; Bash `ls` with zero matches under `pipefail` may abort pruning.
- Configuration: CLI arguments only; no configuration files. Currency defaults to `EUR`; `KEEP` defaults to `7`.
